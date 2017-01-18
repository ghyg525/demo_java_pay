package org.yangjie.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yangjie.entity.PayItem;
import org.yangjie.service.AliPayService;
import org.yangjie.util.ServletUtil;


/**
 * 支付宝
 * @author YangJie [2017年1月13日 下午6:03:26]
 */
@Controller
@RequestMapping("/alipay")
public class AliPayController {
	
	private Logger logger = LoggerFactory.getLogger(AliPayController.class);
	
	@Autowired
	private AliPayService aliPayService;
	
	
	/**
	 * 支付宝同步回调
	 * @author YangJie [2016年2月18日 下午9:06:47]
	 * @param model
	 * @param paynum
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/return", method=RequestMethod.GET)
	public String retur(HttpServletRequest request) {
		Map<String, String> resultMap = ServletUtil.getParamMap(request);
		logger.info("收到支付宝同步回调: {}", resultMap);
		if (aliPayService.disposeResult(resultMap, PayItem.PAY_FLAG_RETURN)) {
			logger.info("支付宝同步回调处理成功!");
			return "redirect:/pay/"+resultMap.get("out_trade_no")+"/ok";
		}
		logger.error("支付宝同步回调处理失败!");
		return null;
	}
	
	/**
	 * 支付宝异步通知
	 * @author YangJie [2016年2月18日 下午9:06:47]
	 * @param model
	 * @param paynum
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/notify", method=RequestMethod.POST)
	public @ResponseBody String notify(HttpServletRequest request) {
		Map<String, String> resultMap = ServletUtil.getParamMap(request);
		logger.info("收到支付宝异步通知: {}", resultMap);
		if (aliPayService.disposeResult(resultMap, PayItem.PAY_FLAG_NOTIFY)) {
			logger.info("支付宝异步通知处理成功!");
			return "success";
		}
		logger.error("支付宝异步通知处理失败!");
		return null;
	}

}