package org.yangjie.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.yangjie.entity.PayItem;

@Mapper
public interface PayItemDao {
	
	@Select("select * from pay_item where paynum=#{paynum}")
	public PayItem select(String paynum);
	
	@Select("select * from pay_item where ordernum=#{ordernum} order by id desc")
	public List<PayItem> selectByOrdernum(String ordernum);
	
	@Insert("insert into pay_item (paynum, ordernum, pay_type, pay_status, pay_money, create_time)"
			+ " values (#{paynum}, #{ordernum}, #{payType}, #{payStatus}, #{payMoney}, now())")
	public boolean insert(PayItem pay);
	
	@Update("update pay_item set pay_status=#{payStatus}, tradenum=#{tradenum}, "
			+ "pay_flag=#{payFlag}, pay_time=now() where paynum=#{paynum}")
	public boolean updateStatus(@Param("paynum")String paynum, @Param("tradenum")String tradenum, 
			@Param("payStatus")byte payStatus, @Param("payFlag")byte payFlag);

}