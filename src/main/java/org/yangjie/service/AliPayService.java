package org.yangjie.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.yangjie.util.HttpUtil;

/**
 * 支付宝支付
 * @author YangJie [2016年2月17日 下午7:08:32]
 */
@Service
public class AliPayService {
	
	private Logger logger = LoggerFactory.getLogger(AliPayService.class);
	
	/** 支付请求地址 */
	public static final String ALI_REQUEST_URL = "https://mapi.alipay.com/gateway.do";
	
	@Value("${config.domain}")
	private String domain;
	
	@Value("${account.ali.appid}")
	private String appid;
	@Value("${account.ali.secret}")
	private String secret;
	
	@Autowired
	private PayItemService payItemService;
	

	/**
	 * 获取支付宝支付url
	 * @author YangJie [2016年3月8日 下午2:46:53]
	 * @param paynum 支付号
	 * @param money 支付金额(分)
	 * @param subject 支付标题(用于在支付宝显示)
	 * @param isMobile 是否移动端
	 * @return
	 * @throws Exception 
	 */
	public String getPayUrl(String paynum, int money, String subject, boolean isMobile) throws Exception {
		String moneyF = String.valueOf(Float.parseFloat(String.valueOf(money)) / 100); // 金额转换为元
		Map<String, String> params = new HashMap<String ,String>();
		params.put("service", isMobile ? "alipay.wap.create.direct.pay.by.user" : "create_direct_pay_by_user");	// 接口名称
		params.put("partner", appid); 					// 合作者身份ID, 以2088开头的纯16位数字。
		params.put("out_trade_no", paynum);		// 商户订单号
		params.put("subject", subject);					// 商品名称
		params.put("total_fee", moneyF);				// 该笔订单的资金总额，单位为元。取值范围为[0.01，100000000.00]，精确到小数点后两位。
		params.put("payment_type", "1");				// 支付类型	(1代表商品购买)
		params.put("seller_id", appid);					// 卖家支付宝用户号, 以2088开头的纯16位数字。
		params.put("return_url", domain+"/alipay/return");	// 同步回调URL
		params.put("notify_url", domain+"/alipay/notify");	// 异步通知URL
		params.put("_input_charset", "UTF-8");			// URL 编码
		params.put("sign", getSign(params));		// 签名
		params.put("sign_type", "MD5");	// 加密方式
		return packUrl(params);	
	}

	/**
	 * 处理支付返回结果
	 * @author YangJie [2016年2月17日 下午8:57:55]
	 * @param resultMap
	 * @return
	 * @throws Throwable
	 */
	public boolean disposeResult(Map<String, String> resultMap, byte payFlag) {
		// 验证支付宝交易状态
		String tradeStatus = resultMap.get("trade_status"); // 支付宝交易状态
		if(!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)){
			logger.error("支付宝支付处理: 交易状态异常: {}", tradeStatus);
			return false;
		}
		// 验证签名
		String sign = resultMap.get("sign");
		if(sign==null || !sign.equals(this.getSign(resultMap))){
			logger.error("支付宝支付处理: 签名验证错误: {}", sign);
			return false;
		}
		// 验证通知ID
		String notifyid = resultMap.get("notify_id"); // 支付宝通知ID
		if(!this.checkNotify(notifyid)){
			logger.error("支付宝支付处理: 通知ID错误: {}", notifyid);
			return false;
		}
		// 完成支付
		String paynum = resultMap.get("out_trade_no"); // paynum
		String tradenum = resultMap.get("trade_no");  	// 支付宝交易号
		int totalFee = (int) (Float.parseFloat(resultMap.get("total_fee")) * 100);	// 实际支付金额
		return payItemService.finishPay(paynum, tradenum, totalFee, payFlag);
	}
	
	/**
	 * 封装请求url参数
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	private String packUrl(Map<String, String> params) throws Exception {
		List<String> keys = new ArrayList<String>(params.keySet());	//获取参数key
		StringBuffer prestr = new StringBuffer(ALI_REQUEST_URL).append("?");
	    String value;
	    for (String key : keys) {
			value = URLEncoder.encode(params.get(key), "UTF-8");
			prestr.append(key).append("=").append(value).append("&");
	    }
	    if(prestr.length() > 0){
	    	prestr.deleteCharAt(prestr.length() - 1);	               
	    }
	     return prestr.toString();
	} 
	
	/**
	 * 获取支付宝签名
	 * @author YangJie [2016年3月8日 下午2:49:24]
	 * @param params
	 * @return
	 */
	private String getSign(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);		// key 排序
        StringBuffer prestr = new StringBuffer(); 
        String value;
        for (String key : keys) {
        	value = params.get(key);
        	if (key!=null && !key.equalsIgnoreCase("sign") && !key.equalsIgnoreCase("sign_type")
        			&& value!=null && !value.isEmpty()){
        		prestr.append(key).append("=").append(value).append("&");
        	}
        }
        prestr.deleteCharAt(prestr.length() - 1); // 去除最后一个&
        prestr.append(secret); //  添加私钥  
        return DigestUtils.md5DigestAsHex(prestr.toString().getBytes());
    }
	
	/**
	 * 检测是否是支付宝请求
	 * @author YangJie [2016年3月8日 下午3:44:56]
	 * @param notifyid
	 * @return
	 */
	private boolean checkNotify(String notifyid) {
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(ALI_REQUEST_URL)
			.append("?service=notify_verify")
			.append("&partner=").append(appid)
			.append("&notify_id=").append(notifyid);
		String result = HttpUtil.get(urlBuffer.toString());
		return "true".equals(result);
	}
	
}