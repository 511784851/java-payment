<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<meta content="IE=EmulateIE7" http-equiv="X-UA-Compatible" />
<title>充值中心</title>
<meta content="" name="Description" />
<meta content="" name="Keywords" />
<link rel="stylesheet" type="text/css" href="/v1.0/css/global.css" />
<link rel="stylesheet" type="text/css" href="/v1.0/css/pay-order.css" />
</head>
<body>
    <!-- doc{ -->
    <div id="doc" class="clearfix">
        <!-- header{ -->
        <div id="header" class="clearfix">
            <a href="" class="logo"></a>
            <div class="quick-link fr">
                <span style="color:black;">您好, ${nick}&nbsp;&nbsp;</span>
                </a>
            </div>
        </div>
        <!-- }header -->
        <!-- content{ -->
        <div id="body">
            <div id="body-header">
               <div class="left">确认订单信息：</div>
               <div class="right">以下商品来自<c:choose>
               	<c:when test="${order.appId == '20'}">会员</c:when>
               	<c:when test="${order.appId == '10'}">多玩游戏网</c:when>
               	<c:when test="${order.appId == '30'}">教育</c:when></c:choose></div>
            </div>
            <div id="product-desc-tbl">
                <div class="product-desc-head">
                    <div id="order-no-head">订单号</div>
                    <div style="width:375px;">商品名称</div>
                    <div style="width:115px;">单价</div>
                    <div style="width:115px;">数量</div>
                    <div class="summary">小计</div>
                </div>
                <div class="product-desc-row">
                    <div class="order-num-cell"><c:out value="${order.appOrderId}" escapeXml="true"/></div>
                    <div class="product-name-cell"><c:out value="${order.productName}" escapeXml="true"/></div>
                    <div class="sell-pric-cell"><fmt:formatNumber value="${order.salePric/100}" pattern="0.00"/>元</div>
                    <div class="amount-cell">${order.amount}</div>
                    <div class="summary"><span class="orange bold"><fmt:formatNumber value="${order.payPric/100}" pattern="0.00"/></span>元</div>
                </div>
            </div>
        </div>
        <div id="choose-pay-lbl">请选择支付方式：</div>
        <div id="bank-list">
            <div id="pay-method-row">
                <ul class="pay-method-list">
                    <li class="pay-method sel-pay-method">
                                                                网上银行支付
                        <div id="sel-symbol-cell">
                            <em>◆</em>
                            <span>◆</span>
                        </div>
                    </li>
                    <!--<li class="pay-method">新增支付方式</li>-->
                </ul>
            </div>
            <div class="warn-row">
                <div style="padding-left:45px;"><img src="/v1.0/img/tip_info.gif"/>&nbsp;请确保您已经在银行柜台开通了网上支付功能，否则无法支付成功。<a href="http://www..com/1110/182279823054.html">如何开通?</a></div>
            </div>
            <div id="pay-row">
                <div style="width:90px;margin-left:45px;">应付金额</div>
                <div style="width:730px;"><span class="orange"><fmt:formatNumber value="${order.payPric/100}" pattern="0.00"/></span>元</div>
            </div>
            <div id="bank-row">
                <form id="pay-form" action="/order-pay-confirm" method="post">
                    <input type="hidden" name="uid" value="${order.uid}"/>
                    <div id="sel-bank-lbl">选择银行</div>
                    <div id="choose-bank-cell">
                        <c:if test="${empty lastEntryId}">
                        <div id="mult-banks">
                            <div class="bank-cell alipay-bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="alipay" <c:if test="${empty lastEntryId}">checked="checked"</c:if>/>&nbsp;</div>
                                <div class="bank"><div class="alipay"></div></div>
                                <div class="alipay-info">支持快捷支付，支付更便捷!</div>
                            </div>
                            <div class="blank"></div>
                            <div class="blank"></div>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="abc"/>&nbsp;</div>
                                <div class="bank"><div class="abc"></div></div>
                            </div>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="ccb"/>&nbsp;</div>
                                <div class="bank"><div class="ccb"></div></div>
                            </div>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="cmb"/>&nbsp;</div>
                                <div class="bank"><div class="cmb"></div></div>
                            </div>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="boc"/>&nbsp;</div>
                                <div class="bank"><div class="boc"></div></div>
                            </div>
                            <div class="blank"></div>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="cib"/>&nbsp;</div>
                                <div class="bank"><div class="cib"></div></div>
                            </div>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="bcom"/>&nbsp;</div>
                                <div class="bank"><div class="bcom"></div></div>
                            </div>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="citic"/>&nbsp;</div>
                                <div class="bank"><div class="citic"></div></div>
                            </div>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="sdb"/>&nbsp;</div>
                                <div class="bank"><div class="sdb"></div></div>
                            </div>
                            <div class="blank"></div>
                            <div><span><a href="#" class="choose-order-bank-link">选择其他&nbsp;<img src="/v1.0/img/other_bank_dire.gif"/></a></div>
                            <div class="blank"></div>
                        </div>
                        </c:if>
                        <div id="single-bank" <c:if test="${empty lastEntryId}">style="display:none"</c:if>>
                            <div class="bank-cell">
                                <div class="radio"><input type="radio" name="payEntry" value="${lastEntryId}" <c:if test="${not empty lastEntryId}">checked="checked"</c:if>/>&nbsp;</div>
                                <div class="bank sel-bank"><div class="${lastEntryId}"></div></div>
                            </div>
                            <div id="sing-sel-other-bank-cell"><span><a href="#" class="choose-order-bank-link">选择其他&nbsp;<img src="/v1.0/img/other_bank_dire.gif"/></a></div>
                            <div class="blank"></div>
                        </div>
                        <div style="float:left;width:100%;" id="submit-row">
                            <input type="image" src="/v1.0/img/go_to_pay.gif" id="submit-btn"/>
                            <a name="submit"/>
                        </div>
                    </div>
                    <input type="hidden" name="orderId" value="${orderId}"/>
                </form>
            </div>
        </div>
        <!-- }content -->
</div>

<!-- }doc -->
<!-- other-banks{ -->
<div id="other-banks-over" class="other-banks-over" style="display: none;">
    <div class="other-banks-over-warp"></div>
    <div class="other-bank-list">
        <div class="other-bank-title">
            <span class="other-title-text"> 请选择银行 </span> 
            <span class="other-bank-close" id="other-bank-close" title="关闭"></span>
        </div>
        <div id="other-bank-dialog">
            <div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="alipay" checked="checked"/>&nbsp;</div>
                    <div class="bank"><div class="alipay"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="icbc"/>&nbsp;</div>
                    <div class="bank"><div class="icbc"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="ccb"/>&nbsp;</div>
                    <div class="bank"><div class="ccb"></div></div>
                </div>
            </div>
            <div class="blank"></div>
            <div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="bcom"/>&nbsp;</div>
                    <div class="bank"><div class="bcom"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="cmb"/>&nbsp;</div>
                    <div class="bank"><div class="cmb"></div></div>
                </div>
                 <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="boc"/>&nbsp;</div>
                    <div class="bank"><div class="boc"></div></div>
                </div>
            </div>
            <div class="blank"></div>
            <div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="ceb"/>&nbsp;</div>
                    <div class="bank"><div class="ceb"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="citic"/>&nbsp;</div>
                    <div class="bank"><div class="citic"></div></div>
                </div>
                 <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="sdb"/>&nbsp;</div>
                    <div class="bank"><div class="sdb"></div></div>
                </div>
            </div>
             <div class="blank"></div>
            <div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="spdb"/>&nbsp;</div>
                    <div class="bank"><div class="spdb"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="cmbc"/>&nbsp;</div>
                    <div class="bank"><div class="cmbc"></div></div>
                </div>
                 <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="cib"/>&nbsp;</div>
                    <div class="bank"><div class="cib"></div></div>
                </div>
            </div>
             <div class="blank"></div>
            <div style="float:left;width:100%;">
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="pab"/>&nbsp;</div>
                    <div class="bank"><div class="pab"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="bea"/>&nbsp;</div>
                    <div class="bank"><div class="bea"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="shrcc"/>&nbsp;</div>
                    <div class="bank"><div class="shrcc"></div></div>
                </div>
            </div>
             <div class="blank"></div>
            <div style="float:left;width:100%;">
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="shb"/>&nbsp;</div>
                    <div class="bank"><div class="shb"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="nbcb"/>&nbsp;</div>
                    <div class="bank"><div class="nbcb"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="hzb"/>&nbsp;</div>
                    <div class="bank"><div class="hzb"></div></div>
                </div>
            </div>
             <div class="blank"></div>
            <div style="float:left;width:100%;">
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="bob"/>&nbsp;</div>
                    <div class="bank"><div class="bob"></div></div>
                </div>
                <div class="bank-cell">
                    <div class="radio"><input type="radio" name="otherBank" value="bjrcb"/>&nbsp;</div>
                    <div class="bank"><div class="bjrcb"></div></div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- }other-banks -->
<script type="text/javascript" src="/v1.0/js/lib/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="/v1.0/js/jqplugin/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
<script type="text/javascript" src="/v1.0/js/core/base.js"></script>
<script type="text/javascript" src="/v1.0/js/pay-form.js"></script>
</body>
</html>

