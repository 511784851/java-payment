/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.kq;

/**
 * 订单状态码转化工具类。
 * 
 * @author 
 * 
 */
public class KqStatusMsgHelper {

    /**
     * 快钱卡密支付请求响应状态码
     * 参考《快钱【充值卡支付网关】商户接口规范（高级版）Ver2.2.1 008A》
     * 在文件“商户接入规范-充值卡网关.pdf”中
     * 
     */
    public interface CardPayResponseReturnCode {
        String RETURN_CODE_00000 = "00000";
        String RETURN_MSG_00000 = "未知错误";
        String RETURN_CODE_10001 = "10001";
        String RETURN_MSG_10001 = "不支持的字符编码格式,系统支持的字符编码格式为 1.GBK,2.UTF-8,3.GB2312";
        String RETURN_CODE_10002 = "10002";
        String RETURN_MSG_10002 = "不支持的返回类型,系统支持的返回类型为 1.页面返回,2.后台返回,3.同时支持页面和后台返回 ";
        String RETURN_CODE_10003 = "10003";
        String RETURN_MSG_10003 = "页面返回地址为空或不合法,请使用一个未带参数的http或者https的合法URL地址";
        String RETURN_CODE_10004 = "10004";
        String RETURN_MSG_10004 = "后台返回地址为空或不合法,请使用一个未带参数的http或者https的合法URL地址";
        String RETURN_CODE_10005 = "10005";
        String RETURN_MSG_10005 = "不支持的网关接口版本号,目前系统支持的版本号为 V2.0";
        String RETURN_CODE_10006 = "10006";
        String RETURN_MSG_10006 = "商户快钱用户名不存在";
        String RETURN_CODE_10007 = "10007";
        String RETURN_MSG_10007 = "页面返回地址和后台返回地址不能都为空,请使用一个未带参数的http或者https的合法URL地址";
        String RETURN_CODE_10010 = "10010";
        String RETURN_MSG_10010 = "订单号不正确,系统只支持以字母,数字组合的订单号";
        String RETURN_CODE_10011 = "10011";
        String RETURN_MSG_10011 = "订单金额不正确,请输入以分为单位的金额";
        String RETURN_CODE_10012 = "10012";
        String RETURN_MSG_10012 = "订单提交时间不正确,请输入以gbgbMMddhhmmss格式的时间字符串";
        String RETURN_CODE_10013 = "10013";
        String RETURN_MSG_10013 = "商品名称不正确";
        String RETURN_CODE_10014 = "10014";
        String RETURN_MSG_10014 = "商品数量不正确";
        String RETURN_CODE_10015 = "10015";
        String RETURN_MSG_10015 = "商品ID不正确";
        String RETURN_CODE_10016 = "10016";
        String RETURN_MSG_10016 = "商品的描述不正确";
        String RETURN_CODE_10017 = "10017";
        String RETURN_MSG_10017 = "扩展参数一不正确";
        String RETURN_CODE_10018 = "10018";
        String RETURN_MSG_10018 = "扩展参数二不正确";
        String RETURN_CODE_10019 = "10019";
        String RETURN_MSG_10019 = "指定的支付方式不正确";
        String RETURN_CODE_10020 = "10020";
        String RETURN_MSG_10020 = "指定的支付服务代码不正确";
        String RETURN_CODE_10021 = "10021";
        String RETURN_MSG_10021 = "指定的银行ID不正确";
        String RETURN_CODE_10022 = "10022";
        String RETURN_MSG_10022 = "不支持的语言类型,系统支持的语言为1.中文,2.英文";
        String RETURN_CODE_10023 = "10023";
        String RETURN_MSG_10023 = "不支持的签名类型,系统支持的签名类型为1.MD5";
        String RETURN_CODE_10024 = "10024";
        String RETURN_MSG_10024 = "商户未开通快钱充值卡网关";
        String RETURN_CODE_10025 = "10025";
        String RETURN_MSG_10025 = "支付人联系方式仅允许Email";
        String RETURN_CODE_10026 = "10026";
        String RETURN_MSG_10026 = "支付人联系方式仅允许电话或手机";
        String RETURN_CODE_10027 = "10027";
        String RETURN_MSG_10027 = "订单金额不正确,请输入大于1或小于500的以元为单位的金额";
        String RETURN_CODE_20001 = "20001";
        String RETURN_MSG_20001 = "订单信息的签名内容不正确";
        String RETURN_CODE_30001 = "30001";
        String RETURN_MSG_30001 = "签名类型为空或不正确(签名类型为 1)";
        String RETURN_CODE_30002 = "30002";
        String RETURN_MSG_30002 = "安全校验域不正确";
        String RETURN_CODE_30003 = "30003";
        String RETURN_MSG_30003 = "商户的充值卡收款账户为空或不正确";
        String RETURN_CODE_30004 = "30004";
        String RETURN_MSG_30004 = "订单号不正确";
        String RETURN_CODE_30005 = "30005";
        String RETURN_MSG_30005 = "订单金额为空或不正确";
        String RETURN_CODE_30006 = "30006";
        String RETURN_MSG_30006 = "mac 不正确";
        String RETURN_CODE_30007 = "30007";
        String RETURN_MSG_30007 = "商户收款账户被冻结";
        String RETURN_CODE_30008 = "30008";
        String RETURN_MSG_30008 = "收款账户类型不存在";
        String RETURN_CODE_30009 = "30009";
        String RETURN_MSG_30009 = "商户没有开通快钱充值卡支付网关";
        String RETURN_CODE_30010 = "30010";
        String RETURN_MSG_30010 = "商户账户状态异常";
        String RETURN_CODE_30011 = "30011";
        String RETURN_MSG_30011 = "支付方式状态异常";
        String RETURN_CODE_30012 = "30012";
        String RETURN_MSG_30012 = "卡密直连参数错误";
        String RETURN_CODE_30013 = "30013";
        String RETURN_MSG_30013 = "订单金额不是整数,不是标准的充值卡面额,无法支付";
        String RETURN_CODE_30014 = "30014";
        String RETURN_MSG_30014 = "参数类型不正确";
        String RETURN_CODE_30015 = "30015";
        String RETURN_MSG_30015 = "支付密码不正确";
        String RETURN_CODE_30016 = "30016";
        String RETURN_MSG_30016 = "余额不足";
        String RETURN_CODE_30017 = "30017";
        String RETURN_MSG_30017 = "bgUrl和pageUrl不正确";
        String RETURN_CODE_30018 = "30018";
        String RETURN_MSG_30018 = "商户账户不存在";
        String RETURN_CODE_30019 = "30019";
        String RETURN_MSG_30019 = "卡密已失效";
        String RETURN_CODE_30020 = "30020";
        String RETURN_MSG_30020 = "订单已存在不能重复提交";
        String RETURN_CODE_30021 = "30021";
        String RETURN_MSG_30021 = "卡密不正确,请重新确认再输入";
        String RETURN_CODE_30022 = "30022";
        String RETURN_MSG_30022 = "商户账户状态被止入";
        String RETURN_CODE_30023 = "30023";
        String RETURN_MSG_30023 = "全额支付标志为空";
        String RETURN_CODE_30024 = "30024";
        String RETURN_MSG_30024 = "全额支付标志只能为 0(不支持全额支付)或 1(支持全额支付)";
        String RETURN_CODE_30025 = "30025";
        String RETURN_MSG_30025 = "订单提交时间为空或格式不正确（正确格式为：gbgbMMddHHmmss.例如20080913090103）";
        String RETURN_CODE_30026 = "30026";
        String RETURN_MSG_30026 = "订单批次号不能为空";
        String RETURN_CODE_30027 = "30027";
        String RETURN_MSG_30027 = "订单批次号或订单已经存在不能重复提交";
        String RETURN_CODE_30028 = "30028";
        String RETURN_MSG_30028 = "商品数量为 0～999 之间";
        String RETURN_CODE_30029 = "30029";
        String RETURN_MSG_30029 = "对不起,由于您在支付页面上停留的时间过长,此次支付会话已失效.请重新从商户网站提交订单并支付";
        String RETURN_CODE_30030 = "30030";
        String RETURN_MSG_30030 = "参数值不能包含，‘’，“”&<>()#?*等特殊符号,否则将会导致校验失败.";
        String RETURN_CODE_30031 = "30031";
        String RETURN_MSG_30031 = "帐户余额不足,请选择卡密支付";
        String RETURN_CODE_0 = "0";
        String RETURN_MSG_0 = "充值失败";
        String RETURN_CODE_1 = "1";
        String RETURN_MSG_1 = "订单金额不能小于1元";
        String RETURN_CODE_2 = "2";
        String RETURN_MSG_2 = "系统正在维护当中,请稍后再试";
        String RETURN_CODE_3 = "3";
        String RETURN_MSG_3 = "卡密销卡失败";
        String RETURN_CODE_4 = "4";
        String RETURN_MSG_4 = "亲爱的用户,对不起!因为运营商流量的限制，目前暂时无法处理您的请求，请您稍后在重新提交。对于给您带来的不便，我们深感抱歉!谢谢您的配合!";
        String RETURN_CODE_5 = "5";
        String RETURN_MSG_5 = "系统正在维护当中,暂时无法进行充值,请稍后再试";
        String RETURN_CODE_120 = "120";
        String RETURN_MSG_120 = "您的订单已受理，快钱处理完成后会将处理结果发送到您的邮箱，并且直接通知商户。请耐心等待，谢谢！";
        String RETURN_CODE_121 = "121";
        String RETURN_MSG_121 = "系统正在维护中,暂时无法进行支付";
        String RETURN_CODE_122 = "122";
        String RETURN_MSG_122 = "参数不符合接口规范,无法进行交易";
        String RETURN_CODE_123 = "123";
        String RETURN_MSG_123 = "系统正在维护当中,暂时无法进行支付,请稍后再试";
        String RETURN_CODE_126 = "126";
        String RETURN_MSG_126 = "您的订单已经处理,因为网络问题暂时无法与商户进行通讯，请快来电查询订单的处理情况，谢谢！";
        String RETURN_CODE_500 = "500";
        String RETURN_MSG_500 = "订单金额不能大于500元";
        String RETURN_CODE_101 = "101";
        String RETURN_MSG_101 = "您输入的卡号错误";
        String RETURN_CODE_102 = "102";
        String RETURN_MSG_102 = "您输入的密码错误";
        String RETURN_CODE_103 = "103";
        String RETURN_MSG_103 = "您提交的卡号或密码重试次数过多";
        String RETURN_CODE_104 = "104";
        String RETURN_MSG_104 = "您提交的卡密已被使用过";
        String RETURN_CODE_105 = "105";
        String RETURN_MSG_105 = "暂不支持该充值卡的支付";
        String RETURN_CODE_106 = "106";
        String RETURN_MSG_106 = "您选择的卡面额不正确";
        String RETURN_CODE_107 = "107";
        String RETURN_MSG_107 = "运营商支付系统维护";
        String RETURN_CODE_108 = "108";
        String RETURN_MSG_108 = "充值卡密码已失效，或已过有效期";
        String RETURN_CODE_110 = "110";
        String RETURN_MSG_110 = "卡号和密码不是来源于同一张充值卡";
        String RETURN_CODE_111 = "111";
        String RETURN_MSG_111 = "其它错误";
        String RETURN_CODE_206 = "206";
        String RETURN_MSG_206 = "运营商处理失败，卡仍然有效";
        String RETURN_CODE_207 = "207";
        String RETURN_MSG_207 = "暂不支持该充值卡的支付";
        String RETURN_CODE_208 = "208";
        String RETURN_MSG_208 = "运营商支付系统繁忙";
        String RETURN_CODE_209 = "209";
        String RETURN_MSG_209 = "与渠道接口技术故障";
        String RETURN_CODE_210 = "210";
        String RETURN_MSG_210 = "充值卡未激活";
        String RETURN_CODE_211 = "211";
        String RETURN_MSG_211 = "运营商处理失败，卡没有处理";
        String RETURN_CODE_212 = "212";
        String RETURN_MSG_212 = "该地区运营商系统维护";
    }

