package org.yangjie.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * servlet工具类
 * 提供server api相关的静态工具类
 * @author YangJie [2016年5月17日 下午6:51:56]
 */
public class ServletUtil {

	
	/**
	 * 获取当前请求地址(带参数)
	 * @author YangJie [2016年3月10日 下午8:43:29]
	 * @param request
	 * @return
	 */
	public static String getRequestUrl(HttpServletRequest request){
		String requestUrl = request.getRequestURL().toString().replace("http://", "https://");
		String queryString = request.getQueryString();
		if (queryString!=null && !queryString.trim().isEmpty()) {
			requestUrl += ("?"+queryString);
		}
		return requestUrl;
	}
	
	/**
	 * 获取参数Map<String, String>
	 * @author YangJie [2016年5月17日 下午6:52:33]
	 * @param request
	 * @return
	 */
	public static Map<String, String> getParamMap(HttpServletRequest request){
		Map<String, String> returnMap = new HashMap<String, String>();
		Map<String, String[]> paramMap = request.getParameterMap();
		for (String key: paramMap.keySet()) {
			String[] values = paramMap.get(key);
			if (values!=null && values.length>0) {
				returnMap.put(key, values[0]);
			}
		}
		return returnMap;
	}
}