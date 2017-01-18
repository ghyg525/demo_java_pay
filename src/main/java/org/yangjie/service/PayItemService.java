package org.yangjie.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yangjie.dao.PayItemDao;
import org.yangjie.entity.PayItem;
import org.yangjie.util.RandomUtil;

@Service
public class PayItemService {
	
	private Logger logger = LoggerFactory.getLogger(PayItemService.class);
	
	@Autowired
	private PayItemDao payItemDao;
	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private WxPayService wxPayService;
	@Autowired
	private AliPayService aliPayService;
	
	
	/**
	 * 获取支付实体
	 * @author YangJie [2016年7月1日 下午4:01:07]
	 * @param paynum
	 * @return
	 */
	public PayItem getPay(String paynum){
		return payItemDao.select(paynum);
	}
	
	/**
	 * 添加支付
	 * @author YangJie [2016年2月17日 下午6:36:57]
	 * @param ordernum 订单号
	 * @param payMoney 支付金额(分)
	 * @param payType 支付类型
	 * @return 返回支付跳转地址
	 */
	public String addPay(String ordernum, int payMoney, byte payType){
		String paynum = RandomUtil.getPaynum();
		PayItem payItem = new PayItem();
		payItem.setPaynum(paynum);
		payItem.setOrdernum(ordernum);
		payItem.setPayType(payType);
		payItem.setPayMoney(payMoney);
		payItem.setPayStatus(PayItem.PAY_STATUS_WAIT);
		payItemDao.insert(payItem);
		logger.info("创建支付记录成功: {}, {}", ordernum, paynum);
		return paynum;
	}
	
	/**
	 * 发起支付
	 * @author YangJie [2017年1月16日 下午6:16:35]
	 * @param paynum 支付号
	 * @param isMobile 是否移动端页面
	 * @return
	 */
	public String sendPay(String paynum, boolean isMobile){
		String returnUrl = null;
		PayItem payItem = this.getPay(paynum);
		if (payItem==null || payItem.getPayMoney()<=0) {
			logger.error("发起支付: 支付记录不存在: {}", paynum);
			return returnUrl;
		}
		switch (payItem.getPayType()) {
		case PayItem.PAY_TYPE_WX: // 微信支付
			if (isMobile) {
				returnUrl = wxPayService.authUrl(paynum);
				logger.info("移动端微信支付, 返回微信网页授权地址: {}", returnUrl);
			}else{
				returnUrl = "/wxpay/callback?openid=pc&state="+paynum;
				logger.info("PC端微信支付, 返回模拟网页授权回调地址: {}", returnUrl);
			}
			break;
		case PayItem.PAY_TYPE_ALI: // 支付宝
			try {
				String title = payOrderService.getOrder(payItem.getOrdernum()).getOrderTitle();
				returnUrl = aliPayService.getPayUrl(paynum, payItem.getPayMoney(), title, isMobile);
				logger.info("支付宝支付, 返回支付地址: {}", returnUrl);
			} catch (Exception e) {
				logger.error("支付宝支付, 获取支付地址失败!", e);
			}
			break;
		}
		return returnUrl;
	}
	
	/**
	 * 完成支付
	 * 验证订单信息 + 更新支付状态 + 更新订单状态(包括支付方式)
	 * @author YangJie [2016年2月17日 下午6:38:39]
	 * @param paynum
	 * @param tradenum 第三方交易号(由第三方通知返回)
	 * @param payMoney 实际支付金额(分)
	 * @param payFlag 支付成功标记(1异步通知/2同步通知/3主动查询/4对账)
	 * @return
	 */
	public boolean finishPay(String paynum, String tradenum, int payMoney, byte payFlag){
		// 核对交易信息
		PayItem payItem = payItemDao.select(paynum);
		// 无支付记录
		if (payItem == null || payItem.getPaynum()==null) {
			logger.error("支付结果处理: 无支付记录, 注意测试环境和正式环境区分: {}", paynum);
			return false;
		}
		// 已经处理过
		if (payItem.getPayStatus().equals(PayItem.PAY_STATUS_PAYED)) {
			logger.warn("支付结果处理: 已经处理过, 收到重复通知: {}", paynum);
			return true;
		}
		// 核对金额
		if (!payItem.getPayMoney().equals(payMoney)) {
			logger.error("支付结果处理: 金额异常: {}, 应付{}分, 实付{}分", paynum, payItem.getPayMoney(), payMoney);
			return false;
		}
		// 更新支付状态
		payItemDao.updateStatus(paynum, tradenum, PayItem.PAY_STATUS_PAYED, payFlag);
		logger.info("支付处理成功, 状态更新为已支付: {}", paynum);
		// 更新订单
		return payOrderService.finishOrder(payItem.getOrdernum());
	}
	
}