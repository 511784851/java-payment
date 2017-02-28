package com.blemobi.payment.dao.impl;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.blemobi.payment.dao.TransactionDao;
import com.blemobi.payment.model.Transaction;

@Repository("transactionDao")
public class TransactionDaoImpl extends JdbcTemplate implements TransactionDao {


    public int insert(Object... args) {
        String sql = "INSERT INTO t_transaction_serial(uuid, biz_ord_no, biz_typ, amt, ptf_sts, ptf_msg, trans_desc, corg_ord_no, corg_sts, corg_msg, crt_tm, upd_tm) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return this.update(sql, args);
    }

    public Transaction selectByPrimaryKey(String custorderno) {
        // TODO Auto-generated method stub
        return null;
    }

    public int updateByPrimaryKey(Transaction transaction) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Resource
    public void setDs(DataSource ds) {
        super.setDataSource(ds);
    }
}
