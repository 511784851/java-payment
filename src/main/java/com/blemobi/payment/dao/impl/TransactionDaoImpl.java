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
        String sql = "INSERT INTO t_tra_pay(amount, status, time, ord_no, rec_uid, corg_ord_id, corg_sts, corg_msg) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
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
