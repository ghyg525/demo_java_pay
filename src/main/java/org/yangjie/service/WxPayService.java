package org.yangjie.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yangjie.config.AppConfig;
import org.yangjie.entity.JsApiConfig;
import org.yangjie.entity.JsApiPayConfig;
import org.yangjie.entity.PayItem;
import org.yangjie.util.HttpUtil;
import org.yangjie.util.JsonUtil;
import org.yangjie.util.RandomUtil;
import org.yangjie.util.XmlUtil;


/**
 * 微信支付
 * @author YangJie [2016年2月17日 下午7:08:32]
 */
@Service
public class WxPayService {
	
	private Logger logger = LoggerFactory.getLogger(WxPayService.class);
	
	/** 微信接口 统一下单 */
	public static final String WX_API_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	/** 微信接口 查询订单 */
	public static final String WX_API_ORDERQUERY = "https://api.mch.weixin.qq.com/pay/orderquery";
	
	@Value("${config.domain}")
	private String domain;
	@Value("${config.authurl}")
	private String authurl;
	@Value("${config.authkey}")
	private String authkey;
	
	@Value("${account.wx.appid}")
	private String appid;
	@Value("${account.wx.secret}")
	private String secret;
	@Value("${account.wx.mchid}")
	private String mchid;
	@Value("${account.wx.mchkey}")
	private String mchkey;
	
	@Autowired
	private PayItemService payItemService;
	@Autowired
	private AppConfig appConfig;
	
	
	/**
	 * 获取微信授权地址
	 * 跳转到moko_weixin服务统一授权 by YangJie 2016-05-04
	 * @author YangJie [2016年3月9日 下午7:05:40]
	 * @return
	 */
	public String authUrl(String paynum){
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(authurl).append("/auth?appkey=").append(authkey)
			.append("&state=").append(paynum);
		return urlBuilder.toString();
	}
	
	/**
	 * 微信支付 二维码支付
	 * @author YangJie [2017年1月18日 下午2:26:50]
	 * @param openid
	 * @param paynum
	 * @return 支付二维码内容
	 */
	public String sendPayPc(String openid, String paynum){
		logger.info("微信支付: 调用二维码支付: {}", paynum);
		int money = payItemService.getPay(paynum).getPayMoney();
		String title = appConfig.getConfigByPaynum(paynum).getTitle();
		return this.apiUnifiedOrder(paynum, money, title, openid, false);
	}
	
	/**
	 * 微信支付
	 * @author YangJie [2016年3月8日 下午5:15:38]
	 * @param openid
	 * @param paynum
	 * @param url 当前页面url
	 * @param isMobile
	 * @return
	 */
	public JsApiPayConfig sendPayJs(String openid, String paynum, String url){
		int money = payItemService.getPay(paynum).getPayMoney();
		String title = appConfig.getConfigByPaynum(paynum).getTitle();
		String prepayid =  this.apiUnifiedOrder(paynum, money, title, openid, true);
		if (prepayid!=null && !prepayid.trim().isEmpty()) {
			// 微信js支付参数
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("appId", appid);
			paramMap.put("nonceStr", getNonceStr());
			paramMap.put("timeStamp", getTimeStamp());
			paramMap.put("package", "prepay_id=" + prepayid);
			paramMap.put("signType", "MD5");
			paramMap.put("paySign", getSign(paramMap));
			paramMap.put("packageStr", paramMap.get("package")); // 解决java中package关键字问题
			JsApiPayConfig jsApiPayConfig = JsonUtil.toObject(JsonUtil.toJson(paramMap), JsApiPayConfig.class);
			// 微信jsapi初始化参数
			String result = HttpUtil.get(authurl + "/jsapi/config");
			logger.info("调用微信授权服务: 获取jsapi初始化参数: {}", result);
			jsApiPayConfig.setInitConfig(JsonUtil.toObject(result, JsApiConfig.class));
			logger.info("返回微信js支付需要的参数信息: {}", JsonUtil.toJson(jsApiPayConfig));
			return jsApiPayConfig;
		}
		return null;
	}
	
