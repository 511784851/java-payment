package com.blemobi.payment.dao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Set;

import org.springframework.stereotype.Repository;

import com.blemobi.library.redis.RedisManager;
import com.blemobi.payment.dao.RedJedisDao;
import com.google.common.base.Strings;

import redis.clients.jedis.Jedis;

/**
 * Redis操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("redJedisDao")
public class RedJedisDaoImpl implements RedJedisDao {

	/** 存储有权限领红包的用户 set */
	private final String RECEIVE_KEY = "payment:receive:";

	/** 存储红包随机金额 list */
	private final String RANDOM_KEY = "payment:random:";

	/** 存储用户单日发送总金额 string */
	private final String DAILY_KEY = "payment:daily:";

	/**
	 * 存储有权限领红包的用户
	 * 
	 * @param ord_no
	 *            订单号
	 * @param uuids
	 *            用户uuid
	 */
	public int putReceiveUsers(String ord_no, Object... uuids) {
		String key = RECEIVE_KEY + ord_no;
		Jedis jedis = RedisManager.getRedis();
		for (Object uuid : uuids)
			jedis.zadd(key, 0, uuid.toString());
		RedisManager.returnResource(jedis);
		return 0;
	}

	/**
	 * 查询有权限领红包的用户（根据订单号）
	 * 
	 * @param ord_no
	 *            订单号
	 */
	public Set<String> findUsersByOrdNo(String ord_no) {
		String key = RECEIVE_KEY + ord_no;
		Jedis jedis = RedisManager.getRedis();
		Set<String> set = jedis.zrange(key, 0, -1);
		RedisManager.returnResource(jedis);
		return set;
	}

	/**
	 * 存储随机红包金额
	 * 
	 * @param ord_no
	 *            订单号
	 * @param moneys
	 *            随机金额
	 */
	public int putRedRandDomMoney(String ord_no, int... moneys) {
		String key = RANDOM_KEY + ord_no;
		Jedis jedis = RedisManager.getRedis();
		for (Object money : moneys)
			jedis.rpush(key, money.toString());
		RedisManager.returnResource(jedis);
		return 0;
	}

	/**
	 * 查询随机红包金额（根据订单号和随机金额索引值）
	 * 
	 * @param ord_no
	 *            订单号
	 * @param idx
	 *            随机红包索引值
	 */
	public String findRandomMoneyByOrdNoAndIdx(String ord_no, long idx) {
		String key = RANDOM_KEY + ord_no;
		Jedis jedis = RedisManager.getRedis();
		List<String> set = jedis.lrange(key, idx, idx);
		RedisManager.returnResource(jedis);
		return set.get(0);
	}

	/**
	 * 累计用户单日发送的金额
	 * 
	 * @param send_uuid
	 *            发送用户uuid
	 * @param money
	 *            发送金额（单位：分）
	 * @return
	 */
	public long incrByDailySendMoney(String send_uuid, int money) {
		long time = getDailyTime();
		String key = DAILY_KEY + send_uuid + ":" + time;
		Jedis jedis = RedisManager.getRedis();
		long newMoney = jedis.incrBy(key, money);
		jedis.expire(key, 48 * 60 * 60);// 48小时过期
		RedisManager.returnResource(jedis);
		return newMoney;
	}

	/**
	 * 查询用户单日发送的金额
	 * 
	 * @param send_uuid
	 *            发送用户uuid
	 * @return 单日已发送总金额（单位：分）
	 */
	public int findDailySendMoney(String send_uuid) {
		long time = getDailyTime();
		String key = DAILY_KEY + time + ":" + send_uuid;
		Jedis jedis = RedisManager.getRedis();
		String money = jedis.get(key);
		RedisManager.returnResource(jedis);
		return Strings.isNullOrEmpty(money) ? 0 : Integer.parseInt(money);
	}

	/**
	 * 获得当日时间戳（精确到秒）
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	private long getDailyTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE), 0, 0, 0);
		Date time = calendar.getTime();
		return time.getTime() / 1000;
	}
	
}