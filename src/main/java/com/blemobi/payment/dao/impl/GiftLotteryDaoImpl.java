/******************************************************************
 *
 *    
 *    Package:     com.blemobi.payment.dao.impl
 *
 *    Filename:    GiftLotteryDaoImpl.java
 *
 *    Description: TODO
 *
 *    @author:     HUNTER.POON
 *
 *    @version:    1.0.0
 *
 *    Create at:   2017年3月22日 下午5:25:28
 *
 *    Revision:
 *
 *    2017年3月22日 下午5:25:28
 *
 *****************************************************************/
package com.blemobi.payment.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.GiftLotteryDao;

/**
 * @ClassName GiftLotteryDaoImpl
 * @Description TODO
 * @author HUNTER.POON
 * @Date 2017年3月22日 下午5:25:28
 * @version 1.0.0
 */

@Repository("giftLotteryDao")
public class GiftLotteryDaoImpl extends JdbcTemplate implements GiftLotteryDao {

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#saveLottery(java.lang.Object[])
     */
    @Override
    public int saveLottery(Object[] param) {
        String sql = "INSERT INTO t_gift_lottery (id, title, gender, remark, area_cnt, overdue_tm, uuid, status, winners, remain_cnt, crt_tm, upd_tm) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return this.update(sql, param);
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#saveLocations(java.util.List)
     */
    @Override
    public int saveLocations(List<Object[]> param) {
        String sql = "INSERT INTO t_gift_location(lottery_id, loc_cd, loc_nm) VALUES(?, ?, ?)";
        return this.batchUpdate(sql, param).length;
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#saveWinners(java.util.List)
     */
    @Override
    public int saveWinners(List<Object[]> param) {
        String sql = "INSERT INTO t_gift_winner(gender, loc_cd, uuid, lottery_id, gift_id) VALUES(?, ?, ?, ?, ?)";
        return this.batchUpdate(sql, param).length;
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#saveGifts(java.util.List)
     */
    @Override
    public int saveGifts(List<Object[]> param) {
        String sql = "INSERT INTO t_gift (id, lottery_id, gift_nm, gift_cnt, remain_cnt, crt_tm, overdue_tm, upd_tm, sort) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return this.batchUpdate(sql, param).length;
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#updateLottery(java.lang.Object[])
     */
    @Override
    public int updateLottery(Object[] param) {
        String sql = "UPDATE t_gift_lottery SET status = ?, remain_cnt = ?, upd_tm = ? WHERE id = ? AND overdue_tm > ?";
        return this.update(sql, param);
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#queryLottery(java.lang.Object[])
     */
    @Override
    public Map<String, Object> queryLottery(String param) {
        String sql = "SELECT title, gender, remark, area_cnt, overdue_tm, status, winners, remain_cnt, crt_tm FROM t_gift_lottery WHERE id = ?";
        return this.queryForMap(sql, param);
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#historyLotteries(java.lang.Object[])
     */
    @Override
    public List<Map<String, Object>> historyLotteries(String uuid, String keywords, Integer startIdx) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<Object>();
        sql.append("SELECT id, title, winners, crt_tm, overdue_tm FROM t_gift_lottery WHERE uuid = ? ");
        param.add(uuid);
        if (!StringUtils.isEmpty(keywords)) {
            param.add("%" + keywords + "%");
            sql.append(" AND title LIKE ? ");
        }
        sql.append(" AND status NOT IN (0)");
        sql.append(" ORDER BY crt_tm DESC LIMIT ?, 10");
        param.add(startIdx);
        List<Map<String, Object>> result = this.queryForList(sql.toString(), param.toArray(new Object[] {}));
        return result;
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#lotteryLocList(java.lang.Object[])
     */
    @Override
    public List<String> lotteryLocList(String lotteryId) {
        String sql = "SELECT loc_cd FROM t_gift_location WHERE lottery_id = ? ORDER BY ID ASC";
        return this.queryForList(sql, String.class, lotteryId);
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#lotteryWinnerList(java.lang.Object[])
     */
    @Override
    public List<Map<String, Object>> lotteryWinnerList(String lotteryId) {
        String sql = "SELECT b_rcv_remark, loc_cd, uuid, gift_id, rcv_nm, rcv_phone, rcv_addr, edit_cnt, status, accept_tm, rcv_email, rcv_remark, b_rcv_nm, b_rcv_phone, b_rcv_addr, b_rcv_email, notify_cnt FROM t_gift_winner WHERE lottery_id = ? ORDER BY ID ASC";
        return this.queryForList(sql, lotteryId);
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#lotteryGiftList(java.lang.Object[])
     */
    @Override
    public List<Map<String, Object>> lotteryGiftList(String lotteryId) {
        String sql = "SELECT id, gift_nm, gift_cnt, remain_cnt, crt_tm, overdue_tm, sort FROM t_gift WHERE lottery_id = ? ORDER BY sort ASC";
        return this.queryForList(sql, lotteryId);
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#queryOverdueLotteries()
     */
    @Override
    public List<Map<String, Object>> queryOverdueLotteries() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (非 Javadoc) Description:
     * @see com.blemobi.payment.dao.GiftLotteryDao#updateOverdueLottery(java.lang.Object[])
     */
    @Override
    public int updateOverdueLottery(Object[] param) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Resource
    public void setDs(DataSource ds) {
        super.setDataSource(ds);
    }

    @Override
    public Map<String, Object> queryGift(Object[] param) {
        String sql = "SELECT remain_cnt, overdue_tm FROM t_gift WHERE id = ? AND lottery_id = ?";
        return this.queryForMap(sql, param);
    }

    @Override
    public Map<String, Object> queryWinner(Object[] param) {
        String sql = "SELECT status, gift_id, edit_cnt, rcv_nm, rcv_phone, rcv_addr, rcv_email, rcv_remark, b_rcv_nm, b_rcv_phone, b_rcv_addr, b_rcv_email, b_rcv_remark FROM t_gift_winner WHERE uuid = ? AND lottery_id = ?";
        return this.queryForMap(sql, param);
    }

    @Override
    public int updateGift(Object[] param) {
        String sql = "UPDATE t_gift SET remain_cnt = ?, upd_tm = ? WHERE id = ? AND lottery_id = ?";
        return this.update(sql, param);
    }

    @Override
    public List<String> lotteryTop5WinnerList(String lotteryId) {
        String sql = "SELECT uuid FROM t_gift_winner WHERE lottery_id = ? ORDER BY id ASC LIMIT 5";
        return this.queryForList(sql, String.class, lotteryId);
    }

    @Override
    public int delete(String uuid, List<String> lotteryId) {
        String lotteryIds = "'" + StringUtils.join(lotteryId, "','") + "'";
        String sql = "UPDATE t_gift_lottery SET status = 0 WHERE id IN (" + lotteryIds + ") AND uuid = ?";
        return this.update(sql, new Object[] {uuid });
    }

    @Override
    public int updateLoc(String rcvNm, String rcvAddr, String rcvPhone, String rcvEmail, String rcvRemark,
            Boolean isSelf, Integer editCnt, Integer status, String lotteryId, String uuid) {
        StringBuilder sql = new StringBuilder();
        List<Object> param = new ArrayList<Object>();
        if (isSelf) {
            sql.append(
                    "UPDATE t_gift_winner SET rcv_nm = ?, rcv_phone = ?, rcv_addr = ?, edit_cnt = ?, status = ?, rcv_email = ?,"
                            + " rcv_remark = ?, b_rcv_nm = ?, b_rcv_phone = ?, b_rcv_addr = ?, b_rcv_email = ?, b_rcv_remark = ? "
                            + "WHERE lottery_id = ? AND uuid = ?");
            param.add(rcvNm);
            param.add(rcvPhone);
            param.add(rcvAddr);
            param.add(editCnt);
            param.add(status);
            param.add(rcvEmail);
            param.add(rcvRemark);
            param.add(rcvNm);
            param.add(rcvPhone);
            param.add(rcvAddr);
            param.add(rcvEmail);
            param.add(rcvRemark);
        } else {
            sql.append(
                    "UPDATE t_gift_winner SET b_rcv_nm = ?, b_rcv_phone = ?, b_rcv_addr = ?, b_rcv_email = ?, b_rcv_remark = ? "
                            + "WHERE lottery_id = ? AND uuid = ?");
            param.add(rcvNm);
            param.add(rcvPhone);
            param.add(rcvAddr);
            param.add(rcvEmail);
            param.add(rcvRemark);
        }
        param.add(lotteryId);
        param.add(uuid);
        return this.update(sql.toString(), param.toArray());
    }
}
