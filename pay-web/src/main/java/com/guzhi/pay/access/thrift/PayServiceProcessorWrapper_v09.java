/**
 * 
 */
package com.guzhi.pay.access.thrift;

import java.net.InetAddress;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.helper.ThreadHelper;
import com.gb.guzhiPay.access.thrift.generated.TguzhiPayService;
import com.gb.guzhiPay.access.thrift.generated.TguzhiPayService.Iface;

/**
 * Processor的额外处理逻辑，获取业务方的IP地址（保存在ThreadLocal中），以作白名单的控制。
 * 
 * @author administrator
 */
// public class PayServiceProcessorWrapper_v09 extends TguzhiPayService.Processor<Iface> implements TProcessor { // for 0.9 to run
public class PayServiceProcessorWrapper_v09 extends TguzhiPayService.Processor implements TProcessor {
    private static Logger log = LoggerFactory.getLogger(PayServiceProcessorWrapper_v09.class);

    public PayServiceProcessorWrapper_v09(Iface iface) {
        super(iface);
    }

    public boolean process(TProtocol iprot, TProtocol oprot) throws TException {
        InetAddress address = ((TSocket) iprot.getTransport()).getSocket().getInetAddress();
        String ip = address.getHostAddress();
        log.debug("get app ip address: {}", ip);

        ThreadHelper.setAppIp(ip);
        try {
            return super.process(iprot, oprot);
        } finally {
            ThreadHelper.cleanupIpRecords();
        }
    }
}
