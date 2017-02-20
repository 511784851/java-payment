/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.dao.impl
 *
 *    Filename:    LotteryDaoImpl.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年2月18日 下午12:10:12
 *
 *    Revision:
 *
 *    2017年2月18日 下午12:10:12
 *
 *****************************************************************/
package com.blemobi.payment.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.LotteryDao;


/**
 * @ClassName LotteryDaoImpl
 * @Description 抽奖数据访问层
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:10:12
 * @version 1.0.0
 */
@Repository("lotteryDao")
public class LotteryDaoImpl extends JdbcTemplate implements LotteryDao {

}
