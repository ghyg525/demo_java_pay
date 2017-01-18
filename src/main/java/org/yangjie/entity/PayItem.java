package org.yangjie.entity;

import java.util.Date;

public class PayItem {
	
	// 支付状态 未支付
	public static final byte PAY_STATUS_WAIT = 1;
	// 支付状态 已支付
	public static final byte PAY_STATUS_PAYED = 2;
	
	// 支付类型 微信
	public static final byte PAY_TYPE_WX = 1;
	// 支付类型 支付宝
	public static final byte PAY_TYPE_ALI = 2;
	
	// 支付标记 - 异步通知 
	public static final byte PAY_FLAG_NOTIFY = 1;
	// 支付标记 - 同步回调 
	public static final byte PAY_FLAG_RETURN = 2;
	// 支付标记 - 主动查询 
	public static final byte PAY_FLAG_QUERY = 3;
	// 支付标记 - 对账 
	public static final byte PAY_FLAG_CHECK = 4;
	
	
    private Integer id;

    private String paynum;

    private String ordernum;

    private String tradenum;

    private Integer payMoney;

    private Byte payStatus;

    private Byte payType;

    private Byte payFlag;

    private Date createTime;

    private Date payTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaynum() {
        return paynum;
    }

    public void setPaynum(String paynum) {
        this.paynum = paynum == null ? null : paynum.trim();
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum == null ? null : ordernum.trim();
    }

    public String getTradenum() {
        return tradenum;
    }

    public void setTradenum(String tradenum) {
        this.tradenum = tradenum == null ? null : tradenum.trim();
    }

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }

    public Byte getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Byte payStatus) {
        this.payStatus = payStatus;
    }

    public Byte getPayType() {
        return payType;
    }

    public void setPayType(Byte payType) {
        this.payType = payType;
    }

    public Byte getPayFlag() {
        return payFlag;
    }

    public void setPayFlag(Byte payFlag) {
        this.payFlag = payFlag;
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