package com.guzhi.pay.thrift.gbpay;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.thrift.AbstractClientValidator;
import com.guzhi.pay.thrift.gbpay.PaymentService.Client;

/**
 * 
 * 连接池用户验证连接的有效性。默认3秒验证一次。
 * 
 * @author yangpeng
 */
public class gbPayClientValidator extends AbstractClientValidator {

    protected static Logger log = LoggerFactory.getLogger(gbPayClientValidator.class);

    @Override
    public boolean isValid(TTransport object) {
        Client client = new Client(new TBinaryProtocol(object));
        try {
            client.ping();
            return true;
        } catch (TException e) {
            log.error("failed to ping " + getPoolName());
            try {
                getPoolManager().invalidateObject(object);
            } catch (Exception e1) {
                log.error("failed to invalidateObject for pool " + getPoolName());
            }
            return false; // 如果抛出异常则表明连接失败
        }
    }
}