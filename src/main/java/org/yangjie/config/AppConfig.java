package org.yangjie.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.yangjie.service.PayItemService;
import org.yangjie.service.PayOrderService;

@Configuration
@ConfigurationProperties
public class AppConfig {
	
	private Map<String, AppSetting> appList;

	@Autowired
	private PayOrderService payOrderService;
	@Autowired
	private PayItemService payItemService;
	
	
	/**
	 * 获取业务配置信息
	 * @author YangJie [2016年5月3日 下午5:38:15]
	 * @param appkey
	 * @return
	 */
	public AppSetting getConfig(String appkey){
		if (appkey!=null && !appkey.trim().isEmpty()) {
			return appList.get(appkey);
		}
		return null;
	}
	
	/**
	 * 获取业务配置信息
	 * @author YangJie [2016年5月3日 下午5:38:15]
	 * @param ordernum
	 * @return
	 */
	public AppSetting getConfigByOrdernum(String ordernum){
		if (ordernum!=null && !ordernum.trim().isEmpty()) {
			return appList.get(payOrderService.getOrder(ordernum).getAppkey());
		}
		return null;
	}
	
	/**
	 * 获取业务配置信息
	 * @author YangJie [2016年5月3日 下午5:38:15]
	 * @param paynum
	 * @return
	 */
	public AppSetting getConfigByPaynum(String paynum){
		if (paynum!=null && !paynum.trim().isEmpty()) {
			return appList.get(payOrderService.getOrder(payItemService.getPay(paynum).getOrdernum()).getAppkey());
		}
		return null;
	}
	
	
	public Map<String, AppSetting> getAppList() {
		return appList;
	}

	public void setAppList(Map<String, AppSetting> appList) {
		this.appList = appList;
	}
	
}