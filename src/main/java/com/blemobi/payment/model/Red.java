package com.blemobi.payment.model;

public class Red {
	private String custorderno;

	private String senduuid;

	private String receiveuuid;

	private long amount;

	private String title;

	private long sendtime;

	private long receivetime;

	private long invalidtime;

	private int status;

	public String getCustorderno() {
		return custorderno;
	}

	public void setCustorderno(String custorderno) {
		this.custorderno = custorderno;
	}

	public String getSenduuid() {
		return senduuid;
	}

	public void setSenduuid(String senduuid) {
		this.senduuid = senduuid;
	}

	public String getReceiveuuid() {
		return receiveuuid;
	}

	public void setReceiveuuid(String receiveuuid) {
		this.receiveuuid = receiveuuid;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getSendtime() {
		return sendtime;
	}

	public void setSendtime(long sendtime) {
		this.sendtime = sendtime;
	}

	public long getReceivetime() {
		return receivetime;
	}

	public void setReceivetime(long receivetime) {
		this.receivetime = receivetime;
	}

	public long getInvalidtime() {
		return invalidtime;
	}

	public void setInvalidtime(long invalidtime) {
		this.invalidtime = invalidtime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}