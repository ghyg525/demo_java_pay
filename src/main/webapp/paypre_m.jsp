<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width" />
<title>选择支付方式</title>
</head>
<body>

<a href="/order/${ordernum}/pay/1"><button style="width:100%;height:100px">微信</button></a>
<a href="/order/${ordernum}/pay/2"><button style="width:100%;height:100px">支付宝</button></a>

</body>
<script type="text/javascript">
// 判断是否是微信浏览器
function isWeixin(){
	var ua = navigator.userAgent.toLowerCase();
	if(ua.match(/MicroMessenger/i)=="micromessenger") {
		return true;
	} else {
		return false;
	}
}
$(function(){
	/* // 根据当前浏览器隐藏相应支付方式
	if(isWeixin()){ 
		$("#dd_select_ali").hide();
	}else{
		$("#dd_select_wx").hide();
	} */
});
</script>
</html>