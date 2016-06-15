/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.thrift.udb;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guzhi.pay.thrift.AbstractClientValidator;
import com.guzhi.pay.thrift.udb.gen.userinfo_service.Client;
import com.guzhi.pay.thrift.gbpay.gbPayClientValidator;

/**
 * udb接口可用性校验器.
 * 
 * @author 
 * 
 */
public class UdbUserInfoClientValidator extends AbstractClientValidator {
    protected static Logger LOG = LoggerFactory.getLogger(gbPayClientValidator.class);

    @Override
    public boolean isValid(TTransport object) {
        Client client = new Client(new TBinaryProtocol(object));
        try {
            client.lg_userinfo_ping(1);
            return true;
        } catch (TException e) {
            LOG.error("[isValid] failed to ping " + getPoolName());
            try {
                getPoolManager().invalidateObject(object);
            } catch (Exception e1) {
                LOG.error("[isValid] failed to invalidateObject for pool " + getPoolName());
            }
            return false; // 如果抛出异常则表明连接失败
        }
    }
}