    /**
     * 快钱卡密查询返回的错误码
     * 参考《快钱【神州行支付网关】商户查询接口规范 ver2.0.001》
     * 在文件“12105214e5fv.pdf”中
     * 
     */
    public interface CardQueryResponseErrorCode {
        String ERROR_CODE_00000 = "00000";
        String ERROR_MSG_00000 = "未知错误";
        String ERROR_CODE_10001 = "10001";
        String ERROR_MSG_10001 = "网关版本号不正确或不存在";
        String ERROR_CODE_10002 = "10002";
        String ERROR_MSG_10002 = "签名类型不正确或不存在";
        String ERROR_CODE_10003 = "10003";
        String ERROR_MSG_10003 = "账号格式不正确";// 原文为“神州行账号格式不正确”，为防止其他卡密支付引起误会，改为当前描述。
        String ERROR_CODE_10004 = "10004";
        String ERROR_MSG_10004 = "查询方式不正确或不存在";
        String ERROR_CODE_10005 = "10005";
        String ERROR_MSG_10005 = "查询模式不正确或不存在";
        String ERROR_CODE_10006 = "10006";
        String ERROR_MSG_10006 = "查询开始时间不正确";
        String ERROR_CODE_10007 = "10007";
        String ERROR_MSG_10007 = "查询结束时间不正确";
        String ERROR_CODE_10008 = "10008";
        String ERROR_MSG_10008 = "商户订单号格式不正确";
        String ERROR_CODE_10009 = "10009";
        String ERROR_MSG_10009 = "签名字符串格式不正确";
        String ERROR_CODE_10010 = "10010";
        String ERROR_MSG_10010 = "字符集格式不正确";
        String ERROR_CODE_11001 = "11001";
        String ERROR_MSG_11001 = "开始时间不能在结束时间之后";
        String ERROR_CODE_11002 = "11002";
        String ERROR_MSG_11002 = "允许查询的时间段最长为 6 小时";
        String ERROR_CODE_11003 = "11003";
        String ERROR_MSG_11003 = "签名字符串不匹配";
        String ERROR_CODE_11004 = "11004";
        String ERROR_MSG_11004 = "错误的交易查询结束时间";
        String ERROR_CODE_20001 = "20001";
        String ERROR_MSG_20001 = "该账号不存在或已注销";
        String ERROR_CODE_20002 = "20002";
        String ERROR_MSG_20002 = "签名字符串不匹配，您无权查询";
        String ERROR_CODE_31001 = "31001";
        String ERROR_MSG_31001 = "本时间段内无交易记录";
        String ERROR_CODE_31002 = "31002";
        String ERROR_MSG_31002 = "本时间段内无成功交易记录";
        String ERROR_CODE_31003 = "31003";
        String ERROR_MSG_31003 = "商户订单号不存在";
        String ERROR_CODE_31004 = "31004";
        String ERROR_MSG_31004 = "查询结果超出能允许的文件范围";
        String ERROR_CODE_31005 = "31005";
        String ERROR_MSG_31005 = "订单号对应的交易支付未成功";
        String ERROR_CODE_31006 = "31006";
        String ERROR_MSG_31006 = "当前记录集页码不存在";
    }

