package com.blemobi.payment.model;

public class Reward {

	private String ord_no;

	private String send_uuid;

	private String rece_uuid;

	private int money;

	private String content;

	private long send_tm;

	private int pay_status;

	public String getOrd_no() {
		return ord_no;
	}

	public void setOrd_no(String ord_no) {
		this.ord_no = ord_no;
	}

	public String getSend_uuid() {
		return send_uuid;
	}

	public void setSend_uuid(String send_uuid) {
		this.send_uuid = send_uuid;
	}

	public String getRece_uuid() {
		return rece_uuid;
	}

	public void setRece_uuid(String rece_uuid) {
		this.rece_uuid = rece_uuid;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getSend_tm() {
		return send_tm;
	}

	public void setSend_tm(long send_tm) {
		this.send_tm = send_tm;
	}

	public int getPay_status() {
		return pay_status;
	}

	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}

}