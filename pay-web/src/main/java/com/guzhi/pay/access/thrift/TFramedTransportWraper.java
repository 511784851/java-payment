/**
 * 
 */
package com.guzhi.pay.access.thrift;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportFactory;

/**
 * 由于TFramedTransport没法获取socket。增加该类获取socket。
 * 
 * @author yangpeng
 */
public class TFramedTransportWraper extends TFramedTransport {
    private TTransport transport_ = null;

    public TFramedTransportWraper(TTransport transport) {
        super(transport);
        transport_ = transport;
    }

    public TFramedTransportWraper(TTransport transport, int maxLength) {
        super(transport, maxLength);
        transport_ = transport;
    }

    public TTransport getTTransport() {
        return transport_;
    }

    public static class Factory extends TTransportFactory {
        private int maxLength_;

        public Factory() {
            maxLength_ = TFramedTransport.DEFAULT_MAX_LENGTH;
        }

        public Factory(int maxLength) {
            maxLength_ = maxLength;
        }

        @Override
        public TTransport getTransport(TTransport base) {
            return new TFramedTransportWraper(base, maxLength_);
        }
    }
}
