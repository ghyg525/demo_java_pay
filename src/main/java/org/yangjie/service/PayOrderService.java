package org.yangjie.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yangjie.config.AppConfig;
import org.yangjie.config.AppSetting;
import org.yangjie.dao.PayOrderDao;
import org.yangjie.entity.PayOrder;
import org.yangjie.util.HttpUtil;
import org.yangjie.util.JsonUtil;
import org.yangjie.util.RandomUtil;


@Service
public class PayOrderService {
	
	private Logger logger = LoggerFactory.getLogger(PayOrderService.class);
	
	@Autowired
	private PayOrderDao payOrderDao;
	@Autowired
	private PayItemService payItemService;
	@Autowired
	private AppConfig appConfig;
	
	
	/**
	 * 获取订单
	 * @author YangJie [2016年5月5日 下午3:57:43]
	 * @param ordernum
	 * @return
	 */
	public PayOrder getOrder(String ordernum){
		return payOrderDao.select(ordernum);
	}
	
	/**
	 * 添加订单
	 * @author YangJie [2016年2月17日 下午6:36:57]
	 * @param orderKey 支付key(由支付服务为业务单独分配)
	 * @param orderMoney 支付金额(元)
	 * @return
	 */
	public String addOrder(String appkey, int money){
		// 验证入参合法性
		if (appkey==null || appkey.trim().isEmpty() || money<=0) {
			logger.error("添加订单失败: 非法出参: appkey={}, money={}", appkey, money);
			return null;
		}
		// 验证是否有此订单类型
		AppSetting appListSetting = appConfig.getConfig(appkey);
		if (appListSetting == null) {
			logger.error("添加订单失败: appkey不存在: {}", appkey);
			return null;
		}
		// 创建订单
		String ordernum = RandomUtil.getOrdernum();
		PayOrder payOrder = new PayOrder();
		payOrder.setAppkey(appkey);
		payOrder.setOrdernum(ordernum);
		payOrder.setOrderMoney(money * 100); // 元转分
		payOrder.setOrderTitle(appListSetting.getTitle()); // 订单描述
		payOrder.setOrderStatus(PayOrder.ORDER_STATUS_WAIT);
		payOrderDao.insert(payOrder);
		logger.info("添加订单成功: {}", ordernum);
		return ordernum;
	}
	
	/**
	 * 添加订单支付记录
	 * @author YangJie [2017年1月18日 上午10:41:21]
	 * @param ordernum
	 * @param payType
	 * @return
	 */
	public String addPay(String ordernum, byte payType){
		PayOrder payOrder = this.getOrder(ordernum);
		if (payOrder==null || payOrder.getOrderMoney()<=0) {
			logger.warn("订单支付: 订单不存在: {}", ordernum);
			return null;
		}
		int payMoney = payOrder.getOrderMoney();
		if (appConfig.getConfigByOrdernum(ordernum).isDebug()) {
			logger.info("当前类型订单状态为debug, 支付金额为1分: {}", ordernum);
			payMoney = 1; // debug阶段的订单支付金额为1分
		}
		return payItemService.addPay(ordernum, payMoney, payType);
	}
	
	/**
	 * 完成支付
	 * @author YangJie [2016年3月9日 上午9:33:40]
	 * @param ordernum
	 * @return
	 */
	public boolean finishOrder(String ordernum){
		// 更新订单状态
		payOrderDao.updateStatus(ordernum, PayOrder.ORDER_STATUS_PAYED);
		// 发送支付成功异步通知
		PayOrder order = payOrderDao.select(ordernum);
		AppSetting appSetting = appConfig.getConfig(order.getAppkey());
		if (appSetting!=null && appSetting.getNotifyUrl()!=null 
				&& !appSetting.getNotifyUrl().trim().isEmpty()) {
			String notifyUrl = appSetting.getNotifyUrl();
			if (notifyUrl.contains("{ordernum}")) { // rest地址
				notifyUrl = notifyUrl.replace("{ordernum}", ordernum);
			}else{ // param地址
				notifyUrl = notifyUrl + "?ordernum="+ordernum;
			}
			logger.info("订单支付完成, 异步通知业务地址: {}, 参数: {}", notifyUrl, JsonUtil.toJson(order));
			String result = HttpUtil.postJson(notifyUrl, JsonUtil.toJson(order));
			logger.info("支付业务端返回信息: {}", result);
			return "success".equals(result);
		}
		return true;
	}
	
	/**
	 * 获取支付成功回调url
	 * @author YangJie [2017年1月17日 下午6:27:39]
	 * @param ordernum
	 * @return
	 */
	public String getPayokUrl(String ordernum){
		String returnUrl = null;
		AppSetting appSetting = appConfig.getConfigByOrdernum(ordernum);
		if (appSetting!=null && appSetting.getReturnUrl()!=null 
				&& appSetting.getReturnUrl().startsWith("http")) {
			returnUrl = appSetting.getReturnUrl();
			if (returnUrl.contains("{ordernum}")) { // rest地址
				returnUrl = returnUrl.replace("{ordernum}", ordernum);
			}else{ // param地址
				returnUrl = returnUrl + "?ordernum="+ordernum;
			}
		}
		return returnUrl;
	}
	
}