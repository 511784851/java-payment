<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="x-ua-compatible" content="ie=7" />
</head>
<body>
<a style="display:none" href="javascript:void 0" target=_blank id='openA'></a>
<script type="text/javascript">
var resp = ${resp};
if(resp.code == 'success' && resp.url){
    var url = resp.url + '?';
    for(var i in resp.params){
        url = url + i + '=' + encodeURI(resp.params[i]) + '&';
    }
    url = url.substr(0,url.length-1);
    window.open(url.replace(/^(?:ie:)?/i, "ie:"),'_blank','height='+screen.availHeight+',width='+screen.availWidth+',left=0,top=0,status=1,resizable=1,location=0,directories=0,titlebar=1,toolbar=0');
}else{
	var openA = document.getElementById('openA');
	openA.href =('https://'+window.location.hostname+'/error.html');
	openA.click();
}
</script>
</body>
</html>