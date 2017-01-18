package org.yangjie.entity;

/**
 * 微信js api 支付参数
 * @author YangJie [2017年1月18日 下午2:34:59]
 */
public class JsApiPayConfig{
	
	private String appId;
	private String timeStamp;
	private String nonceStr;
	private String packageStr;
	private String signType;
	private String paySign;
	private JsApiConfig initConfig;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getPackageStr() {
		return packageStr;
	}
	public void setPackageStr(String packageStr) {
		this.packageStr = packageStr;
	}
	public String getSignType() {
		return signType;
	}
	public void setSignType(String signType) {
		this.signType = signType;
	}
	public String getPaySign() {
		return paySign;
	}
	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}
	public JsApiConfig getInitConfig() {
		return initConfig;
	}
	public void setInitConfig(JsApiConfig initConfig) {
		this.initConfig = initConfig;
	}
	
}