<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>
<body onload='document.getElementById("form").submit();'>
	<form id='form' name='form' action='http://s2.vnetone.com/Default.aspx'
		method='post'>
		<!--spID 5位-->
		<input name='spid' type='hidden'
			value='<%=request.getParameter("spid")%>' />
		<!--sp网站 客户网站名-->
		<input name='spname' type='hidden'
			value='<%=request.getParameter("spname")%>' />
		<!--sp订单   唯一订单码，长度不超过30字符-->
		<input name='spoid' type='hidden'
			value='<%=request.getParameter("spoid")%>' />
		<!--网站请求地址-->
		<input name='spreq' type='hidden'
			value='<%=request.getParameter("spreq")%>' />
		<!--网站接收地址 请直接以ASP/php/jsp结尾 不要带参数 我方会给此地址加上？参数=XXX 等格式   客户站接收GET URL数据 -->
		<input name='sprec' type='hidden'
			value='<%=request.getParameter("sprec")%>' />
		<!--客户ID-->
		<input name='userid' type='hidden'
			value='<%=request.getParameter("userid")%>' />
		<!--客户浏览器IP-->
		<input name='userip' type='hidden'
			value='<%=request.getParameter("userip")%>' />
		<!--MD5-->
		<input name='spmd5' type='hidden'
			value='<%=request.getParameter("spmd5")%>' />
		<!--用户自己定义30个字符以内 -->
		<input name='spcustom' type='hidden'
			value='<%=request.getParameter("spcustom")%>' />
		<!--支付版本号码 -->
		<input name='spversion' type='hidden'
			value='<%=request.getParameter("spversion")%>' />
		<!--用户根据网站自己定义的整数面值如1,2,3,4....单位：V币-->
		<input name='money' type='hidden'
			value='<%=request.getParameter("money")%>' />
		<!-- default utf-8 -->
		<input name='urlcode' type='hidden'
			value='<%=request.getParameter("urlcode")%>' />
	</form>
</body>
</html>