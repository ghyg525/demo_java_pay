package org.yangjie.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yangjie.util.HttpUtil;
import org.yangjie.util.HttpsUtil;
import org.yangjie.util.RandomUtil;
import org.yangjie.util.XmlUtil;

/**
 * 微信退款
 * 只有核心方法, 非完整实例
 * @author YangJie [2016年5月17日 下午6:24:44]
 */
@Service
public class WxRefundService {
	
	/** 微信接口 申请退款 */
	public static final String WX_API_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	
	/** 微信接口 查询退款 */
	public static final String WX_API_REFUNDQUERY = "https://api.mch.weixin.qq.com/pay/refundquery";
	
	@Value("${config.domain}")
	private String domain;
	
	@Value("${account.wx.appid}")
	private String appid;
	@Value("${account.wx.secret}")
	private String secret;
	@Value("${account.wx.mchid}")
	private String mchid;
	@Value("${account.wx.mchkey}")
	private String mchkey;
	
	
	/**
	 * 微信申请退款
	 * @author YangJie [2016年4月28日 上午11:35:00]
	 * @param refundnum
	 * @param tradenum
	 * @param payMoney
	 * @param RefundMoney
	 * @return
	 */
	public Map<String, String> refund(String refundnum, String tradenum, int payMoney, int RefundMoney) {
		Map<String, String> paramMap = new HashMap<String ,String>();
		paramMap.put("appid", appid);		// 公众账号ID
		paramMap.put("mch_id", mchid);	// 商户号
		paramMap.put("transaction_id", tradenum);		// 微信订单号
		paramMap.put("out_refund_no", refundnum);		// 商户退款单号
		paramMap.put("total_fee", String.valueOf(payMoney));			// 订单总金额, 单位为【分】
		paramMap.put("refund_fee", String.valueOf(RefundMoney));	 // 退款金额, 单位为【分】
		paramMap.put("nonce_str", RandomUtil.getRandomStr(32));// 随机字符串
		paramMap.put("op_user_id", mchid); 			// 操作员帐号, 默认为商户号
		paramMap.put("sign", getSign(paramMap));	// 签名
		String xmlParamString = mapToXml(paramMap);			// 封装xml格式参数字符串
		SSLSocketFactory socketFactory = HttpsUtil.getSSLSocketFactory("证书路径", "证书密码");
		String result = HttpsUtil.postXml(socketFactory, WX_API_REFUND, xmlParamString);
		return XmlUtil.toObject(result, HashMap.class, String.class, String.class);
	}
	
	/**
	 * 处理微信退款返回
	 * @author YangJie [2016年4月28日 上午11:55:10]
	 * @param refundnum
	 * @param resultMap
	 * @return
	 */
	public boolean refundReturn(String refundnum, Map<String, String> resultMap) {
		// 判断通信标识
		if (!"SUCCESS".equals(resultMap.get("return_code"))) {
			return false;
		}
		// 验证签名
		if (!getSign(resultMap).equals(resultMap.get("sign"))) {
			return false;
		}
		// 判断交易状态
		if (!"SUCCESS".equals(resultMap.get("result_code"))) {
			return false;
		}
		// 完成支付状态
//		int refundFee = Integer.parseInt(resultMap.get("refund_fee").toString());	// 实际退款金额
		return true; // 此处调用完成退款订单
	}

	/**
	 * 通过api查询退款
	 * @author YangJie [2016年4月28日 下午4:09:22]
	 * @param refundnum
	 * @return
	 */
	public Map<String, String> refundQuery(String refundnum) {
		Map<String, String> paramMap = new HashMap<String ,String>();
		paramMap.put("appid", appid);		// 公众账号ID
		paramMap.put("mch_id", mchid);	// 商户退款单号
		paramMap.put("out_refund_no", refundnum);	// 商户号
		paramMap.put("nonce_str", RandomUtil.getRandomStr(32));	// 随机字符串
		paramMap.put("sign", getSign(paramMap));	// 签名
		String xmlParamString = mapToXml(paramMap); // 封装xml格式参数字符串
		String result = HttpUtil.postXml(WX_API_REFUNDQUERY, xmlParamString);
		return XmlUtil.toObject(result, HashMap.class, String.class, String.class);
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
