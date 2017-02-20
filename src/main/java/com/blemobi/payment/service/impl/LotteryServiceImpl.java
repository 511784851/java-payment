/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.service.impl
 *
 *    Filename:    LotteryServiceImpl.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月18日 下午12:07:19
 *
 *    Revision:
 *
 *    2017年2月18日 下午12:07:19
 *
 *****************************************************************/
package com.blemobi.payment.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blemobi.payment.dao.LotteryDao;
import com.blemobi.payment.service.LotteryService;


/**
 * @ClassName LotteryServiceImpl
 * @Description 抽奖业务类
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:07:19
 * @version 1.0.0
 */
@Service("lotteryService")
public class LotteryServiceImpl implements LotteryService {
    @Autowired
    private LotteryDao lotteryDao;
    
}
