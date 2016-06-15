/**
 * 
 */
package com.guzhi.pay.access.thrift;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.guzhi.pay.business.PayService;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.domain.AppInfo;
import com.guzhi.pay.domain.DomainResource;
import com.guzhi.pay.domain.PayOrder;
import com.guzhi.pay.domain.PayOrder.PayReqVal;
import com.guzhi.pay.domain.PayOrder.PayReqView;
import com.guzhi.pay.domain.PayOrder.QueryReqVal;
import com.guzhi.pay.domain.PayOrder.QueryReqView;
import com.guzhi.pay.domain.PayOrder.RefundReqVal;
import com.guzhi.pay.domain.PayOrder.RefundReqView;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.JsonHelper;
import com.guzhi.pay.helper.SecureHelper;
import com.guzhi.pay.helper.StringHelper;
import com.guzhi.pay.helper.ThreadHelper;
import com.guzhi.pay.helper.TraceHelper;
import com.guzhi.pay.util.PayOrderUtil;
import com.gb.guzhiPay.access.thrift.generated.TReq;
import com.gb.guzhiPay.access.thrift.generated.TResp;
import com.gb.guzhiPay.access.thrift.generated.TguzhiPayService.Iface;

/**
 * 支付服务Wraper,从Thrift Access 层转为Service层调用。
 * 把Thrift类转为Java Domain类，或把Java Domain类转为Thrift类。
 * 
 * @author administrator
 */
@Service
@SuppressWarnings("unchecked")
public class PayServiceHandler implements Iface {
    private static Logger log = LoggerFactory.getLogger(PayServiceHandler.class);

    @Resource
    private PayService payService;
    @Resource
    private DomainResource resource;

    @Override
    public void ping() throws TException {
        log.info("ping IF invoked, from ip: {}", ThreadHelper.getAppIp());
    }

    @Override
    public String status() throws TException {
        log.info("status IF invoke start, from ip: {}", ThreadHelper.getAppIp()); // TODO:
                                                                                  // Audit
                                                                                  // Log

        String respStr = "version=1.0";

        log.info("status IF invoke end, respStr: {}", respStr); // TODO: Audit
                                                                // Log
        return respStr;
    }

    @Override
    public TResp pay(TReq tReq) throws TException {
        log.info("pay IF invoke start, from ip: {}, tReq: {}", ThreadHelper.getAppIp(), tReq); // TODO:
                                                                                               // Audit
                                                                                               // Log

        PayOrder payOrder = null;
        try {
            Map<String, String> paramsData = JsonHelper.fromJson(
                    StringHelper.decode(tReq.getData(), Consts.CHARSET_UTF8), Map.class);
            log.info("[pay] get pay request,parameterMap:{}", paramsData,
                    TraceHelper.getTrace(tReq.getAppId(), paramsData));

            payOrder = PayOrderUtil.assemblePayOrder(resource, tReq.getData(), tReq.getSign(), tReq.getAppId(),
                    PayReqView.class, PayReqVal.class, true);
            payOrder = payService.pay(payOrder);
            // 支付后参数
            log.info("[pay] process payorder finished,about to response,payorder:{}", payOrder,
                    TraceHelper.getTrace(payOrder));
            TResp tResp = assembleNormalResp(payOrder);
            log.info("pay IF invoke end, tResp: {}", tResp); // TODO: Audit Log
            return tResp;
        } catch (Throwable t) {
            log.error("pay IF invoke failed! tReq: {}", tReq, t); // TODO: Audit
                                                                  // Log
            TResp tResp = assembleErrorResp(tReq, t, payOrder);
            return tResp;
        }
    }

    @Override
    public TResp query(TReq tReq) throws TException {
        log.info("query IF invoke start, from ip: {}, tReq: {}", ThreadHelper.getAppIp(), tReq); // TODO:
                                                                                                 // Audit

        PayOrder payOrder = null;
        try {
            Map<String, String> paramsData = JsonHelper.fromJson(
                    StringHelper.decode(tReq.getData(), Consts.CHARSET_UTF8), Map.class);
            log.info("[query] query request,parameterMap:{}", paramsData,
                    TraceHelper.getTrace(tReq.getAppId(), paramsData));
            payOrder = PayOrderUtil.assemblePayOrder(resource, tReq.getData(), tReq.getSign(), tReq.getAppId(),
                    QueryReqView.class, QueryReqVal.class, false);
            payOrder = payService.query(payOrder);
            // 查询后参数
            log.info("[query] process payorder finished,about to response,payorder:{}", payOrder,
                    TraceHelper.getTrace(payOrder));
            TResp tResp = assembleNormalResp(payOrder);
            log.info("query IF invoke end, tResp: {}", tResp); // TODO: Audit
                                                               // Log
            return tResp;
        } catch (Throwable t) {
            log.error("query IF invoke failed! tReq: {}", tReq, t); // TODO:
                                                                    // Audit
                                                                    // Logger
            TResp tResp = assembleErrorResp(tReq, t, payOrder);
            return tResp;
        }
    }

