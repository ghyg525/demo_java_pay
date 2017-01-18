package org.yangjie.entity;

import java.util.Date;

public class PayOrder {
	
	// 支付状态 未支付
	public static final byte ORDER_STATUS_WAIT = 1;
	// 支付状态 已支付
	public static final byte ORDER_STATUS_PAYED = 2;
	
	
    private Integer id;

    private String appkey;
    
    private String ordernum;

    private Integer orderMoney;

    private Byte orderStatus;

    private String orderTitle;

    private Date createTime;

    private Date payTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(Integer orderMoney) {
        this.orderMoney = orderMoney;
    }

    public Byte getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Byte orderStatus) {
        this.orderStatus = orderStatus;
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