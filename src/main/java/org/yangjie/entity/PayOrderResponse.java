package org.yangjie.entity;

import java.util.Date;

public class PayOrderResponse {
	
    private String appkey;
    
    private String ordernum;

    private int orderMoney;

    private boolean payed;

    private String orderTitle;

    private Date createTime;

    private Date payTime;

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey == null ? null : appkey.trim();
    }
    
    public String getOrdernum() {
    	return ordernum;
    }
    
    public void setOrdernum(String ordernum) {
    	this.ordernum = ordernum == null ? null : ordernum.trim();
    }

    public int getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(int orderMoney) {
        this.orderMoney = orderMoney;
    }

    public boolean isPayed() {
		return payed;
	}

	public void setPayed(boolean payed) {
		this.payed = payed;
	}

    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle == null ? null : orderTitle.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }
}