    /**
     * 快钱银行直连支付返回的错误码。
     * 参考《快钱【人民币网关支付】接口文档版本（V2.0.7）》
     * 在文件“快钱人民币网关支付接口文档_V2.0.7.pdf”中
     */
    public interface BankPayResponseErrCode {
        String ERR_CODE_00000 = "00000";
        String ERR_MSG_00000 = "未知错误";
        String ERR_CODE_10001 = "10001";
        String ERR_MSG_10001 = "不支持的字符编码格式,系统支持的字符编码格式为1.UTF-8,2.GBK,3.GB2312";
        String ERR_CODE_10002 = "10002";
        String ERR_MSG_10002 = "不支持的返回类型,系统支持的返回类型为1.页面返回,2.后台返回,3.同时支持页面和后台返回";
        String ERR_CODE_10003 = "10003";
        String ERR_MSG_10003 = "页面返回地址和后台返回地址不能同时为空,请使用符合URL规则的http或者https地址";
        String ERR_CODE_10004 = "10004";
        String ERR_MSG_10004 = "后台返回地址和后台返回地址不能同时为空,请使用符合URL规则的http或者https地址";// 原始值为“页面......”，和前面的比对发现应啊设置成“后台......”
        String ERR_CODE_10005 = "10005";
        String ERR_MSG_10005 = "不支持的网关接口版本号,目前系统支持的版本号为v2.0";
        String ERR_CODE_10006 = "10006";
        String ERR_MSG_10006 = "商户号不存在";
        String ERR_CODE_10007 = "10007";
        String ERR_MSG_10007 = "付款方用户名不正确";
        String ERR_CODE_10008 = "10008";
        String ERR_MSG_10008 = "不支持的付款方联系方式,系统支持的联系方式为1.电子邮件,2.电话.当联系内容不为空时联系方式不能为空.";
        String ERR_CODE_10009 = "10009";
        String ERR_MSG_10009 = "付款方的联系内容不正确,请输入合法的联系地址";
        String ERR_CODE_10010 = "10010";
        String ERR_MSG_10010 = "30订单号不正确,系统只支持以字母,数字组合的订单号,最大长度不能超过";
        String ERR_CODE_10011 = "10011";
        String ERR_MSG_10011 = "订单金额不正确,请输入以分为单位的金额";
        String ERR_CODE_10012 = "10012";
        String ERR_MSG_10012 = "订单提交时间不正确,请输入以gbgbMMddhhmmss格式的时间字符串";
        String ERR_CODE_10013 = "10013";
        String ERR_MSG_10013 = "商品名称不正确";
        String ERR_CODE_10014 = "10014";
        String ERR_MSG_10014 = "商品数量不正确";
        String ERR_CODE_10015 = "10015";
        String ERR_MSG_10015 = "商品ID不正确";
        String ERR_CODE_10016 = "10016";
        String ERR_MSG_10016 = "商品的描述不正确";
        String ERR_CODE_10017 = "10017";
        String ERR_MSG_10017 = "扩展参数一不正确";
        String ERR_CODE_10018 = "10018";
        String ERR_MSG_10018 = "扩展参数二不正确";
        String ERR_CODE_10019 = "10019";
        String ERR_MSG_10019 = "指定的支付方式不正确";
        String ERR_CODE_10020 = "10020";
        String ERR_MSG_10020 = "指定的支付服务代码不正确";
        String ERR_CODE_10021 = "10021";
        String ERR_MSG_10021 = "指定的银行ID不正确";
        String ERR_CODE_10022 = "10022";
        String ERR_MSG_10022 = "不支持的诧言类型,系统支持的诧言为1.中文,2.英文";
        String ERR_CODE_10023 = "10023";
        String ERR_MSG_10023 = "不支持的签名类型,系统支持的签名类型为1.MD5";
        String ERR_CODE_10024 = "10024";
        String ERR_MSG_10024 = "商户未开通人民币网关";
        String ERR_CODE_10025 = "10025";
        String ERR_MSG_10025 = "商户未开通国际卡人民币网关";
        String ERR_CODE_10026 = "10026";
        String ERR_MSG_10026 = "商户未开通电话支付人民币网关";
        String ERR_CODE_10027 = "10027";
        String ERR_MSG_10027 = "不正确的pid值";
        String ERR_CODE_10028 = "10028";
        String ERR_MSG_10028 = "不正确的国际卡支付参数,组合支付方式和支付方式必须为国际卡对应的参数";
        String ERR_CODE_10029 = "10029";
        String ERR_MSG_10029 = "不正确的神州行支付参数,组合支付方式和支付方式必须为神州行支付对应的参数";
        String ERR_CODE_10030 = "10030";
        String ERR_MSG_10030 = "不正确的代理商帐户代码";
        String ERR_CODE_10031 = "10031";
        String ERR_MSG_10031 = "商户未开通代理网关";
        String ERR_CODE_10032 = "10032";
        String ERR_MSG_10032 = "原始交易不存在";
        String ERR_CODE_10033 = "10033";
        String ERR_MSG_10033 = "订单金额不正确,请输入以元为单位的金额,最多允许两位小数";
        String ERR_CODE_10034 = "10034";
        String ERR_MSG_10034 = "手续费金额不正确,请输入以元为单位的金额,最多允许两位小数";
        String ERR_CODE_10035 = "10035";
        String ERR_MSG_10035 = "手续费总额大于或等于订单金额";
        String ERR_CODE_10036 = "10036";
        String ERR_MSG_10036 = "同一订单号禁止重复提交标志不正确";
        String ERR_CODE_10037 = "10037";
        String ERR_MSG_10037 = "对不起，该订单不允许重复提交，请重新下订单提交！";
        String ERR_CODE_10038 = "10038";
        String ERR_MSG_10038 = "超过允许支付的时间范围";
        String ERR_CODE_10042 = "10042";
        String ERR_MSG_10042 = "卡号格式不正确";
        String ERR_CODE_10043 = "10043";
        String ERR_MSG_10043 = "不支持的收卡机构";
        String ERR_CODE_10044 = "10044";
        String ERR_MSG_10044 = "接口参数不正确，缺少收卡机构";
        String ERR_CODE_10045 = "10045";
        String ERR_MSG_10045 = "接口参数不正确";
        String ERR_CODE_10046 = "10046";
        String ERR_MSG_10046 = "商户没有定制支持的收卡机构";
        String ERR_CODE_10047 = "10047";
        String ERR_MSG_10047 = "商户没有定制支持的卡种";
        String ERR_CODE_10053 = "10053";
        String ERR_MSG_10053 = "平台返回地址不能为空";
        String ERR_CODE_10054 = "10054";
        String ERR_MSG_10054 = "商户签名数据不能为空";
        String ERR_CODE_10055 = "10055";
        String ERR_MSG_10055 = "扩展参数类型无效";
        String ERR_CODE_10056 = "10056";
        String ERR_MSG_10056 = "扩展参数内容格式错误";
        String ERR_CODE_10057 = "10057";
        String ERR_MSG_10057 = "不支持外币提交";
        String ERR_CODE_10058 = "10058";
        String ERR_MSG_10058 = "不支持的外币币种";
        String ERR_CODE_10059 = "10059";
        String ERR_MSG_10059 = "身份证格式不正确";
        String ERR_CODE_10060 = "10060";
        String ERR_MSG_10060 = "外币金额超过最大限制";
        String ERR_CODE_10061 = "10061";
        String ERR_MSG_10061 = "订单时间戳格式不正确,请输入以gbgbMMddHHmmss格式的时间戳";
        String ERR_CODE_10062 = "10062";
        String ERR_MSG_10062 = "您的订单已超时，请到商户网站重新提交订单";
        String ERR_CODE_10063 = "10063";
        String ERR_MSG_10063 = "付款人IP地址格式不正确";
        String ERR_CODE_10064 = "10064";
        String ERR_MSG_10064 = "未提供订单时间戳，请联系商户网站支持人员";
        String ERR_CODE_10065 = "10065";
        String ERR_MSG_10065 = "未提供付款人IP地址，请联系商用户网站支持人员";
        String ERR_CODE_20001 = "20001";
        String ERR_MSG_20001 = "订单信息的签名内容不正确";
        String ERR_CODE_20002 = "20002";
        String ERR_MSG_20002 = "商户账号已被冻结";
        String ERR_CODE_20003 = "20003";
        String ERR_MSG_20003 = "商户交易金额已超过限制";
        String ERR_CODE_20004 = "20004";
        String ERR_MSG_20004 = "商户制定的银行直连参数不正确";
        String ERR_CODE_20005 = "20005";
        String ERR_MSG_20005 = "不能使用优惠券";
        String ERR_CODE_20006 = "20006";
        String ERR_MSG_20006 = "商户账户不允许收款";
        String ERR_CODE_20007 = "20007";
        String ERR_MSG_20007 = "账户已注销";
        String ERR_CODE_20008 = "20008";
        String ERR_MSG_20008 = "订单金额小于支付手续费,不能支付";
        String ERR_CODE_20009 = "20009";
        String ERR_MSG_20009 = "商户不允许银行直连,可能商户没有对网关定制或者定制中没有选择银行直连,请联系快钱客服";
        String ERR_CODE_20010 = "20010";
        String ERR_MSG_20010 = "您通过快钱向此商户的支付金额超过支付限额,请联系快钱客服";
        String ERR_CODE_20011 = "20011";
        String ERR_MSG_20011 = "您通过快钱向此商户的支付金额超过单笔订单的支付限额,请联系快钱客服";
        String ERR_CODE_20012 = "20012";
        String ERR_MSG_20012 = "您通过快钱向此商户的支付金额超过单日的支付总限额,请联系快钱客服";
        String ERR_CODE_20013 = "20013";
        String ERR_MSG_20013 = "您通过快钱向此商户的支付金额超过单月的支付总限额,请联系快钱客服";
        String ERR_CODE_20028 = "20028";
        String ERR_MSG_20028 = "您使用的商户编号没有对ATA属性配置,请联系快钱客服";
        String ERR_CODE_30001 = "30001";
        String ERR_MSG_30001 = "银行不可用";
        String ERR_CODE_30002 = "30002";
        String ERR_MSG_30002 = "线下支付不可用";
        String ERR_CODE_30003 = "30003";
        String ERR_MSG_30003 = "电话银行不可用";
        String ERR_CODE_30004 = "30004";
        String ERR_MSG_30004 = "订单已支付成功,请勿重新支付.";
        String ERR_CODE_30005 = "30005";
        String ERR_MSG_30005 = "请填写您的电子邮箱";
        String ERR_CODE_50001 = "50001";
        String ERR_MSG_50001 = "验证码不正确";
        String ERR_CODE_50002 = "50002";
        String ERR_MSG_50002 = "用户不存在";
        String ERR_CODE_50003 = "50003";
        String ERR_MSG_50003 = "用户被冻结";
        String ERR_CODE_50004 = "50004";
        String ERR_MSG_50004 = "登录次数过多,用户已被锁定";
        String ERR_CODE_50005 = "50005";
        String ERR_MSG_50005 = "付款人不能和收款人相同";
        String ERR_CODE_50006 = "50006";
        String ERR_MSG_50006 = "你的操作已超时，请重新提交";
        String ERR_CODE_50007 = "50007";
        String ERR_MSG_50007 = "账户不正确";
        String ERR_CODE_50008 = "50008";
        String ERR_MSG_50008 = "密码不正确";
        String ERR_CODE_50009 = "50009";
        String ERR_MSG_50009 = "余额不足";
        String ERR_CODE_50010 = "50010";
        String ERR_MSG_50010 = "没有付款权限";
        String ERR_CODE_50011 = "50011";
        String ERR_MSG_50011 = "不能向个人会员付款";
        String ERR_CODE_50012 = "50012";
        String ERR_MSG_50012 = "复核错误";
        String ERR_CODE_50013 = "50013";
        String ERR_MSG_50013 = "您的快钱盾已挂失,无法登录.您可以通过叏消挂失或更换后再登录";
        String ERR_CODE_50014 = "50014";
        String ERR_MSG_50014 = "您输入的快钱盾数据不正确";
        String ERR_CODE_50015 = "50015";
        String ERR_MSG_50015 = "使用快钱盾遇到未知错误，请联系快钱客服";
        String ERR_CODE_50016 = "50016";
        String ERR_MSG_50016 = "请使用您的默讣用户名登录!";
        String ERR_CODE_50017 = "50017";
        String ERR_MSG_50017 = "复核重试次数超过3次,您不能重新复核";
        String ERR_CODE_60001 = "60001";
        String ERR_MSG_60001 = "货币种类不正确!提示1：人民币网关；3：预付费卡网关.";
        String ERR_CODE_60002 = "60002";
        String ERR_MSG_60002 = "定单号不能为空,不能超过30位长度";
        String ERR_CODE_60003 = "60003";
        String ERR_MSG_60003 = "定单号只能包含数字或字母以及中划线和下划线";
        String ERR_CODE_60004 = "60004";
        String ERR_MSG_60004 = "该笔交易金额格式不正确,必须为整数或者小数";
        String ERR_CODE_60005 = "60005";
        String ERR_MSG_60005 = "该笔交易金额格式不正确,必须为整数";
        String ERR_CODE_70001 = "70001";
        String ERR_MSG_70001 = "您输入的用户不能使用此优惠券";
        String ERR_CODE_70002 = "70002";
        String ERR_MSG_70002 = "您尝试使用优惠错误次数超过3次,请不快钱客服联系";
        String ERR_CODE_70003 = "70003";
        String ERR_MSG_70003 = "您验证优惠券的用户错误次数超过3次,请不快钱客服联系";
        String ERR_CODE_80001 = "80001";
        String ERR_MSG_80001 = "当前交易属于非法交易，请不商户网站联系";
        String ERR_CODE_80002 = "80002";
        String ERR_MSG_80002 = "当前交易属于非法交易，请不商户网站联系";
        String ERR_CODE_81000 = "81000";
        String ERR_MSG_81000 = "商户提供的汇款充值码不符合规则，请联系您的商户";
        String ERR_CODE_81001 = "81001";
        String ERR_MSG_81001 = "商户提供的汇款充值码重复，请联系您的商户";
        String ERR_CODE_81004 = "81004";
        String ERR_MSG_81004 = "商家单笔交易金额超过限制，需上传身份证影印件。";
        String ERR_CODE_81005 = "81005";
        String ERR_MSG_81005 = "商家本月累计交易金额超过限制，需上传身份证影印件";
        String ERR_CODE_82001 = "82001";
        String ERR_MSG_82001 = "收款方账户非实名";
        String ERR_CODE_82002 = "82002";
        String ERR_MSG_82002 = "付款方账户非实名";
    }

