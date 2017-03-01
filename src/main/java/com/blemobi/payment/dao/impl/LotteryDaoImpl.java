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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.LotteryDao;
import com.blemobi.payment.util.DateTimeUtils;

import lombok.extern.log4j.Log4j;

/**
 * @ClassName LotteryDaoImpl
 * @Description 抽奖数据访问层
 * @author HUNTER.POON
 * @Date 2017年2月18日 下午12:10:12
 * @version 1.0.0
 */
@Log4j
@Repository("lotteryDao")
public class LotteryDaoImpl extends JdbcTemplate implements LotteryDao {


    @Override
    public int createLottery(Object[] param) {
        String sql = "INSERT INTO t_lotteries(id, title, typ, winners, tot_amt, remain_amt, remain_cnt, status, uuid, crt_tm, upd_tm, obj_key) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return this.update(sql, param);
    }
    @Override
    public int createLotteryLoc(List<Object[]> param) {
        String sql = "INSERT INTO t_lottery_locations(lottery_id, loc_cd, loc_nm) VALUES(?, ?, ?)";
        return this.batchUpdate(sql, param).length;
    }

    @Override
    public int createWinners(List<Object[]> param) {
        String sql = "INSERT INTO t_winners(uuid, lottery_id, nick_nm, sex, bonus, status, crt_tm, accept_tm) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        return this.batchUpdate(sql, param).length;
    }

    @Override
    public List<Map<String, Object>> lotteryList(String uuid, String keywords, int startIdx, int size) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<Object>();
        sql.append("SELECT id, title, typ, winners, crt_tm, obj_key FROM t_lotteries WHERE uuid = ? AND crt_tm >= ? ");
        param.add(uuid);
        param.add(DateTimeUtils.calcTime(TimeUnit.DAYS, -30));
        if (!StringUtils.isEmpty(keywords)) {
            sql.append(" AND title LIKE ?");
            param.add("%" + keywords + "%");
        }
        sql.append(" ORDER BY crt_tm DESC LIMIT ?, ?");
        param.add(startIdx);
        param.add(size);
        log.debug(sql.toString() + "----" + StringUtils.join(param, ","));
        List<Map<String, Object>> result = this.queryForList(sql.toString(), param.toArray(new Object[] {}));
        return result;
    }

    @Override
    public Map<String, Object> lotteryDetail(String lotteryId) {
        String sql = "SELECT title, typ, crt_tm, tot_amt,winners, uuid from t_lotteries where id = ?";
        return this.queryForMap(sql, lotteryId);
    }

    @Override
    public List<Map<String, Object>> lotteryLocations(String lotteryId) {
        String sql = "SELECT loc_cd, loc_nm from t_lottery_locations where lottery_id = ? ORDER BY id ASC";
        return this.queryForList(sql, new Object[] {lotteryId });
    }

    @Override
    public List<Map<String, Object>> lotteryUsers(String lotteryId, String keywords, int type) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<Object>();
        sql.append("SELECT nick_nm, sex, bonus, uuid FROM t_winners WHERE lottery_id = ? ");
        param.add(lotteryId);
        if (type == 1 || type == 2) {
            param.add(type);
            sql.append(" AND sex = ? ");
        }
        if (!StringUtils.isEmpty(keywords)) {
            param.add( "%" + keywords + "%");
            sql.append(" AND nick_nm LIKE ?");
        }
        sql.append(" ORDER BY id ASC");
        log.info(sql.toString());
        return this.queryForList(sql.toString(), param.toArray(new Object[] {}));
    }

    @Override
    public List<String> top5UUID(String lotteryId) {
        String sql = "SELECT uuid FROM t_winners WHERE lottery_id = ? ORDER BY ID ASC LIMIT 0, 5";
        return this.queryForList(sql, String.class, lotteryId);
    }
    

    @Resource
    public void setDs(DataSource ds) {
        super.setDataSource(ds);
    }
    @Override
    public int paySucc(String ordNo, int amt) {
        String sql = "UPDATE t_lotteries SET status = 2 WHERE id = ? AND status = 1 AND tot_amt = ?";
        Object[] param = new Object[]{ordNo, amt};
        return this.update(sql, param);
    }
    @Override
    public int acceptPrize(String lotteryId, String uuid) {
        String sql = "UPDATE t_winners SET status = 1, accept_tm = ? WHERE lottery_id = ? AND uuid = ? AND status = 0";
        return this.update(sql, new Object[]{DateTimeUtils.currTime(), lotteryId, uuid});
    }
    @Override
    public int updateLottery(String lotteryId, int remainCnt, int remainAmt, long updTm, int status) {
        String sql = "UPDATE t_lotteries SET remain_cnt = ?, remain_amt = ?, status = ?, upd_tm = ? WHERE id = ?";
        return this.update(sql, new Object[]{remainCnt, remainAmt, status, updTm, lotteryId});
    }
    @Override
    public Map<String, Object> queryLotteryInf(String lotteryId) {
        String sql = "SELECT crt_tm, tot_amt, remain_cnt, remain_amt, status FROM t_lotteries WHERE id = ? ";
        Map<String, Object> inf = this.queryForMap(sql, lotteryId);
        return inf;
    }
    @Override
    public Map<String, Object> getPrizeInf(String lotteryId, String uuid) {
        String sql = "SELECT status, bonus FROM t_winners WHERE lottery_id = ? AND uuid = ?";
        return this.queryForMap(sql, new Object[]{lotteryId, uuid});
    }
}
