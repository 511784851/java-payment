/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.thrift.udb;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.duowan.pooling.thrift.ThriftClientWrapper;
import com.guzhi.pay.common.Consts;
import com.guzhi.pay.exception.PayException;
import com.guzhi.pay.helper.ThrifeUtils;
import com.guzhi.pay.thrift.ThriftConfig;
import com.guzhi.pay.thrift.udb.gen.userinfo_service.Iface;

/**
 * UDB的用户查询服务
 * 
 * @author 
 *         reference gate.udbuserinfoservice,original author administrator.
 */
@Service
public class UdbUserInfoService {
    private static final Logger LOG = LoggerFactory.getLogger(UdbUserInfoService.class);

    @Autowired(required = false)
    @Qualifier("udbUserInfoConfig")
    private ThriftConfig thriftConfig;

    public String getPassportByUid(String uid) {
        try {
            return getPassportByUidHelper(uid);
        } catch (Exception e) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e1) {
            }
            return getPassportByUidHelper(uid);
        }
    }

    @SuppressWarnings("unchecked")
    private String getPassportByUidHelper(String uid) {
        ThriftClientWrapper<Iface> client = null;
        try {
            client = thriftConfig.getFactory().createClient();
            String result = client.getClient().lg_userinfo_transPassportByUid(uid);
            LOG.info("[getPassportByUid] uid:{}, result:{}", uid, result);
            return result;
        } catch (Exception e) {
            LOG.error("[getPassportByUid] error.", e);
            e.printStackTrace();
            throw new PayException(Consts.SC.CONN_ERROR, "transfer uid to passport fail");
        } finally {
            ThrifeUtils.close(client);
        }
    }
}