    /**
     * 快钱银行直连查询返回的错误码
     * 参考《快钱【人民币网关支付】查询接口文档 版本（V2.0.1）》
     * 在文件“订单查询接口.pdf”中
     * 
     */
    public interface BankQueryErrCode {
        String ERR_CODE_00000 = "00000";
        String ERR_MSG_00000 = "未知错误";
        String ERR_CODE_10001 = "10001";
        String ERR_MSG_10001 = "网关版本号不正确或不存在";
        String ERR_CODE_10002 = "10002";
        String ERR_MSG_10002 = "签名类型不正确或不存在";
        String ERR_CODE_10003 = "10003";
        String ERR_MSG_10003 = "人民币账号格式不正确";
        String ERR_CODE_10004 = "10004";
        String ERR_MSG_10004 = "查询方式不正确或不存在";
        String ERR_CODE_10005 = "10005";
        String ERR_MSG_10005 = "查询模式不正确或不存在";
        String ERR_CODE_10006 = "10006";
        String ERR_MSG_10006 = "查询开始时间不正确";
        String ERR_CODE_10007 = "10007";
        String ERR_MSG_10007 = "查询结束时间不正确";
        String ERR_CODE_10008 = "10008";
        String ERR_MSG_10008 = "商户订单号格式不正确";
        String ERR_CODE_10010 = "10010";
        String ERR_MSG_10010 = "字符集输入不正确";
        String ERR_CODE_11001 = "11001";
        String ERR_MSG_11001 = "开始时间不能在结束时间之后";
        String ERR_CODE_11002 = "11002";
        String ERR_MSG_11002 = "允许查询的时间段最长为30天";
        String ERR_CODE_11003 = "11003";
        String ERR_MSG_11003 = "签名字符串不匹配";
        String ERR_CODE_11004 = "11004";
        String ERR_MSG_11004 = "查询结束时间晚于当前时间";
        String ERR_CODE_20001 = "20001";
        String ERR_MSG_20001 = "该账号不存在或已注销";
        String ERR_CODE_20002 = "20002";
        String ERR_MSG_20002 = "签名字符串不匹配，您无权查询";
        String ERR_CODE_30001 = "30001";
        String ERR_MSG_30001 = "系统繁忙，请稍后再查询";
        String ERR_CODE_30002 = "30002";
        String ERR_MSG_30002 = "查询过程异常，请稍后再试";
        String ERR_CODE_31001 = "31001";
        String ERR_MSG_31001 = "本时间段内无交易记录";
        String ERR_CODE_31002 = "31002";
        String ERR_MSG_31002 = "本时间段内无成功交易记录";
        String ERR_CODE_31003 = "31003";
        String ERR_MSG_31003 = "商户订单号不存在";
        String ERR_CODE_31004 = "31004";
        String ERR_MSG_31004 = "查询结果超出能允许的文件范围";
        String ERR_CODE_31005 = "31005";
        String ERR_MSG_31005 = "订单号对应的交易支付未成功";
        String ERR_CODE_31006 = "31006";
        String ERR_MSG_31006 = "当前记录集页码不存在";

    }

