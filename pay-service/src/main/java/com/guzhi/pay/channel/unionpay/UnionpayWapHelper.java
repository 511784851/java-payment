/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.unionpay;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.channel.Help;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.MD5Utils;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.TimeHelper;
import com.guzhi.pay.helper.UrlHelper;

/**
 * @author 
 * 
 */
public class UnionpayWapHelper {
    private static final Logger LOG = LoggerFactory.getLogger(UnionpayWapHelper.class);
    private static final String TRANS_Type = "01";
    private static final String AMP = "&";
    private static final String EQ = "=";

    /**
     * Generate pay request body.
     * 
     * @param payOrder
     * @return
     */
    public static String generatePayRequestBody(PayOrder payOrder, Map<String, String> request) {
        String merchantId = payOrder.getAppChInfo().getChAccountId();
        String base64MerchantId = baseMerchantId(payOrder);
        String desKey = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), Constants.DESKEY);
        desKey = MD5Utils.getMD5(desKey);
        String content = generatePayContent(payOrder, request);
        String payKey = payOrder.getAppChInfo().getChPayKeyMd5();
        try {
            String base64DesKey = Base64.encodeBase64String(EncryptUtil.encryptByRSA(
                    desKey.getBytes(Consts.CHARSET_UTF8), UnionpayWapAdapter.PRIVATEKEYFILEPATH, payKey));
            String base64Content = Base64.encodeBase64String(EncryptUtil.encryptBy3DES(desKey.getBytes(),
                    content.getBytes(Consts.CHARSET_UTF8)));
            String requestBody = base64MerchantId + "|" + base64DesKey + "|" + base64Content;
            LOG.info("[generatePayRequestBody] merchantId:{},content:{},encrypt:{}", merchantId, content, requestBody);
            return requestBody;
        } catch (Exception e) {
            LOG.info("[generatePayRequestBody] encrypt or encode error {}", e.getMessage());
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
    }

    /**
     * 
     * @param payOrder
     * @return
     */
    private static String baseMerchantId(PayOrder payOrder) {
        String base64MerchantId = null;
        try {
            base64MerchantId = new String(Base64.encodeBase64(payOrder.getAppChInfo().getChAccountId()
                    .getBytes(Consts.CHARSET_UTF8)));
        } catch (UnsupportedEncodingException e) {
            LOG.error("[UnsupportedEncodingException] encrypt or encode error {}", e.getMessage());
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }
        return base64MerchantId;
    }

    /**
     * Convert monetary unit from CNY to FEN,for example,convert "100.01" to
     * "10001".
     * 
     * @param amount
     * @return
     */
    public static String generateCentAmountString(BigDecimal amount) {
        String plainAmount = amount.toPlainString();
        Float floatCentAmount = Float.valueOf(plainAmount) * 100;
        return String.valueOf(floatCentAmount.intValue());
    }

    private static String generatePayContent(PayOrder payOrder, Map<String, String> request) {
        String sendTime = TimeHelper.get(8, new Date());
        // 标识报文发送的唯一编号
        String sendSeqId = sendTime + String.valueOf(10000 + RandomUtils.nextInt(90000));
        Document doc = null;
        doc = DOMDocumentFactory.getInstance().createDocument(Consts.CHARSET_UTF8);
        Element root = doc.addElement(Constants.UPBP);
        root.addAttribute(Constants.KEY_APPLICATION, Constants.PAY_REQ_APPLICATION);
        root.addAttribute(Constants.KEY_VERSION, Constants.VERSION);
        root.addAttribute(Constants.KEY_SENDTIME, sendTime);
        root.addAttribute(Constants.KEY_SENDSEQID, sendSeqId);
        for (String key : request.keySet()) {
            String value = request.get(key);
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            root.addElement(key).addText(value);
        }
        String xml = doc.asXML();
        LOG.info("pay content xml:{}", xml);
        return xml;
    }

    /**
     * Generate notify request map,and send response to unionpay.
     * 
     * @param req
     * @param resp
     * @return
     */
    public static Map<String, String> generateNotifyRequestMap(HttpServletRequest req, HttpServletResponse resp) {
        Map<String, String> requestMap = new HashMap<String, String>();
        try {
            byte[] b = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int packageSize = -1;
            while ((packageSize = req.getInputStream().read(b)) > -1) {
                baos.write(b, 0, packageSize);
                baos.flush();
            }
            String requestBody = new String(baos.toByteArray());
            LOG.info("[generateNotifyRequestMap] get request body:{}", requestBody);
            String[] strArray = requestBody.split("\\|");
            String base64MerchantId = strArray[0];
            String base64DesKey = strArray[1];
            String base64Content = strArray[2];
            String merchantId = new String(Base64.decodeBase64(base64MerchantId));
            byte[] desKey = EncryptUtil.decryptByRSA(Base64.decodeBase64(base64DesKey),
                    UnionpayWapAdapter.PUBLICKEYFILEPATH);
            String content = new String(EncryptUtil.decryptBy3DES(desKey, Base64.decodeBase64(base64Content)));
            LOG.info(
                    "[generateNotifyRequestMap] get notify,base64MerchantId:{},base64Content:{},merchantId:{},content:{}",
                    base64MerchantId, base64Content, merchantId, content);
            requestMap = getRequestMap(content);
            requestMap.put(Constants.DESKEY, new String(desKey));
        } catch (Exception e) {
            LOG.error("[generateNotifyRequestMap] error occurs while decrypting.", e.getMessage());
            throw new PayException(Consts.SC.DATA_ERROR, e.getMessage());
        }
        return requestMap;
    }

    /**
     * 返回响应
     * 
     * @param requestMap
     * @return
     */
    public static String getNotifyResponse(Map<String, String> requestMap) {
        try {
            Document respDoc = DocumentHelper.createDocument();
            Element respRoot = respDoc.addElement("upbp");
            respRoot.addAttribute(Constants.KEY_APPLICATION, Constants.NOTIFY_RSP_APPLICATION);
            respRoot.addAttribute(Constants.KEY_VERSION, Constants.VERSION);
            respRoot.addAttribute(Constants.KEY_SENDTIME, requestMap.get(Constants.KEY_SENDTIME));
            respRoot.addAttribute(Constants.KEY_SENDSEQID, requestMap.get(Constants.KEY_SENDSEQID));
            respRoot.addElement(Constants.KEY_TRANSTYPE).addText(requestMap.get(Constants.KEY_TRANSTYPE));
            respRoot.addElement(Constants.KEY_MERCHANTID).addText(requestMap.get(Constants.KEY_MERCHANTID));
            respRoot.addElement(Constants.KEY_MERCHANTORDERID).addText(requestMap.get(Constants.KEY_MERCHANTORDERID));
            respRoot.addElement(Constants.KEY_RESPCODE).addText(Constants.NOTIFY_RSP_SUCCESS_CODE);
            respRoot.addElement(Constants.KEY_RESPDESC).addText(Constants.NOTIFY_RSP_SUCCESS_DESC);
            String respContent = respDoc.asXML();
            LOG.info("[generateNotifyRequestMap] get response content:{}", respContent);
            String base64RespContent = Base64.encodeBase64String(EncryptUtil.encryptBy3DES(
                    requestMap.get(Constants.DESKEY).getBytes(), respContent.getBytes()));
            String base64MD5RespContent = Base64.encodeBase64String(MD5Utils.getMD5(respContent).getBytes());
            String responseBody = "1" + "|" + base64RespContent + "|" + base64MD5RespContent;
            LOG.info("[generateNotifyRequestMap] send response to unionpay,responseBody:{}", responseBody);
            return responseBody;
        } catch (Exception e) {
            throw new PayException(Consts.SC.DATA_ERROR, e.getMessage());
        }
    }

    /**
     * 获取请求参数
     * 
     * @param content
     * @return
     */
    private static Map<String, String> getRequestMap(String content) {
        Map<String, String> requestMap = new HashMap<String, String>();
        Document doc;
        try {
            doc = DocumentHelper.parseText(content);
            Element root = doc.getRootElement();
            for (int i = 0; i < root.attributeCount(); i++) {
                Attribute attribute = root.attribute(i);
                requestMap.put(attribute.getName(), attribute.getText());
            }

            for (int i = 0; i < root.nodeCount(); i++) {
                Node node = root.node(i);
                requestMap.put(node.getName(), node.getText());
            }
            return requestMap;
        } catch (DocumentException e) {
            throw new PayException(Consts.SC.DATA_ERROR, e.getMessage());
        }

    }

    /**
     * @param responseBody
     * @param payOrder
     */
    public static void updatePayOrderWithPayResponse(String responseBody, PayOrder payOrder) {
        String[] strArray = responseBody.split("\\|");
        if (strArray.length != 3) {
            throw new PayException(Consts.SC.CHANNEL_ERROR, responseBody);
        }
        if (Constants.ERROR.equalsIgnoreCase(strArray[0])) {
            String resultCode = strArray[1];
            String resultMsg = new String(Base64.decodeBase64(strArray[2]));

            LOG.error("[updatePayOrder] request upbp error,code:{},message:{},payOder:{}", resultCode, resultMsg,
                    payOrder);

            throw new PayException(Consts.SC.DATA_ERROR, resultMsg, payOrder);
        }
        String desKey = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), Constants.DESKEY);
        desKey = MD5Utils.getMD5(desKey);
        String content = null;
        try {
            content = new String(EncryptUtil.decryptBy3DES(desKey.getBytes(Consts.CHARSET_UTF8),
                    Base64.decodeBase64(strArray[1])));
            // Verifying MD5 sign of content necessary.
            LOG.info("[updatePayOrder] decrypt package success.content:{},payOrder:{}", content, payOrder);
            String payUrl = getPayUrl(content, payOrder);
            LOG.info("UnionpayWap payurl:{}", payUrl);
            payOrder.setStatusCode(Consts.SC.PENDING);
            payOrder.setStatusMsg(Constants.PREPARE_SUCCESS_MSG);
            payOrder.setPayUrl(payUrl);
        } catch (Exception e) {
            LOG.error("[updatePayOrder] parse package failed.content:{},payOrder:{}", content, payOrder);
        }
    }

    /**
     * 解析payurl
     * 
     * @param content
     * @param payOrder
     * @return
     */
    private static String getPayUrl(String content, PayOrder payOrder) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(content);
        } catch (DocumentException e) {
            LOG.error("DocumentHelper parse error {}", content);
            throw new PayException(Consts.SC.DATA_ERROR, content);
        }
        Element upbp = doc.getRootElement();
        if (null == upbp) {
            LOG.error("[updatePayOrder] get a null root element.content:{},payOrder:{}", content, payOrder);
            throw new PayException(Consts.SC.DATA_ERROR, content);
        }
        String redirectPage = upbp.elementText(Constants.GWINVOKECMD);
        if (StringUtils.isEmpty(redirectPage)) {
            LOG.error("[updatePayOrder] get a null gwInvokeCmd element.content:{},payOrder:{}", content, payOrder);
            throw new PayException(Consts.SC.DATA_ERROR, content);
        }
        return redirectPage;
    }

    /**
     * @param payOrder
     */
    public static String generateQueryRequestBody(PayOrder payOrder) {
        String content = getQueryContent(payOrder);
        String base64MerchantId = baseMerchantId(payOrder);
        String desKey = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), Constants.DESKEY);
        desKey = MD5Utils.getMD5(desKey);
        String payKey = payOrder.getAppChInfo().getChPayKeyMd5();
        String base64EncryptDesKey = null;
        String base64EncryptContent = null;
        try {
            base64EncryptDesKey = Base64.encodeBase64String(EncryptUtil.encryptByRSA(
                    desKey.getBytes(Consts.CHARSET_UTF8), UnionpayWapAdapter.PRIVATEKEYFILEPATH, payKey));
            base64EncryptContent = Base64.encodeBase64String(EncryptUtil.encryptBy3DES(
                    desKey.getBytes(Consts.CHARSET_UTF8), content.getBytes(Consts.CHARSET_UTF8)));
        } catch (UnsupportedEncodingException e) {
            LOG.error("[UnsupportedEncodingException] encrypt or encode error {}", e.getMessage());
            throw new PayException(Consts.SC.CHANNEL_ERROR, e.getMessage());
        }

        String requestBody = base64MerchantId + "|" + base64EncryptDesKey + "|" + base64EncryptContent;
        LOG.info("[generateQueryRequestBody] generate success, requestBody:{},payOrder:{}", requestBody, payOrder);
        return requestBody;
    }

    /**
     * 组装查询请求
     * 
     * @param payOrder
     * @return
     */
    private static String getQueryContent(PayOrder payOrder) {
        String sendTime = TimeHelper.get(8, new Date());
        String sendSeqId = sendTime + String.valueOf(100 + RandomUtils.nextInt(900));
        String merchantId = payOrder.getAppChInfo().getChAccountId();
        Document doc = null;
        doc = DOMDocumentFactory.getInstance().createDocument();
        Element root = doc.addElement(Constants.UPBP);
        root.addAttribute(Constants.KEY_APPLICATION, Constants.QUERY_APPLICATION);
        root.addAttribute(Constants.KEY_VERSION, Constants.VERSION);
        root.addAttribute(Constants.KEY_SENDTIME, sendTime);
        root.addAttribute(Constants.KEY_SENDSEQID, sendSeqId);
        root.addElement(Constants.KEY_TRANSTYPE).addText(TRANS_Type);
        root.addElement(Constants.KEY_MERCHANTNAME).addText(payOrder.getAppChInfo().getChAccountName());
        root.addElement(Constants.KEY_MERCHANTID).addText(merchantId);
        root.addElement(Constants.KEY_MERCHANTORDERID).addText(payOrder.getChOrderId());
        root.addElement(Constants.KEY_MERCHANTORDERTIME).addText(payOrder.getSubmitTime());
        String content = doc.asXML();
        LOG.info("[generateQueryRequestBody] generate content success, content:{},payOrder:{}", content, payOrder);
        return content;
    }

    /**
     * @param responseBody
     * @param payOrder
     */
    public static void updatePayOrderWithQueryResponse(String responseBody, PayOrder payOrder) {
        String[] strArray = responseBody.split("\\|");
        if (Constants.ERROR.equalsIgnoreCase(strArray[0])) {
            LOG.error("[updatePayOrderWithQueryResponse] request upbp error,code:{},message:{},payOder:{}",
                    strArray[1], Base64.decodeBase64(strArray[2]), payOrder);
            throw new PayException(Consts.SC.DATA_ERROR, "request upbp error", payOrder);
        }
        String desKey = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), Constants.DESKEY);
        desKey = MD5Utils.getMD5(desKey);
        String content = new String(EncryptUtil.decryptBy3DES(desKey.getBytes(), Base64.decodeBase64(strArray[1])));
        // Verifying MD5 sign of content necessary.
        LOG.info("[updatePayOrderWithQueryResponse] decrypt package success.content:{},payOrder:{}", content, payOrder);
        Document doc;
        try {
            doc = DocumentHelper.parseText(content);
            Element upbp = doc.getRootElement();
            if (null == upbp) {
                LOG.error("[updatePayOrderWithQueryResponse] get a null root element.content:{},payOrder:{}", content,
                        payOrder);
                throw new PayException(Consts.SC.DATA_ERROR, content);
            }
            String responseCode = upbp.elementText(Constants.RESPCODE);
            String queryResult = upbp.elementText(Constants.QUERY_RESULT);
            // String cupsRespCode = upbp.elementText(Constants.CUPSRESPCODE);
            if (StringUtils.isNotEmpty(responseCode) && responseCode.equals("0000")) {
                if (Constants.QueryResult.QUERYRESULT_CODE_0.equalsIgnoreCase(queryResult)) {
                    payOrder.setChDealTime(TimeHelper.get(8, new Date()));
                    payOrder.setStatusCode(Consts.SC.SUCCESS);
                    payOrder.setStatusMsg("银联：" + Constants.QueryResult.QUERYRESULT_MSG_0);
                    LOG.info("[updatePayOrderWithQueryResponse] pay success,payOrder:{}", payOrder);

                }
                // 订单不存在，合并到无此交易，返回pending
                // else if
                // (Constants.QueryResult.QUERYRESULT_CODE_1.equalsIgnoreCase(queryResult))
                // {
                // payOrder.setStatusCode(Consts.SC.FAIL);
                // payOrder.setStatusMsg("银联：" +
                // Constants.QueryResult.QUERYRESULT_MSG_1 + "；CUPS："
                // + translateCupsRespCode(cupsRespCode));
                // LOG.info("[updatePayOrderWithQueryResponse] pay failed,payOrder:{}",
                // payOrder);
                //
                // }
                else if (Constants.QueryResult.QUERYRESULT_CODE_2.equalsIgnoreCase(queryResult)) {
                    payOrder.setStatusCode(Consts.SC.PENDING);
                    payOrder.setStatusMsg("银联：" + Constants.QueryResult.QUERYRESULT_MSG_2);
                    LOG.info("[updatePayOrderWithQueryResponse] pay pending,payOrder:{}", payOrder);

                } else if (Constants.QueryResult.QUERYRESULT_CODE_3.equalsIgnoreCase(queryResult)
                        || Constants.QueryResult.QUERYRESULT_CODE_1.equalsIgnoreCase(queryResult)) {

                    payOrder.setStatusMsg("银联：" + Constants.QueryResult.QUERYRESULT_MSG_3);
                    LOG.warn("[updatePayOrderWithQueryResponse] pay order not exists in unionpay,payOrder:{}", payOrder);
                }
            } else {
                LOG.error("[updatePayOrderWithQueryResponse] get a error while query.responseCode:{},payOrder:{}",
                        responseCode, payOrder);
            }
        } catch (DocumentException e) {
            LOG.error("[updatePayOrderWithQueryResponse] parse package failed.content:{},payOrder:{}", content,
                    payOrder);
        }
    }

    /**
     * 根据通知结果组装payorder
     * 
     * @param resource
     * @param requestMap
     * @return
     */
    public static PayOrder assemblePayorderByNotify(DomainResource resource, Map<String, String> requestMap) {
        String application = requestMap.get(Constants.KEY_APPLICATION);
        String merchantOrderId = requestMap.get(Constants.KEY_MERCHANTORDERID);
        if (application.equals(Constants.NOTIFY_REQ_APPLICATION)) {
            PayOrder payOrder = Help.getPayOrderByNotify(resource, merchantOrderId);
            if (Consts.SC.SUCCESS.equalsIgnoreCase(payOrder.getStatusCode())) {
                return payOrder;
            }
            String cupsRespCode = requestMap.get(Constants.CUPSRESPCODE);
            // normal situation
            if (Constants.CupsStatus.CUPS_CODE_00.equalsIgnoreCase(cupsRespCode)) {
                String dealTime = requestMap.get(Constants.CPUSTRACETIME);
                // 解决支付通知时间问题
                payOrder.setChDealTime(assembleDealTime(dealTime));

                LOG.info("chDealTime:{},apporderid:{},notfiyDataMap:{}", requestMap.get(Constants.CPUSTRACETIME),
                        payOrder.getAppOrderId(), requestMap);
                payOrder.setStatusCode(Consts.SC.SUCCESS);
                payOrder.setStatusMsg("银联：" + Constants.CupsStatus.CUPS_MSG_00);
            } else {
                // abnormal situation
                payOrder.setStatusCode(Consts.SC.FAIL);
                payOrder.setStatusMsg("CUPS：" + translateCupsRespCode(cupsRespCode));
            }
            payOrder.setBankDealId(requestMap.get(Constants.CUPSQID));
            LOG.info("[unionPayNotify] notify success.payOrder:{}", payOrder);
            return payOrder;
        } else {
            throw new PayException(Consts.SC.DATA_ERROR, "assember payorder error");
        }
    }

    /**
     * 防止订单时间跨年的问题
     * 
     * @return
     */
    private static String assembleDealTime(String dealTime) {
        if (StringUtils.length(dealTime) > 10) {
            return dealTime;
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if (calendar.get(Calendar.MONTH) == 0 && StringUtils.startsWith(dealTime, "12")) {
            return (year - 1) + dealTime;
        }
        return year + dealTime;
    }

    /**
     * @param payOrder
     * @param guzhiPayNotify
     */
    public static String generateFrontUrl(PayOrder payOrder, String guzhiPayNotifyUrl) {
        Map<String, String> frontUrlParamMap = new LinkedHashMap<String, String>();
        String desKey = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), Constants.DESKEY);
        String target = payOrder.getChOrderId() + desKey;
        String sign = MD5Utils.getMD5(target);
        frontUrlParamMap.put(Constants.KEY_MERCHANTORDERID, payOrder.getChOrderId());
        frontUrlParamMap.put("sign", sign);
        String frontUrlParamStr = UrlHelper.assembleQueryStr(frontUrlParamMap);
        String frontUrl = UrlHelper.removeLastSep(guzhiPayNotifyUrl) + Constants.RETURNURL;
        frontUrl = UrlHelper.addQuestionMark(frontUrl) + frontUrlParamStr;
        LOG.info("[generateFrontUrl] success.frontUrl:{},payOrder:{}", frontUrl, payOrder);
        return frontUrl;
    }

    /**
     * 校验returnUrl（在unionpay渠道中称为frontUrl）。
     * 
     * @param resource
     * @param chOrderId
     * @param sign
     * @return
     */
    public static boolean verifyFrontUrl(PayOrder payOrder, String sign) {
        if (null == payOrder) {
            return false;
        }
        String desKey = JsonHelper.fromJson(payOrder.getAppChInfo().getAdditionalInfo(), Constants.DESKEY);
        String target = payOrder.getChOrderId() + desKey;
        String exceptedSign = MD5Utils.getMD5(target);
        if (exceptedSign.equalsIgnoreCase(sign)) {
            return true;
        }
        return false;
    }

    /**
     * 转化指定的字符串为map形式。
     * 
     * @param src
     * @return
     */
    public static Map<String, String> stringToMap(String src, String separator) {
        Map<String, String> map = new LinkedHashMap<String, String>();
        String newSeparator = separator;
        if (StringUtils.isBlank(separator)) {
            newSeparator = AMP;
        }
        String[] kvs = StringUtils.split(src, newSeparator);
        for (int i = 0; i < kvs.length; i++) {
            String kv[] = kvs[i].split(EQ);
            if (kv.length == 2) {
                map.put(kv[0], StringHelper.decode(kv[1], Constants.CHARSET_UTF8));
            } else if (kv.length == 1) {
                if (StringUtils.isNotBlank(kv[0]))
                    map.put(kv[0], "");
            }
        }
        return map;
    }

    /**
     * 生成APP支付时的请求参数体。
     * 
     * @param req 请求要素
     * @param resp 应答要素
     * @return 请求字符串
     */
    public static String buildRequestBodyForApp(Map<String, String> req, String password) {
        Map<String, String> signatureMap = new HashMap<String, String>(req);
        String signature = signForApp(signatureMap, password);
        // 签名结果与签名方式加入请求提交参数组中
        signatureMap.put(Constants.KEY_SIGNMETHOD, Constants.MD5);
        signatureMap.put(Constants.KEY_SIGNATURE, signature);
        return StringHelper.assembleResqStr(signatureMap, Constants.CHARSET_UTF8, false, false);
        // return UpmpCore.createLinkString(filteredReq, false, true);
    }

    /**
     * 为APP的参数签名
     * 
     * @param req 需要签名的要素
     * @return 签名结果字符串
     */
    private static String signForApp(Map<String, String> req, String password) {
        String prestr = StringHelper.assembleResqStr(req, null, true, false);
        prestr = prestr + "&" + MD5Utils.getMD5(password);
        return MD5Utils.getMD5(prestr);
    }

    /**
     * 校验APP参数集合
     * 
     * @param para 异步通知消息
     * @return 验证结果
     */
    private static void verifySignatureForApp(Map<String, String> para, String password) {
        String respSignature = para.get(Constants.KEY_SIGNATURE);
        String signType = para.get(Constants.KEY_SIGNMETHOD);
        if (StringUtils.isBlank(respSignature) || StringUtils.isBlank(signType)) {
            LOG.error("[verifySignatureForApp] get a empty sinature or signMethod,para:{}", para);
            throw new PayException(Consts.SC.CHANNEL_INFO_ERROR, "get a empty sign from unionpay.");
        }
        Map<String, String> signMap = new HashMap<String, String>(para);
        signMap.remove(Constants.KEY_SIGNATURE);
        signMap.remove(Constants.KEY_SIGNMETHOD);
        String signature = signForApp(signMap, password);
        if (!respSignature.equals(signature)) {
            LOG.info("[verifySignatureForApp] fail,excepted sign:{},response param:{}", signature, para);
            throw new PayException(Consts.SC.CHANNEL_INFO_ERROR, "get unexcepted sign from unionpay");
        }
    }

    /**
     * 组装同步响应参数集合
     * 
     * @param respString 应答报文
     * @param resp 应答要素
     * @return 应答是否成功
     */
    public static Map<String, String> assembleResponseMapForApp(String respString, String password) {
        if (StringUtils.isBlank(respString)) {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "get an empty response string.");
        }
        Map<String, String> map = UnionpayWapHelper.stringToMap(respString, "");
        verifySignatureForApp(map, password);
        return map;
    }

    /**
     * 根据app通知或查询响应组装订单。
     * 
     * @param resource
     * @param params
     * @return
     */
    public static PayOrder assembleAppPayOrder(PayOrder payOrder, Map<String, String> params) {
        String settleAmount = params.get(Constants.KEY_SETTLEAMOUNT);
        String orderTime = params.get(Constants.KEY_ORDERTIME);
        String qn = params.get(Constants.KEY_QN);
        String transType = params.get(Constants.KEY_TRANSTYPE);
        String respCode = params.get(Constants.KEY_RESPCODE);
        verifySignatureForApp(params, payOrder.getAppChInfo().getChPayKeyMd5());

        if (Consts.SC.SUCCESS.equals(payOrder.getStatusCode())) {
            return payOrder;
        }
        if (!Constants.TRANSTYPE.equals(transType)) {
            String errorMsg = "get unexpected transtype,expect transtype:" + Constants.KEY_TRANSTYPE
                    + ",request transtype:" + transType;
            LOG.info(errorMsg + ",params:{}", params);
            throw new PayException(Consts.SC.CHANNEL_ERROR, errorMsg);
        }
        String expectedAmount = StringHelper.getAmount(payOrder.getAmount());
        if (!expectedAmount.equals(settleAmount)) {
            String errorMsg = "get unexpected amount,expect amount:" + expectedAmount + ",request amount:"
                    + settleAmount;
            LOG.info(errorMsg + ",params:{}", params);
            throw new PayException(Consts.SC.CHANNEL_ERROR, errorMsg);
        }
        if (Constants.APP_NOTIFY_SUCCESS_TRANSSTATUS.equals(params.get(Constants.KEY_TRANSSTATUS))) {
            LOG.info("[assembleAppPayOrder] set success status,chorderid:{}", payOrder.getChOrderId());
            payOrder.setStatusCode(Consts.SC.SUCCESS);
        } else {
            LOG.info("[assembleAppPayOrder] set fail status,chorderid:{}", payOrder.getChOrderId());
            payOrder.setStatusCode(Consts.SC.FAIL);
        }
        payOrder.setStatusMsg("银联：" + translateAppCupsStatusCode(respCode));
        payOrder.setChDealId(qn);
        payOrder.setChDealTime(orderTime);
        return payOrder;
    }

    /**
     * According to a document,it will translate the CUPS code to a friendly
     * description.
     * 
     * @param cupsRespCode
     * @return
     */
    private static String translateCupsRespCode(String cupsRespCode) {
        if (Constants.CupsStatus.CUPS_CODE_00.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_00;
        } else if (Constants.CupsStatus.CUPS_CODE_01.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_01;
        } else if (Constants.CupsStatus.CUPS_CODE_02.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_02;
        } else if (Constants.CupsStatus.CUPS_CODE_03.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_03;
        } else if (Constants.CupsStatus.CUPS_CODE_06.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_06;
        } else if (Constants.CupsStatus.CUPS_CODE_11.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_11;
        } else if (Constants.CupsStatus.CUPS_CODE_14.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_14;
        } else if (Constants.CupsStatus.CUPS_CODE_15.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_15;
        } else if (Constants.CupsStatus.CUPS_CODE_20.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_20;
        } else if (Constants.CupsStatus.CUPS_CODE_21.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_21;
        } else if (Constants.CupsStatus.CUPS_CODE_25.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_25;
        } else if (Constants.CupsStatus.CUPS_CODE_30.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_30;
        } else if (Constants.CupsStatus.CUPS_CODE_36.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_36;
        } else if (Constants.CupsStatus.CUPS_CODE_39.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_39;
        } else if (Constants.CupsStatus.CUPS_CODE_40.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_40;
        } else if (Constants.CupsStatus.CUPS_CODE_41.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_41;
        } else if (Constants.CupsStatus.CUPS_CODE_42.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_42;
        } else if (Constants.CupsStatus.CUPS_CODE_56.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_56;
        } else if (Constants.CupsStatus.CUPS_CODE_71.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_71;
        } else if (Constants.CupsStatus.CUPS_CODE_80.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_80;
        } else if (Constants.CupsStatus.CUPS_CODE_81.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_81;
        } else if (Constants.CupsStatus.CUPS_CODE_82.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_82;
        } else if (Constants.CupsStatus.CUPS_CODE_83.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_83;
        } else if (Constants.CupsStatus.CUPS_CODE_84.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_84;
        } else if (Constants.CupsStatus.CUPS_CODE_94.equalsIgnoreCase(cupsRespCode)) {
            return Constants.CupsStatus.CUPS_MSG_94;
        } else {
            return "不能识别的CUPSCODE" + cupsRespCode;
        }
    }

    /**
     * 翻译CUPS处理App请求响应的状态码。
     * 
     * @param code
     * @return
     */
    public static String translateAppCupsStatusCode(String code) {
        if (Constants.AppCupsStatus.CUPS_CODE_00.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_00;
        } else if (Constants.AppCupsStatus.CUPS_CODE_01.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_01;
        } else if (Constants.AppCupsStatus.CUPS_CODE_02.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_02;
        } else if (Constants.AppCupsStatus.CUPS_CODE_03.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_03;
        } else if (Constants.AppCupsStatus.CUPS_CODE_04.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_04;
        } else if (Constants.AppCupsStatus.CUPS_CODE_11.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_11;
        } else if (Constants.AppCupsStatus.CUPS_CODE_21.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_21;
        } else if (Constants.AppCupsStatus.CUPS_CODE_22.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_22;
        } else if (Constants.AppCupsStatus.CUPS_CODE_23.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_23;
        } else if (Constants.AppCupsStatus.CUPS_CODE_24.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_24;
        } else if (Constants.AppCupsStatus.CUPS_CODE_25.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_25;
        } else if (Constants.AppCupsStatus.CUPS_CODE_26.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_26;
        } else if (Constants.AppCupsStatus.CUPS_CODE_27.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_27;
        } else if (Constants.AppCupsStatus.CUPS_CODE_28.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_28;
        } else if (Constants.AppCupsStatus.CUPS_CODE_31.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_31;
        } else if (Constants.AppCupsStatus.CUPS_CODE_32.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_32;
        } else if (Constants.AppCupsStatus.CUPS_CODE_33.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_33;
        } else if (Constants.AppCupsStatus.CUPS_CODE_41.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_41;
        } else if (Constants.AppCupsStatus.CUPS_CODE_42.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_42;
        } else if (Constants.AppCupsStatus.CUPS_CODE_51.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_51;
        } else if (Constants.AppCupsStatus.CUPS_CODE_52.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_52;
        } else if (Constants.AppCupsStatus.CUPS_CODE_53.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_53;
        } else if (Constants.AppCupsStatus.CUPS_CODE_61.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_61;
        } else if (Constants.AppCupsStatus.CUPS_CODE_91.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_91;
        } else if (Constants.AppCupsStatus.CUPS_CODE_92.equals(code)) {
            return Constants.AppCupsStatus.CUPS_MSG_92;
        }
        return "不能识别的CUPSCODE," + code;
    }
}