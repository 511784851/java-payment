package com.blemobi.payment.model;

public class Transaction {
	private String orderno;

	private int orderamount;

	private int orderstatus;

	private long ordertime;

	private String custorderno;

	private String receiveuid;

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public int getOrderamount() {
		return orderamount;
	}

	public void setOrderamount(int orderamount) {
		this.orderamount = orderamount;
	}

	public int getOrderstatus() {
		return orderstatus;
	}

	public void setOrderstatus(int orderstatus) {
		this.orderstatus = orderstatus;
	}

	public long getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(long ordertime) {
		this.ordertime = ordertime;
	}

	public String getCustorderno() {
		return custorderno;
	}

	public void setCustorderno(String custorderno) {
		this.custorderno = custorderno;
	}

	public String getReceiveuid() {
		return receiveuid;
	}

	public void setReceiveuid(String receiveuid) {
		this.receiveuid = receiveuid;
	}

}