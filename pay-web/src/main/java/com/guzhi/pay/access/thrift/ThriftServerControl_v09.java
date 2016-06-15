/**
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.access.thrift;

import java.net.InetSocketAddress;

import javax.annotation.Resource;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gb.guzhiPay.access.thrift.generated.TguzhiPayService;
import com.gb.guzhiPay.access.thrift.generated.TguzhiPayService.Iface;

/**
 * 控制Thrift服务器的启停
 * 
 * @author administrator
 */
public class ThriftServerControl_v09 {
    private static Logger log = LoggerFactory.getLogger(ThriftServerControl_v09.class);
    private String bindHost;
    private int bindPort;
    private TServer server;

    @Resource
    private PayServiceHandler payServiceHandler;

    public void start() {
//        log.info("Starting ThriftServer Server with host={}, port={}", bindHost, bindPort);
//
//        try {
//            TServerSocket transport = new TServerSocket(new InetSocketAddress(bindHost, bindPort));
//            TguzhiPayService.Processor<Iface> processor = new PayServiceProcessorWrapper_v09((Iface) payServiceHandler);
//            server = new TThreadPoolServer(new TThreadPoolServer.Args(transport).processor(processor));
//            
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    server.serve(); // will block here
//                }
//            }).start();
//            log.error("ThriftServer started, bindHost={}, bindPort={}", bindHost, bindPort);
//        } catch (Exception e) {
//            log.error("Exception when starting ThriftServer, bindHost={}, bindPort={}", bindHost, bindPort, e);
//        }
    }

    public void stop() {
        log.info("Stopping ThriftServer.");
        this.server.stop();
        log.info("ThriftServer stopped");
    }

    public void setBindHost(String bindHost) {
        this.bindHost = bindHost;
    }

    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }

    public PayServiceHandler getPayServiceHandler() {
        return payServiceHandler;
    }

    public void setPayServiceHandler(PayServiceHandler payServiceHandler) {
        this.payServiceHandler = payServiceHandler;
    }
}
