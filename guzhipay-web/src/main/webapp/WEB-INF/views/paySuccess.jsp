<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<meta content="IE=EmulateIE7" http-equiv="X-UA-Compatible" />
<title>充值中心</title>
<meta content="" name="Description" />
<meta content="" name="Keywords" />
<link rel="stylesheet" type="text/css" href="v1.0/css/global.css" />
</head>
<body>
	<!-- doc{ -->
	<div id="doc" class="clearfix">
		<!-- header{ -->
		<div id="header" class="clearfix">
			<a href="" class="logo"></a>
		</div>
		<!-- }header -->
		<!-- content{ -->
		<div class="content clearfix">
			<div class="content-hd"></div>
			<div class="content-bd clearfix">
				<div class="mod-msg">
					<i class="icon"></i>
					<p class="ts">付款成功！</p>
					<p>您已为 ${order.gameName}-${order.goodsName} 成功付款</p>
					<p>
						<span>订 单 号：</span><em class="blue">${order.orderId}</em>
					</p>
					<p>
						<span>付款金额：</span><em class="orange"> <fmt:formatNumber value="${order.payPric/100}" pattern="0.##"/></em> 元
					</p>
					<div class="blank10"></div>
					<p>
						您现在可以 <a href="${ticket}">查看订单状态</a> 或 <a href="javascript:window.opener=''; window.close();void 0;">关闭本页面</a>
					</p>
				</div>
			</div>
			<div class="content-ft"></div>
		</div>
		<!-- }content -->
</div>
<!-- }doc -->
</body>
</html>

