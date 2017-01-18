package org.yangjie.service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.yangjie.util.DateUtil;
import org.yangjie.util.HttpUtil;

/**
 * 支付宝退款
 * 只有核心方法, 非完整实例
 * @author YangJie [2016年5月17日 下午6:25:07]
 */
@Service
public class AliRefundService {
	
	@Value("${config.domain}")
	private String domain;
	
	@Value("${account.ali.appid}")
	private String appid;
	@Value("${account.ali.secret}")
	private String secret;
	

	/**
	 * 获取支付宝退款url
	 * @author YangJie [2016年3月8日 下午2:46:53]
	 * @param tradenum 第三方交易号
	 * @param money 退款金额(分), 不大于支付金额
	 * @param reason 退款理由
	 * @return
	 * @throws Exception
	 */
	public String getRefundUrl(String refundnum, String tradenum, int money, String reason) throws Exception{
		Map<String, String> params = new HashMap<String ,String>();DateUtil.formatDateTime(new Date());
		params.put("service", "refund_fastpay_by_platform_pwd");		// 接口名称
		params.put("partner", appid); 			// 合作者身份ID, 以2088开头的纯16位数字。
		params.put("seller_user_id", appid);	// 卖家支付宝用户号, 以2088开头的纯16位数字。
		params.put("batch_no", refundnum);	// 退款批次号, 不可重复
		params.put("batch_num", "1");			// 退款总笔数 (此为批量退款接口, 暂只做退一笔的情况)
		params.put("detail_data", packRefundDetail(tradenum, money, reason));	// 单笔数据集 (2011011201037066^5.00^协商退款)
		params.put("refund_date", DateUtil.formatDateTime(new Date())); 			// 退款请求的当前时间。格式为：yyyy-MM-dd hh:mm:ss。
		params.put("notify_url", domain+"/alirefund/notify");			// 异步通知URL
		params.put("_input_charset", "UTF-8");	// URL 编码
		params.put("sign", getSign(params));		// 签名
		params.put("sign_type", "MD5");			// 加密方式
		return packUrl(params);	
	}


	/**
	 * 处理退款返回
	 * @author YangJie [2016年2月17日 下午8:57:55]
	 * @param tradenum 第三方交易号
	 * @param resultMap
	 * @return
	 * @throws Throwable
	 */
	public boolean refundReturn(String refundnum, Map<String, String> resultMap) throws Exception{
		// 验证签名
		String sign = resultMap.get("sign");
		if(sign==null || !sign.equals(getSign(resultMap))){
			return false;
		}
		// 验证通知ID
		String notifyid = resultMap.get("notify_id"); // 支付宝通知ID
		if(!checkNotify(notifyid)){
			return false;
		}
		// 完成退款
//		String resultDetails = resultMap.get("result_details"); // 交易号^退款金额^处理结果。
//		int money = (int) (Float.parseFloat(resultDetails.split("^")[1]) * 100);	// 实际退款金额
		return true; // 此处调用完成退款订单
	}
	
	
	/**
	 * 封装退款详情(2011011201037066^5.00^协商退款)
	 * @author YangJie [2016年4月27日 下午1:50:35]
	 * @param paynum
	 * @param money
	 * @param reason
	 * @return
	 */
	private String packRefundDetail(String tradenum, int money, String reason) {
		String moneyF = String.valueOf(Float.parseFloat(String.valueOf(money)) / 100); // 金额转换为元
		return new StringBuilder().append(tradenum).append("^").append(moneyF).append("^").append(reason).toString();
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
	 * 封装请求url参数
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	private String packUrl(Map<String, String> params) throws Exception {
		List<String> keys = new ArrayList<String>(params.keySet());	//获取参数key
		StringBuffer prestr = new StringBuffer(AliPayService.ALI_REQUEST_URL).append("?");
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
	 * 检测是否是支付宝请求
	 * @author YangJie [2016年3月8日 下午3:44:56]
	 * @param notifyid
	 * @return
	 */
	private boolean checkNotify(String notifyid) {
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(AliPayService.ALI_REQUEST_URL)
			.append("?service=notify_verify")
			.append("&partner=").append(appid)
			.append("&notify_id=").append(notifyid);
		String result = HttpUtil.get(urlBuffer.toString());
		return "true".equals(result);
	}
	
}
