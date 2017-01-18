package org.yangjie.util;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 随机数据工具类
 * @author YangJie [2016年4月28日 下午12:18:27]
 */
public class RandomUtil {

	
	public static Random RANDOM = new Random();
	
	/**
	 * 获取随机数字 (支持[1-4]位)
	 * @author YangJie [2016年4月28日 下午12:19:47]
	 * @param length
	 * @return
	 */
	public static int getRandomInt(int length){
		switch (length) {
		case 1:
			return RANDOM.nextInt(9);
		case 2:
			return RANDOM.nextInt(89)+10;
		case 3:
			return RANDOM.nextInt(899)+100;
		case 4:
			return RANDOM.nextInt(8999)+1000;
		default:
			return -1; 
		}
	}
	
	/**
	 * 获取随机字符串 (支持[1-64]位)
	 * @author YangJie [2016年4月28日 下午2:29:26]
	 * @param length
	 * @return
	 */
	public static String getRandomStr(int length) {
		String str = UUID.randomUUID().toString().replace("-", "");
		if (length>0 && length<=32) {
			return str.substring(0, length);
		}
		str += UUID.randomUUID().toString().replace("-", "");
		if (length>32 && length<=64) {
			return str.substring(0, length);
		}
		return null;
	}
	
	/**
	 * 生成支付号
	 * 当前毫秒数+3位随机数
	 * @return
	 * @author YangJie
	 * @createTime 2015年5月9日 下午12:00:48
	 */
	public static String getPaynum(){
		return String.valueOf(System.currentTimeMillis()) + getRandomInt(3);
	}
	
	/**
	 * 生成订单号
	 * 当前日期+3位随机数
	 * @return
	 * @author YangJie
	 * @createTime 2015年5月9日 下午12:00:48
	 */
	public static String getOrdernum() {
		return DateUtil.formatShortMillisecond(new Date()) + getRandomInt(3);
	}
	
}