    @Override
    public TResp refund(TReq tReq) throws TException {
        log.info("refund IF invoke start, from ip: {}, tReq: {}", ThreadHelper.getAppIp(), tReq); // TODO:
                                                                                                  // Audit

        PayOrder payOrder = null;
        try {
            Map<String, String> paramsData = JsonHelper.fromJson(
                    StringHelper.decode(tReq.getData(), Consts.CHARSET_UTF8), Map.class);
            log.info("[refund] refund request,parameterMap:{}", paramsData,
                    TraceHelper.getTrace(tReq.getAppId(), paramsData));
            payOrder = PayOrderUtil.assemblePayOrder(resource, tReq.getData(), tReq.getSign(), tReq.getAppId(),
                    RefundReqView.class, RefundReqVal.class, false);
            payOrder = payService.refund(payOrder);
            // 退款后参数
            log.info("[refund] process payorder finished,about to response,payorder:{}", payOrder,
                    TraceHelper.getTrace(payOrder));
            TResp tResp = assembleNormalResp(payOrder);
            log.info("refund IF invoke end, tResp: {}", tResp); // TODO: Audit
                                                                // Log
            return tResp;
        } catch (Throwable t) {
            log.error("refund IF invoke failed! tReq: {}", tReq, t); // TODO:
                                                                     // Audit
                                                                     // Logger
            TResp tResp = assembleErrorResp(tReq, t, payOrder);
            return tResp;
        }
    }

    @Override
    public TResp accounts(TReq tReq) throws TException {
        log.info("accounts IF invoke start, from ip: {}, tReq: {}", ThreadHelper.getAppIp(), tReq); // TODO:
                                                                                                    // Audit

        try {
            throw new PayException(Consts.SC.INTERNAL_ERROR, "this interface is not implemented yet!", null, null);
            // Accounts accounts = convertToAccounts(tReq);
            // verifyAccountsParameters(accounts);
            // Accounts as = payService.accounts(accounts);
            // TResp tResp = convert2TResp(as);
            // log.info("accounts IF invoke end, tResp: {}", tResp); // TODO:
            // Audit Log
            // return tResp;
        } catch (Throwable t) {
            log.error("accounts IF invoke failed! tReq: {}", tReq, t); // TODO:
                                                                       // Audit
                                                                       // Log
            TResp tResp = assembleErrorResp(tReq, t, null); // TODO: the
                                                            // payOrder here is
                                                            // null
            return tResp;
        }
    }

    private TResp assembleErrorResp(TReq tReq, Throwable t, PayOrder payOrderInParam) {
        String appId = tReq.getAppId();
        PayOrder payOrderForResp = null;
        PayException payException = null;

        // 尽量得到原始的PayOrder（注意抛出异常时，可能连基本的PayOrder都没有组装出来）
        if (t instanceof PayException) { // 优先使用异常抛出时所以包含的PayOrder
            payException = (PayException) t;
            payOrderForResp = payException.getPayOrder();
        }

        // 其次使用方法参数中的PayOrder 或 新建一个
        if (payOrderForResp == null) {
            payOrderForResp = (payOrderInParam == null) ? new PayOrder() : payOrderInParam;
        }

        // 组装信息到PayOrder（注意这里设置的信息不会进入数据库！）
        payOrderForResp.setAppId(tReq.getAppId());
        payOrderForResp.setStatusCode(Consts.SC.INTERNAL_ERROR);
        Throwable cause = t;
        String statusMsg = "Get exception: " + (cause == null ? "" : cause.getMessage());
        while (cause != null && cause.getCause() != null) {
            cause = cause.getCause();
            statusMsg = statusMsg + " > " + cause.getMessage();
        }
        payOrderForResp.setStatusMsg(statusMsg);
        if (payException != null) {
            payOrderForResp.setStatusCode(payException.getStatusCode());
            payOrderForResp.setStatusMsg(payException.getStatusMsg());
        }

        // 组装Response
        String data = JsonHelper.payOrderToRespJson(payOrderForResp);
        String sign = "AppInfoNull";
        if (payOrderForResp.getAppInfo() != null) {
            // 注意这里不应再调用resource查找appId，否则可能直接抛出异常给Thrift
            sign = SecureHelper.genMd5Sign(payOrderForResp.getAppInfo().getKey(), data);
        }
        TResp tResp = new TResp();
        tResp.setData(data);
        tResp.setSign(sign);
        tResp.setAppId(appId);

        return tResp;
    }

    private TResp assembleNormalResp(PayOrder payOrder) {
        AppInfo appInfo = payOrder.getAppInfo();
        String data = JsonHelper.payOrderToRespJson(payOrder);
        String sign = SecureHelper.genMd5Sign(appInfo.getKey(), data);

        TResp tResp = new TResp();
        tResp.setData(data);
        tResp.setSign(sign);
        tResp.setAppId(payOrder.getAppId());

        return tResp;
    }

    public void setPayService(PayService payService) {
        this.payService = payService;
    }

    public void setResource(DomainResource resource) {
        this.resource = resource;
    }
}
