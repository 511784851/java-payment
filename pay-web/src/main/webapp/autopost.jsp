<%@ page language="java" contentType="text/html; charset=UTF8"  pageEncoding="UTF8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.guzhi.pay.helper.StringHelper" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<title></title>
<%
	Map<String,String> trxMap = (Map<String,String>)request.getAttribute("trxMap");
if(null==trxMap){
    response.getWriter().println("没有有效参数trxMap.");
}
	String payUrl = trxMap.get("payUrl");
	Iterator entries = trxMap.entrySet().iterator();  
	Map.Entry entry;  
	String name = "";  
	String value = "";  
%>
<script type="text/javascript">
	function on_submit(){
		document.forms[0].submit();
	}
</script>
</head>
<body onload="on_submit();">
<form id="form" action="<%=payUrl%>" method="post">
<%
	while (entries.hasNext()) 
	{  
		entry = (Map.Entry) entries.next();  
		name = (String) entry.getKey();  
		
		if("payUrl".equals(name))continue;
		
		Object valueObj = entry.getValue();  
		if(null == valueObj){  
			value = "";  			
		}else  
			value = valueObj.toString();  
%>
	<input type="hidden" name="<%=name%>" value="<%=value%>" />
<%
	}
%>
</form>
</body>
</html>