package org.yangjie.controller;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.site.SitePreference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yangjie.entity.JsApiPayConfig;
import org.yangjie.entity.PayItem;
import org.yangjie.service.PayItemService;
import org.yangjie.service.WxPayService;
import org.yangjie.util.QrcodeUtil;
import org.yangjie.util.ServletUtil;
import org.yangjie.util.XmlUtil;

@Controller
@RequestMapping("/wxpay")
public class WxPayController {
	
	private Logger logger = LoggerFactory.getLogger(WxPayController.class);
	
	@Autowired
	private WxPayService wxPayService;
	@Autowired
	private PayItemService payItemService;
	
	
	/**
	 * 微信支付
	 * 由微信公众号授权服务回调
	 * @author YangJie [2016年3月8日 下午6:03:53]
	 * @return
	 */
	@RequestMapping(value="/callback", method=RequestMethod.GET)
	public String callback(@RequestParam String openid, @RequestParam String state, 
			Model model, HttpServletRequest request, SitePreference sitePreference)  throws Exception {
		String paynum = state; // state为授权的paynum
		logger.info("收到微信支付网页授权回调: openid={}, paynum={}", openid, paynum);
		// 验证openid 和 paynum是否合法
		if (openid==null || openid.trim().isEmpty() || paynum==null || paynum.trim().isEmpty()) {
			logger.error("微信支付: 入参非法: openid={}, paynum={}", openid, paynum);
			return null;
		}
		PayItem payItem = payItemService.getPay(paynum);
		// 判断支付记录是否存在
		if (payItem==null || payItem.getId()<=0) {
			logger.error("微信支付: 支付记录不存在: {}", paynum);
			return null;
		}
		// 判断是否重复请求 (paynum已经支付过)
		if (payItem.getPayStatus() == PayItem.PAY_STATUS_PAYED) {
			logger.warn("微信支付: 支付记录已支付过: {}", paynum);
			return "redirect:/pay/"+paynum+"/ok";
		}
		if (sitePreference.isMobile()) { // 移动端执行js支付
			JsApiPayConfig jsApiPayConfig = wxPayService.sendPayJs(openid, paynum, ServletUtil.getRequestUrl(request));
			model.addAttribute("payConfig", jsApiPayConfig);
			model.addAttribute("paynum", paynum);
			return jsApiPayConfig==null ? null : "/wxpay_m.jsp";
		}else{ // pc端执行二维码支付
			String codeUrl = wxPayService.sendPayPc(openid, paynum);
			model.addAttribute("codeUrl", URLEncoder.encode(codeUrl, "utf8"));
			model.addAttribute("paynum", paynum);
			return codeUrl==null ? null : "/wxpay.jsp";
		}
	}
	
	/**
	 * 微信异步通知
	 * @author YangJie [2016年2月18日 下午9:06:47]
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/notify", method=RequestMethod.POST)
	public @ResponseBody String notify(@RequestBody String paramStr) throws Exception{
		Map<String, String> resultMap = XmlUtil.toObject(paramStr, HashMap.class, String.class, String.class);
		logger.info("收到微信支付异步通知: {}", resultMap);
		if (wxPayService.disposeResult(resultMap, PayItem.PAY_FLAG_NOTIFY)) {
			logger.info("微信支付异步通知处理成功!");
			return "success";
		}
		logger.info("微信支付异步通知处理失败!");
		return null;
	}
	
	/**
	 * 通过内容生成二维码
	 * @author YangJie [2016年5月27日 下午4:34:30]
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value="/code", method=RequestMethod.GET)
	public void code(@RequestParam(required=true) String codeUrl, 
			HttpServletResponse response) throws Exception{
		if (codeUrl!=null && !codeUrl.toString().trim().isEmpty()) {
			codeUrl = URLDecoder.decode(codeUrl, "utf8");
			OutputStream outputStream = response.getOutputStream();
			QrcodeUtil.create(codeUrl.toString(), outputStream);
			outputStream.flush();
			outputStream.close();
		}
	}
	
	/**
	 * 查询订单是否支付
	 * @author YangJie [2017年1月18日 下午2:57:57]
	 * @param paynum
	 * @throws Exception 
	 */
	@RequestMapping(value="/check/{paynum}", method=RequestMethod.GET)
	public @ResponseBody boolean check(@PathVariable("paynum") String paynum) throws Exception{
		return wxPayService.checkPay(paynum);
	}
	
}