package org.yangjie.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.yangjie.entity.PayItem;
import org.yangjie.service.PayItemService;


/**
 * 订单
 * @author YangJie [2016年3月8日 下午5:49:44]
 */
@Controller
@RequestMapping("/pay")
public class PayItemController{
	
	private Logger logger = LoggerFactory.getLogger(PayItemController.class);
	
	@Autowired
	private PayItemService payItemService;
	

	/**
	 * 发起支付
	 * @author YangJie [2017年1月16日 下午6:12:14]
	 * @param ordernum
	 * @param payType 支付方式(1微信/2支付宝)
	 * @param sitePreference
	 * @return
	 */
	@RequestMapping(value="/{paynum}", method=RequestMethod.GET)
	public String pay(@PathVariable String paynum, SitePreference sitePreference){
		logger.info("发起支付请求: {}", paynum);
		if (paynum!=null && !paynum.trim().isEmpty()) {
			String url = payItemService.sendPay(paynum, sitePreference.isMobile());
			return url==null ? null : "redirect:"+url;
		}
		return null;
	}
	
	/**
	 * 支付成功
	 * @author YangJie [2016年7月4日 下午2:54:24]
	 * @return
	 */
	@RequestMapping(value="/{paynum}/ok", method=RequestMethod.GET)
	public String payok(@PathVariable String paynum){
		PayItem payItem = payItemService.getPay(paynum);
		return payItem!=null && payItem.getPayStatus()==PayItem.PAY_STATUS_PAYED ? 
				"redirect:/order/"+payItem.getOrdernum()+"/ok" : null;
	}

}