	/**
	 * 微信api统一下单
	 * @param paynum 交易流水号
	 * @param money 支付金额/分
	 * @param body 商品描述
	 * @param detail 商品详情
	 * @return
	 * @author YangJie
	 * @createTime 2015年3月26日 下午2:34:46
	 */
	public String apiUnifiedOrder(String paynum, int money, String body, String openid, boolean isMobile) {
		Map<String, String> paramMap = new HashMap<String ,String>();
		paramMap.put("appid", appid);		// 公众账号ID
		paramMap.put("mch_id", mchid);	// 商户号
		paramMap.put("nonce_str", getNonceStr());				// 随机字符串
		paramMap.put("out_trade_no", paynum);					// 商户订单号
		paramMap.put("body", body);										// 商品描述
		paramMap.put("total_fee", String.valueOf(money));		// 支付金额, 单位为【分】
		paramMap.put("spbill_create_ip", "127.0.0.1"); 			// 终端IP, APP和网页支付提交用户端ip
		paramMap.put("notify_url", domain+"/wxpay/notify");		// 通知地址
		paramMap.put("trade_type", isMobile ? "JSAPI" : "NATIVE");	// 交易类型, 取值如下：JSAPI，NATIVE，APP
		if (isMobile) {
			paramMap.put("openid", openid);	// 用户标识, trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。
		}
		paramMap.put("sign", getSign(paramMap));	// 签名
		String xmlParamString = mapToXml(paramMap);			// 封装xml格式参数字符串
		logger.info("微信统一下单api请求参数: {}", xmlParamString);
		String result = HttpUtil.postXml(WX_API_ORDER, xmlParamString);
		logger.info("微信统一下单api返回结果: {}", result);
		HashMap<String, String> resultMap = XmlUtil.toObject(result, HashMap.class, String.class, String.class);
		if ("SUCCESS".equals(resultMap.get("return_code"))) {
			return isMobile ? resultMap.get("prepay_id") : resultMap.get("code_url");
		}
		logger.error("微信统一下单api调用失败: {}", resultMap);
		return null;
	}
	
	/**
	 * 处理微信支付返回结果
	 * @param resultMap
	 * @return
	 * @author YangJie
	 * @throws Throwable 
	 * @createTime 2015年3月31日 下午5:36:27
	 */
	public boolean disposeResult(Map<String, String> resultMap, byte payFlag) {
		// 判断通信标识
		if (!"SUCCESS".equals(resultMap.get("return_code"))) {
			logger.error("微信支付处理: 通信标识异常: {}", resultMap.get("return_code"));
			return false;
		}
		// 验证签名
		if (!getSign(resultMap).equals(resultMap.get("sign"))) {
			logger.error("微信支付处理: 签名验证失败: {}", resultMap.get("sign"));
			return false;
		}
		// 判断交易状态
		if (!"SUCCESS".equals(resultMap.get("result_code"))) {
			logger.error("微信支付处理: 交易状态异常: {}", resultMap.get("result_code"));
			return false;
		}
		// 完成支付状态
		String paynum = resultMap.get("out_trade_no");
		String tradenum = resultMap.get("transaction_id"); // 微信支付订单号
		int totalFee = Integer.parseInt(resultMap.get("total_fee").toString());	// 实际支付金额
		return payItemService.finishPay(paynum, tradenum, totalFee, payFlag);
	}
	
