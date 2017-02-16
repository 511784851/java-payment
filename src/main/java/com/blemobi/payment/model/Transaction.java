package com.blemobi.payment.model;

import java.util.Date;

public class Transaction {
    private String orderno;

    private Integer orderamount;

    private Integer orderstatus;

    private Date ordertime;

    private String custorderno;

    private String receiveuid;

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno == null ? null : orderno.trim();
    }

    public Integer getOrderamount() {
        return orderamount;
    }

    public void setOrderamount(Integer orderamount) {
        this.orderamount = orderamount;
    }

    public Integer getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(Integer orderstatus) {
        this.orderstatus = orderstatus;
    }

    public Date getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(Date ordertime) {
        this.ordertime = ordertime;
    }

    public String getCustorderno() {
        return custorderno;
    }

    public void setCustorderno(String custorderno) {
        this.custorderno = custorderno == null ? null : custorderno.trim();
    }

    public String getReceiveuid() {
        return receiveuid;
    }

    public void setReceiveuid(String receiveuid) {
        this.receiveuid = receiveuid == null ? null : receiveuid.trim();
    }
}