    /**
     * 翻译卡密的查询错误码
     */
    public static String translateCardQueryResponseErrorCode(String errorCode) {
        if (CardQueryResponseErrorCode.ERROR_CODE_00000.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_00000;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10001.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10001;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10002.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10002;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10003.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10003;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10004.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10004;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10005.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10005;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10006.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10006;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10007.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10007;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10008.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10008;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10009.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10009;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_10010.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_10010;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_11001.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_11001;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_11002.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_11002;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_11003.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_11003;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_11004.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_11004;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_20001.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_20001;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_20002.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_20002;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_31001.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_31001;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_31002.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_31002;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_31003.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_31003;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_31004.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_31004;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_31005.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_31005;
        } else if (CardQueryResponseErrorCode.ERROR_CODE_31006.equalsIgnoreCase(errorCode)) {
            return CardQueryResponseErrorCode.ERROR_MSG_31006;
        } else {
            return "支付平台：未知的快钱返回码，errorCode：" + errorCode;
        }
    }

    /**
     * 翻译卡密返回状态码
     * 
     * @param returnCode
     * @return
     */

    public static String translateCardPayResponseReturnCode(String returnCode) {
        if (CardPayResponseReturnCode.RETURN_CODE_00000.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_00000;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10001.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10001;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10002.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10002;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10003.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10003;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10004.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10004;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10005.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10005;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10006.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10006;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10007.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10007;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10010.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10010;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10011.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10011;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10012.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10012;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10013.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10013;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10014.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10014;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10015.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10015;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10016.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10016;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10017.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10017;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10018.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10018;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10019.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10019;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10020.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10020;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10021.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10021;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10022.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10022;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10023.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10023;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10024.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10024;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10025.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10025;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10026.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10026;
        } else if (CardPayResponseReturnCode.RETURN_CODE_10027.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_10027;
        } else if (CardPayResponseReturnCode.RETURN_CODE_20001.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_20001;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30001.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30001;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30002.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30002;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30003.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30003;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30004.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30004;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30005.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30005;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30006.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30006;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30007.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30007;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30008.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30008;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30009.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30009;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30010.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30010;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30011.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30011;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30012.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30012;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30013.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30013;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30014.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30014;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30015.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30015;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30016.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30016;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30017.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30017;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30018.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30018;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30019.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30019;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30020.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30020;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30021.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30021;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30022.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30022;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30023.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30023;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30024.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30024;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30025.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30025;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30026.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30026;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30027.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30027;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30028.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30028;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30029.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30029;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30030.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30030;
        } else if (CardPayResponseReturnCode.RETURN_CODE_30031.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_30031;
        } else if (CardPayResponseReturnCode.RETURN_CODE_0.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_0;
        } else if (CardPayResponseReturnCode.RETURN_CODE_1.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_1;
        } else if (CardPayResponseReturnCode.RETURN_CODE_2.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_2;
        } else if (CardPayResponseReturnCode.RETURN_CODE_3.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_3;
        } else if (CardPayResponseReturnCode.RETURN_CODE_4.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_4;
        } else if (CardPayResponseReturnCode.RETURN_CODE_5.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_5;
        } else if (CardPayResponseReturnCode.RETURN_CODE_120.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_120;
        } else if (CardPayResponseReturnCode.RETURN_CODE_121.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_121;
        } else if (CardPayResponseReturnCode.RETURN_CODE_122.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_122;
        } else if (CardPayResponseReturnCode.RETURN_CODE_123.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_123;
        } else if (CardPayResponseReturnCode.RETURN_CODE_126.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_126;
        } else if (CardPayResponseReturnCode.RETURN_CODE_500.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_500;
        } else if (CardPayResponseReturnCode.RETURN_CODE_101.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_101;
        } else if (CardPayResponseReturnCode.RETURN_CODE_102.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_102;
        } else if (CardPayResponseReturnCode.RETURN_CODE_103.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_103;
        } else if (CardPayResponseReturnCode.RETURN_CODE_104.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_104;
        } else if (CardPayResponseReturnCode.RETURN_CODE_105.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_105;
        } else if (CardPayResponseReturnCode.RETURN_CODE_106.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_106;
        } else if (CardPayResponseReturnCode.RETURN_CODE_107.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_107;
        } else if (CardPayResponseReturnCode.RETURN_CODE_108.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_108;
        } else if (CardPayResponseReturnCode.RETURN_CODE_110.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_110;
        } else if (CardPayResponseReturnCode.RETURN_CODE_111.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_111;
        } else if (CardPayResponseReturnCode.RETURN_CODE_206.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_206;
        } else if (CardPayResponseReturnCode.RETURN_CODE_207.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_207;
        } else if (CardPayResponseReturnCode.RETURN_CODE_208.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_208;
        } else if (CardPayResponseReturnCode.RETURN_CODE_209.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_209;
        } else if (CardPayResponseReturnCode.RETURN_CODE_210.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_210;
        } else if (CardPayResponseReturnCode.RETURN_CODE_211.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_211;
        } else if (CardPayResponseReturnCode.RETURN_CODE_212.equalsIgnoreCase(returnCode)) {
            return CardPayResponseReturnCode.RETURN_MSG_212;
        } else {
            return "支付平台：未知的支付返回码，returnCode：" + returnCode;
        }
    }

