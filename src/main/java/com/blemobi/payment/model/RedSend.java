package com.blemobi.payment.model;

public class RedSend {

	private String ord_no;

	private String send_uuid;

	private int red_type;

	private int tot_amount;

	private int any_amount;

	private int num;

	private String content;

	private long send_tm;

	private long invalid_tm;

	private int rec_status;

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

	public int getRed_type() {
		return red_type;
	}

	public void setRed_type(int red_type) {
		this.red_type = red_type;
	}

	public int getTot_amount() {
		return tot_amount;
	}

	public void setTot_amount(int tot_amount) {
		this.tot_amount = tot_amount;
	}

	public int getAny_amount() {
		return any_amount;
	}

	public void setAny_amount(int any_amount) {
		this.any_amount = any_amount;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
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

	public long getInvalid_tm() {
		return invalid_tm;
	}

	public void setInvalid_tm(long invalid_tm) {
		this.invalid_tm = invalid_tm;
	}

	public int getRec_status() {
		return rec_status;
	}

	public void setRec_status(int rec_status) {
		this.rec_status = rec_status;
	}

	public int getPay_status() {
		return pay_status;
	}

	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}

}