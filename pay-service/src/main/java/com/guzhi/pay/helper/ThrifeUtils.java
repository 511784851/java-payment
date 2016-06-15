/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.helper;

import com.duowan.pooling.thrift.ThriftClientWrapper;

/**
 * @author administrator
 * 
 */
public class ThrifeUtils {

    public static void close(ThriftClientWrapper<? extends Object> client) {
        if (client != null) {
            client.close();
        }
    }

}
