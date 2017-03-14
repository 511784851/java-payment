package com.blemobi.payment.model;

public class Bill {

	private int id;

	private String uuid;

	private String ord_no;

	private int money;

	private long time;

	private int type;

	private int status;

	private String from_uuid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOrd_no() {
		return ord_no;
	}

	public void setOrd_no(String ord_no) {
		this.ord_no = ord_no;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getFrom_uuid() {
		return from_uuid;
	}

	public void setFrom_uuid(String from_uuid) {
		this.from_uuid = from_uuid;
	}

}