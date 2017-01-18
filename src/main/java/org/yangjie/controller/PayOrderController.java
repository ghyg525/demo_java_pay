package org.yangjie.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yangjie.entity.PayOrder;
import org.yangjie.entity.PayOrderRequest;
import org.yangjie.entity.PayOrderResponse;
import org.yangjie.service.PayOrderService;
import org.yangjie.util.JsonUtil;


/**
 * 订单
 * @author YangJie [2016年3月8日 下午5:49:44]
 */
@Controller
@RequestMapping("/order")
public class PayOrderController{
	
	private Logger logger = LoggerFactory.getLogger(PayOrderController.class);
	
	@Autowired
	private PayOrderService payOrderService;
	
	
	/**
	 * 添加订单
	 * @author YangJie [2017年1月16日 下午4:58:01]
	 * @param paynum
	 * @return ordernum
	 */
	@RequestMapping(method=RequestMethod.POST)
	public @ResponseBody String addOrder(@RequestBody PayOrderRequest request){
		logger.info("添加订单请求: {}", JsonUtil.toJson(request));
		return payOrderService.addOrder(request.getAppkey(), request.getMoney());
	}
	
	/**
	 * 获取订单
	 * @author YangJie [2017年1月16日 下午4:57:53]
	 * @param paynum
	 * @return
	 */
	@RequestMapping(value="/{ordernum}", method=RequestMethod.GET)
	public @ResponseBody PayOrderResponse getOrder(@PathVariable String ordernum){
		PayOrderResponse response = null;
		if(ordernum!=null && !ordernum.trim().isEmpty()){
			PayOrder payOrder = payOrderService.getOrder(ordernum);
			if(payOrder!=null && payOrder.getOrdernum()!=null){
				response = new PayOrderResponse();
				BeanUtils.copyProperties(payOrder, response);
				response.setOrderMoney(payOrder.getOrderMoney()/100);
				response.setPayed(payOrder.getOrderStatus()==PayOrder.ORDER_STATUS_PAYED);
			}
		}
		logger.info("获取订单请求: {}, 返回结果: {}", ordernum, JsonUtil.toJson(response));
		return response;
	}
	
	/**
	 * 订单支付(默认打开选择支付方式页面)
	 * @author YangJie [2017年1月17日 下午2:51:10]
	 * @param ordernum
	 * @return
	 */
	@RequestMapping(value="/{ordernum}/pay", method=RequestMethod.GET)
	public String pre(@PathVariable String ordernum, SitePreference sitePreference){
		if(ordernum==null || ordernum.trim().isEmpty()) {
			logger.error("订单支付: 订单号非法: {}", ordernum);
			return null;
		}
		PayOrder payOrder = payOrderService.getOrder(ordernum);
		if (payOrder==null || payOrder.getOrderMoney()<=0) {
			logger.error("订单支付: 订单不存在: {}", ordernum);
			return null;
		}
		if(payOrder.getOrderStatus()==PayOrder.ORDER_STATUS_PAYED){
			logger.info("订单支付: 订单已经支付过: {}", ordernum);
			return "redirect:/order/"+ordernum+"/ok"; // 已经支付过
		}
		logger.info("订单支付: 返回选择支付方式页面: {}", ordernum);
		return sitePreference.isMobile() ? "/paypre_m.jsp" : "/paypre.jsp";
	}
	
	/**
	 * 选择支付方式
	 * @author YangJie [2017年1月16日 下午6:09:59]
	 * @param ordernum
	 * @param model
	 * @param sitePreference
	 * @return
	 */
	@RequestMapping(value="/{ordernum}/pay/{payType}", method=RequestMethod.GET)
	public String pay(@PathVariable String ordernum,
			@PathVariable byte payType, SitePreference sitePreference){
		logger.info("订单支付: {}, 支付类型: {}", ordernum, payType==1 ? "微信" : "支付宝");
		if (ordernum!=null && !ordernum.trim().isEmpty()) {
			String paynum = payOrderService.addPay(ordernum, payType);
			return paynum==null ? "/" : "redirect:/pay/"+paynum;
		}
		return null;
	}
	
	/**
	 * 支付成功
	 * @author YangJie [2016年7月4日 下午2:54:24]
	 * @return
	 */
	@RequestMapping(value="/{ordernum}/ok", method=RequestMethod.GET)
	public String payok(@PathVariable String ordernum, Model model, SitePreference sitePreference){
		PayOrder payOrder = payOrderService.getOrder(ordernum);
		if (payOrder!=null && payOrder.getOrderStatus() == PayOrder.ORDER_STATUS_PAYED) {
			String returnUrl = payOrderService.getPayokUrl(ordernum);
			if (returnUrl!=null && !returnUrl.trim().isEmpty()) {
				return "redirect:" + returnUrl; // 重定向到业务配置地址
			} // 未配置成功地址的返回默认
			model.addAttribute("payOrder", payOrder);
			return sitePreference.isMobile() ? "/payok_m.jsp" : "/payok.jsp";
		}
		return null;
	}
	
}