    /**
     * 翻译银行直连返回错误码。
     */
    public static String translateBankPayResponseErrCode(String errCode) {
        if (BankPayResponseErrCode.ERR_CODE_00000.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_00000;
        } else if (BankPayResponseErrCode.ERR_CODE_10001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10001;
        } else if (BankPayResponseErrCode.ERR_CODE_10002.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10002;
        } else if (BankPayResponseErrCode.ERR_CODE_10003.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10003;
        } else if (BankPayResponseErrCode.ERR_CODE_10004.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10004;
        } else if (BankPayResponseErrCode.ERR_CODE_10005.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10005;
        } else if (BankPayResponseErrCode.ERR_CODE_10006.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10006;
        } else if (BankPayResponseErrCode.ERR_CODE_10007.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10007;
        } else if (BankPayResponseErrCode.ERR_CODE_10008.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10008;
        } else if (BankPayResponseErrCode.ERR_CODE_10009.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10009;
        } else if (BankPayResponseErrCode.ERR_CODE_10010.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10010;
        } else if (BankPayResponseErrCode.ERR_CODE_10011.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10011;
        } else if (BankPayResponseErrCode.ERR_CODE_10012.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10012;
        } else if (BankPayResponseErrCode.ERR_CODE_10013.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10013;
        } else if (BankPayResponseErrCode.ERR_CODE_10014.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10014;
        } else if (BankPayResponseErrCode.ERR_CODE_10015.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10015;
        } else if (BankPayResponseErrCode.ERR_CODE_10016.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10016;
        } else if (BankPayResponseErrCode.ERR_CODE_10017.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10017;
        } else if (BankPayResponseErrCode.ERR_CODE_10018.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10018;
        } else if (BankPayResponseErrCode.ERR_CODE_10019.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10019;
        } else if (BankPayResponseErrCode.ERR_CODE_10020.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10020;
        } else if (BankPayResponseErrCode.ERR_CODE_10021.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10021;
        } else if (BankPayResponseErrCode.ERR_CODE_10022.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10022;
        } else if (BankPayResponseErrCode.ERR_CODE_10023.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10023;
        } else if (BankPayResponseErrCode.ERR_CODE_10024.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10024;
        } else if (BankPayResponseErrCode.ERR_CODE_10025.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10025;
        } else if (BankPayResponseErrCode.ERR_CODE_10026.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10026;
        } else if (BankPayResponseErrCode.ERR_CODE_10027.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10027;
        } else if (BankPayResponseErrCode.ERR_CODE_10028.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10028;
        } else if (BankPayResponseErrCode.ERR_CODE_10029.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10029;
        } else if (BankPayResponseErrCode.ERR_CODE_10030.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10030;
        } else if (BankPayResponseErrCode.ERR_CODE_10031.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10031;
        } else if (BankPayResponseErrCode.ERR_CODE_10032.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10032;
        } else if (BankPayResponseErrCode.ERR_CODE_10033.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10033;
        } else if (BankPayResponseErrCode.ERR_CODE_10034.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10034;
        } else if (BankPayResponseErrCode.ERR_CODE_10035.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10035;
        } else if (BankPayResponseErrCode.ERR_CODE_10036.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10036;
        } else if (BankPayResponseErrCode.ERR_CODE_10037.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10037;
        } else if (BankPayResponseErrCode.ERR_CODE_10038.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10038;
        } else if (BankPayResponseErrCode.ERR_CODE_10042.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10042;
        } else if (BankPayResponseErrCode.ERR_CODE_10043.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10043;
        } else if (BankPayResponseErrCode.ERR_CODE_10044.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10044;
        } else if (BankPayResponseErrCode.ERR_CODE_10045.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10045;
        } else if (BankPayResponseErrCode.ERR_CODE_10046.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10046;
        } else if (BankPayResponseErrCode.ERR_CODE_10047.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10047;
        } else if (BankPayResponseErrCode.ERR_CODE_10053.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10053;
        } else if (BankPayResponseErrCode.ERR_CODE_10054.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10054;
        } else if (BankPayResponseErrCode.ERR_CODE_10055.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10055;
        } else if (BankPayResponseErrCode.ERR_CODE_10056.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10056;
        } else if (BankPayResponseErrCode.ERR_CODE_10057.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10057;
        } else if (BankPayResponseErrCode.ERR_CODE_10058.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10058;
        } else if (BankPayResponseErrCode.ERR_CODE_10059.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10059;
        } else if (BankPayResponseErrCode.ERR_CODE_10060.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10060;
        } else if (BankPayResponseErrCode.ERR_CODE_10061.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10061;
        } else if (BankPayResponseErrCode.ERR_CODE_10062.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10062;
        } else if (BankPayResponseErrCode.ERR_CODE_10063.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10063;
        } else if (BankPayResponseErrCode.ERR_CODE_10064.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10064;
        } else if (BankPayResponseErrCode.ERR_CODE_10065.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_10065;
        } else if (BankPayResponseErrCode.ERR_CODE_20001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20001;
        } else if (BankPayResponseErrCode.ERR_CODE_20002.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20002;
        } else if (BankPayResponseErrCode.ERR_CODE_20003.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20003;
        } else if (BankPayResponseErrCode.ERR_CODE_20004.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20004;
        } else if (BankPayResponseErrCode.ERR_CODE_20005.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20005;
        } else if (BankPayResponseErrCode.ERR_CODE_20006.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20006;
        } else if (BankPayResponseErrCode.ERR_CODE_20007.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20007;
        } else if (BankPayResponseErrCode.ERR_CODE_20008.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20008;
        } else if (BankPayResponseErrCode.ERR_CODE_20009.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20009;
        } else if (BankPayResponseErrCode.ERR_CODE_20010.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20010;
        } else if (BankPayResponseErrCode.ERR_CODE_20011.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20011;
        } else if (BankPayResponseErrCode.ERR_CODE_20012.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20012;
        } else if (BankPayResponseErrCode.ERR_CODE_20013.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20013;
        } else if (BankPayResponseErrCode.ERR_CODE_20028.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_20028;
        } else if (BankPayResponseErrCode.ERR_CODE_30001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_30001;
        } else if (BankPayResponseErrCode.ERR_CODE_30002.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_30002;
        } else if (BankPayResponseErrCode.ERR_CODE_30003.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_30003;
        } else if (BankPayResponseErrCode.ERR_CODE_30004.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_30004;
        } else if (BankPayResponseErrCode.ERR_CODE_30005.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_30005;
        } else if (BankPayResponseErrCode.ERR_CODE_50001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50001;
        } else if (BankPayResponseErrCode.ERR_CODE_50002.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50002;
        } else if (BankPayResponseErrCode.ERR_CODE_50003.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50003;
        } else if (BankPayResponseErrCode.ERR_CODE_50004.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50004;
        } else if (BankPayResponseErrCode.ERR_CODE_50005.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50005;
        } else if (BankPayResponseErrCode.ERR_CODE_50006.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50006;
        } else if (BankPayResponseErrCode.ERR_CODE_50007.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50007;
        } else if (BankPayResponseErrCode.ERR_CODE_50008.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50008;
        } else if (BankPayResponseErrCode.ERR_CODE_50009.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50009;
        } else if (BankPayResponseErrCode.ERR_CODE_50010.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50010;
        } else if (BankPayResponseErrCode.ERR_CODE_50011.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50011;
        } else if (BankPayResponseErrCode.ERR_CODE_50012.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50012;
        } else if (BankPayResponseErrCode.ERR_CODE_50013.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50013;
        } else if (BankPayResponseErrCode.ERR_CODE_50014.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50014;
        } else if (BankPayResponseErrCode.ERR_CODE_50015.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50015;
        } else if (BankPayResponseErrCode.ERR_CODE_50016.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50016;
        } else if (BankPayResponseErrCode.ERR_CODE_50017.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_50017;
        } else if (BankPayResponseErrCode.ERR_CODE_60001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_60001;
        } else if (BankPayResponseErrCode.ERR_CODE_60002.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_60002;
        } else if (BankPayResponseErrCode.ERR_CODE_60003.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_60003;
        } else if (BankPayResponseErrCode.ERR_CODE_60004.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_60004;
        } else if (BankPayResponseErrCode.ERR_CODE_60005.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_60005;
        } else if (BankPayResponseErrCode.ERR_CODE_70001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_70001;
        } else if (BankPayResponseErrCode.ERR_CODE_70002.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_70002;
        } else if (BankPayResponseErrCode.ERR_CODE_70003.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_70003;
        } else if (BankPayResponseErrCode.ERR_CODE_80001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_80001;
        } else if (BankPayResponseErrCode.ERR_CODE_80002.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_80002;
        } else if (BankPayResponseErrCode.ERR_CODE_81000.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_81000;
        } else if (BankPayResponseErrCode.ERR_CODE_81001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_81001;
        } else if (BankPayResponseErrCode.ERR_CODE_81004.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_81004;
        } else if (BankPayResponseErrCode.ERR_CODE_81005.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_81005;
        } else if (BankPayResponseErrCode.ERR_CODE_82001.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_82001;
        } else if (BankPayResponseErrCode.ERR_CODE_82002.equalsIgnoreCase(errCode)) {
            return BankPayResponseErrCode.ERR_MSG_82002;
        } else {
            return "支付平台：不能识别的银行直连返回码，errCode：" + errCode;
        }
    }

