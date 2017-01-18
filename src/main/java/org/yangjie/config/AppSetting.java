package org.yangjie.config;

public class AppSetting {
	
	private String title;
	private String notifyUrl;
	private String returnUrl;
	private boolean debug;
	
	
	public String getNotifyUrl() {
		return notifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isDebug() {
		return debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}