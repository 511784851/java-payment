/*
 *  
 * All Rights Reserved.
 * This program is the confidential and proprietary information of 
 * duowan. ("Confidential Information").  You shall not disclose such
 * Confidential Information and shall use it only in accordance with
 * the terms of the license agreement you entered into with guzhi.com.
 */
package com.guzhi.pay.channel.apple;

/**
 * @author
 * 
 */
public interface AppleBalanceConsts {

    // 正式验证地址
    static final String PRODUCVERIFYRECEIPTURl = "https://buy.itunes.apple.com/verifyReceipt?";

    // 测试验证地址
    static final String TESTVERIFYRECEIPTURl = "https://sandbox.itunes.apple.com/verifyReceipt?";

    static final String RECEIPTDATA = "receipt-data";

    static final String STATUS = "status";

    static final String SUCCESS = "0";

    static final String QUANTITY = "quantity";

    static final String PRODUCT_ID = "product_id";

    static final String RECEIPT = "receipt";

    static final String UNIT_PRICE = "unitPrice";
}
