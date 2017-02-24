package com.blemobi.payment.dao.impl;

import java.util.List;

import java.util.Set;

import org.springframework.stereotype.Repository;

import com.blemobi.library.redis.RedisManager;
import com.blemobi.payment.dao.RedJedisDao;

import redis.clients.jedis.Jedis;

/**
 * Redis操作h实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("redJedisDao")
public class RedJedisDaoImpl implements RedJedisDao {

	/** 存储有权限领红包的用户 */
	private final String RECEIVE_KEY = "payment:receive:";

	/** 存储随机红包金额 */
	private final String RANDOM_KEY = "payment:random:";

	/**
	 * 存储有权限领红包的用户
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
	 */
	public String findRandomMoneyByOrdNoAndIdx(String ord_no, long idx) {
		String key = RANDOM_KEY + ord_no;
		Jedis jedis = RedisManager.getRedis();
		List<String> set = jedis.lrange(key, idx, idx);
		RedisManager.returnResource(jedis);
		return set.get(0);
	}
}