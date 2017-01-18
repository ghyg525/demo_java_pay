package org.yangjie.entity;

/**
 * 微信js api 初始化参数
 * @author YangJie [2017年1月18日 下午2:34:42]
 */
public class JsApiConfig{
	
	private String appId;
	private String timestamp;
	private String nonceStr;
	private String signature;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
}