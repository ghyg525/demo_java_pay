<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>微信扫码支付</title>
</head>
<body>

<img src="/wxpay/code?codeUrl=${codeUrl}" alt="微信支付二维码"/>

<br>

<a href="/pay/${paynum}/ok">支付完成</a>

</body>
<!-- // 定时轮询支付结果
<script src="//apps.bdimg.com/libs/jquery/2.1.1/jquery.min.js"></script>
<script type="text/javascript">
// 定时查询订单状态
$(function(){
	var count = 0;
	var paynum = "${paynum}";
	var timer = setInterval(function(){
		$.get("/wxpay/check/"+paynum, function(data){
			if(data==true || data=="true"){
				location.href="/pay/${paynum}/ok";
			}else{
				if(count++ > 20){ // 重试20次后认为失败
					clearInterval(timer);    //清除定时器
				}
			}
		});
	}, 5000); // 5秒一次
});
</script> 
-->  
</html>