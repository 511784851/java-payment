/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.thrift;

import org.apache.commons.pool.ObjectPool;
import org.apache.thrift.transport.TTransport;

import com.duowan.pooling.IObjectPoolManager;
import com.duowan.pooling.thrift.ObjectValidator;

/**
 * @author administrator
 * 
 */
public abstract class AbstractClientValidator implements ObjectValidator<TTransport> {
    private IObjectPoolManager<? extends ObjectPool, TTransport> poolManager;
    private String poolName;

    public IObjectPoolManager<? extends ObjectPool, TTransport> getPoolManager() {
        return poolManager;
    }

    public void setPoolManager(IObjectPoolManager<? extends ObjectPool, TTransport> poolManager) {
        this.poolManager = poolManager;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public abstract boolean isValid(TTransport object);

}
