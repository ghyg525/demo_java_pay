package org.yangjie.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.yangjie.entity.PayOrder;

@Mapper
public interface PayOrderDao {
	
	@Select("select * from pay_order where ordernum=#{ordernum}")
	public PayOrder select(String ordernum);
	
	@Insert("insert into pay_order (appkey, ordernum, order_status, order_title, order_money, create_time)"
			+ " values (#{appkey}, #{ordernum}, #{orderStatus}, #{orderTitle}, #{orderMoney}, now())")
	public boolean insert(PayOrder order);
	
	@Update("update pay_order set order_status=#{orderStatus}, pay_time=now() where ordernum=#{ordernum}")
	public boolean updateStatus(@Param("ordernum")String ordernum, @Param("orderStatus")byte orderStatus);
	
}