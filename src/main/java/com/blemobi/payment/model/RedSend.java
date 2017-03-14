package com.blemobi.payment.model;

import com.blemobi.library.redis.RedisManager;
import com.google.common.base.Strings;

import redis.clients.jedis.Jedis;

public class RedSend {

	private int id;

	private String ord_no;

	private String send_uuid;

	private int type;

	private int tota_money;

	private int each_money;

	private int tota_number;

	private int rece_money;

	private int rece_number;

	private String content;

	private long send_tm;

	private long over_tm;

	private int pay_status;

	private int ref_status;

	private int rece_tota_num;

	private String rece_uuid5;

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

	public String getSend_uuid() {
		return send_uuid;
	}

	public void setSend_uuid(String send_uuid) {
		this.send_uuid = send_uuid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTota_money() {
		return tota_money;
	}

	public void setTota_money(int tota_money) {
		this.tota_money = tota_money;
	}

	public int getEach_money() {
		return each_money;
	}

	public void setEach_money(int each_money) {
		this.each_money = each_money;
	}

	public int getTota_number() {
		return tota_number;
	}

	public void setTota_number(int tota_number) {
		this.tota_number = tota_number;
	}

	public int getRece_money() {
		return rece_money;
	}

	public void setRece_money(int rece_money) {
		this.rece_money = rece_money;
	}

	public int getRece_number() {
		return rece_number;
	}

	public void setRece_number(int rece_number) {
		this.rece_number = rece_number;
	}

	private final String CONTENT_KEY = "payment:content:";

	public String getContent() {
		if (Strings.isNullOrEmpty(content)) {
			String key = CONTENT_KEY + ord_no;
			Jedis jedis = RedisManager.getRedis();
			content = jedis.get(key);
			RedisManager.returnResource(jedis);
		}

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

	public long getOver_tm() {
		return over_tm;
	}

	public void setOver_tm(long over_tm) {
		this.over_tm = over_tm;
	}

	public int getPay_status() {
		return pay_status;
	}

	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}

	public int getRef_status() {
		return ref_status;
	}

	public void setRef_status(int ref_status) {
		this.ref_status = ref_status;
	}

	public int getRece_tota_num() {
		return rece_tota_num;
	}

	public void setRece_tota_num(int rece_tota_num) {
		this.rece_tota_num = rece_tota_num;
	}

	public String getRece_uuid5() {
		return rece_uuid5;
	}

	public void setRece_uuid5(String rece_uuid5) {
		this.rece_uuid5 = rece_uuid5;
	}

}