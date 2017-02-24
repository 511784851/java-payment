package com.blemobi.payment.model;

public class RedReceive {

	private int id;

	private String ord_no;

	private long rece_uuid;

	private int money;

	private long rece_tm;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrd_no() {
		return ord_no;
	}

	public void setOrd_no(String ord_no) {
		this.ord_no = ord_no;
	}

	public long getRece_uuid() {
		return rece_uuid;
	}

	public void setRece_uuid(long rece_uuid) {
		this.rece_uuid = rece_uuid;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public long getRece_tm() {
		return rece_tm;
	}

	public void setRece_tm(long rece_tm) {
		this.rece_tm = rece_tm;
	}

}