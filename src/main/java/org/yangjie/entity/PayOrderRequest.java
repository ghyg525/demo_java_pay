package org.yangjie.entity;

/**
 * 添加订单实体
 * @author YangJie [2017年1月16日 下午5:04:43]
 */
public class PayOrderRequest {

    private String appkey;
    private int money;


    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey == null ? null : appkey.trim();
    }

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
  
}