    /**
     * 翻译直连银行查询错误码。
     */
    public static String translateBankQueryErrCode(String errCode) {
        if (BankQueryErrCode.ERR_CODE_00000.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_00000;
        } else if (BankQueryErrCode.ERR_CODE_10001.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10001;
        } else if (BankQueryErrCode.ERR_CODE_10002.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10002;
        } else if (BankQueryErrCode.ERR_CODE_10003.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10003;
        } else if (BankQueryErrCode.ERR_CODE_10004.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10004;
        } else if (BankQueryErrCode.ERR_CODE_10005.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10005;
        } else if (BankQueryErrCode.ERR_CODE_10006.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10006;
        } else if (BankQueryErrCode.ERR_CODE_10007.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10007;
        } else if (BankQueryErrCode.ERR_CODE_10008.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10008;
        } else if (BankQueryErrCode.ERR_CODE_10010.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_10010;
        } else if (BankQueryErrCode.ERR_CODE_11001.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_11001;
        } else if (BankQueryErrCode.ERR_CODE_11002.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_11002;
        } else if (BankQueryErrCode.ERR_CODE_11003.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_11003;
        } else if (BankQueryErrCode.ERR_CODE_11004.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_11004;
        } else if (BankQueryErrCode.ERR_CODE_20001.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_20001;
        } else if (BankQueryErrCode.ERR_CODE_20002.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_20002;
        } else if (BankQueryErrCode.ERR_CODE_30001.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_30001;
        } else if (BankQueryErrCode.ERR_CODE_30002.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_30002;
        } else if (BankQueryErrCode.ERR_CODE_31001.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_31001;
        } else if (BankQueryErrCode.ERR_CODE_31002.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_31002;
        } else if (BankQueryErrCode.ERR_CODE_31003.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_31003;
        } else if (BankQueryErrCode.ERR_CODE_31004.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_31004;
        } else if (BankQueryErrCode.ERR_CODE_31005.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_31005;
        } else if (BankQueryErrCode.ERR_CODE_31006.equalsIgnoreCase(errCode)) {
            return BankQueryErrCode.ERR_MSG_31006;
        } else {
            return "支付平台：不能识别的银行直连查询状态码，errCode：" + errCode;
        }
    }
}