	/**
	 * 通过第三方api确认支付情况
	 * @author YangJie [2016年7月18日 下午12:50:56]
	 * @return
	 * @throws Exception 
	 */
	public boolean checkPay(String paynum) throws Exception{
		PayItem payItem = payItemService.getPay(paynum);
		if (payItem == null) {
			logger.error("微信支付查询: 当前订单不存在: {}", paynum);
			return false;
		}
		if (payItem!=null && payItem.getPayStatus()==PayItem.PAY_STATUS_PAYED) {
			logger.info("微信支付查询: 当前订单状态已支付: {}", paynum);
			return true; // 如果订单已支付, 直接返回true
		}
		// 订单未支付, 通过api查询并处理
		Map<String, String> resultMap = this.apiOrderQuery(paynum);
		logger.info("微信支付查询: 开始处理主动查询结果: {}", resultMap);
		if(!"SUCCESS".equals(resultMap.get("trade_state"))){
			logger.info("微信支付查询: 订单未支付: trade_state={}", resultMap.get("trade_state"));
			return false;
		}
		return this.disposeResult(resultMap, PayItem.PAY_FLAG_QUERY);
	}
	
	/**
	 * 通过api查询订单
	 * @author YangJie [2016年2月19日 下午3:35:50]
	 * @param paynum
	 * @return
	 * @throws Exception 
	 * @throws Throwable
	 */
	public Map<String, String> apiOrderQuery(String paynum) throws Exception {
		Map<String, String> paramMap = new HashMap<String ,String>();
		paramMap.put("appid", appid);				// 公众账号ID
		paramMap.put("mch_id", mchid);			// 商户号
		paramMap.put("nonce_str", getNonceStr());		// 随机字符串
		paramMap.put("out_trade_no", paynum);			// 商户订单号
		paramMap.put("sign", getSign(paramMap));	// 签名
		// 封装xml格式参数字符串
		String xmlParamString = mapToXml(paramMap);
		logger.info("微信订单查询api请求参数: {}", xmlParamString);
		String result = HttpUtil.postXml(WX_API_ORDERQUERY, xmlParamString);
		logger.info("微信订单查询api返回结果: {}", result);
		Map<String, String> resultMap = XmlUtil.toObject(result, HashMap.class, String.class, String.class);
		return resultMap;
	}
	
	
	/**
	 * 获取随机字符串
	 * @return 32字符以内
	 * @author YangJie
	 * @createTime 2015年3月26日 下午5:12:47
	 */
	private String getNonceStr(){
		return RandomUtil.getRandomStr(32);
	}
	
	/**
	 * 获取时间戳
	 * @return 精确到秒
	 * @author YangJie
	 * @createTime 2015年3月26日 下午5:12:47
	 */
	private String getTimeStamp(){
		return String.valueOf(System.currentTimeMillis()/1000);
	}
	
	/**
	 * 获取微信认证签名(md5)
	 * @param paramMap
	 * @author YangJie
	 * @createTime 2015年3月26日 下午2:48:17
	 */
	private String getSign(Map<String, String> paramMap) {
        List<String> keyList = new ArrayList<String>(paramMap.keySet());	// 获取参数key
        Collections.sort(keyList);	// key 排序
        StringBuffer paramBuffer = new StringBuffer();
        String value = null;
        for (String key : keyList) {	// 循环封装非空参数
        	value = paramMap.get(key);
        	if (key != null && !key.equals("sign") && value!=null && !value.isEmpty()) {
        		paramBuffer.append(key).append("=").append(value).append("&");
        	}
        } // 添加私钥
        paramBuffer.append("key=").append(mchkey);   
        return DigestUtils.md5Hex(paramBuffer.toString()).toUpperCase();
    }
	
	/**
	 * 获取xml格式参数字符串
	 * @param paramMap
	 * @return
	 * @author YangJie
	 * @createTime 2015年3月26日 下午3:06:26
	 */
	private String mapToXml(Map<String, String> paramMap) {
		StringBuilder xmlBuilder = new StringBuilder("<xml>");
		for (String key : paramMap.keySet()) {
			if (key != null && paramMap.get(key)!=null && !paramMap.get(key).isEmpty()) {
				xmlBuilder.append("<").append(key).append(">").append(paramMap.get(key))
					.append("</").append(key).append(">");
			}
		}
		xmlBuilder.append("</xml>");
		return xmlBuilder.toString();
	}
	
}
