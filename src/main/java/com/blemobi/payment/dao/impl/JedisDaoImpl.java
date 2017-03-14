package com.blemobi.payment.dao.impl;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
//github.com/blemobi/java-payment.git
import org.springframework.stereotype.Repository;

import com.blemobi.library.redis.RedisManager;
import com.blemobi.payment.dao.JedisDao;
import com.google.common.base.Strings;

import redis.clients.jedis.Jedis;

/**
 * Redis操作实现类
 * 
 * @author zhaoyong
 *
 */
@Repository("jedisDao")
public class JedisDaoImpl implements JedisDao {

	/** 存储用户单日发送总金额 string */
	private final String DAILY_KEY = "payment:daily:";

	@Override
	public long incrByDailySendMoney(String send_uuid, int money) {
		long time = getDailyTime();
		String key = DAILY_KEY + send_uuid + ":" + time;
		Jedis jedis = RedisManager.getRedis();
		long newMoney = jedis.incrBy(key, money);
		jedis.expire(key, 48 * 60 * 60);// 48小时过期
		RedisManager.returnResource(jedis);
		return newMoney;
	}

	@Override
	public int findDailySendMoney(String send_uuid) {
		long time = getDailyTime();
		String key = DAILY_KEY + time + ":" + send_uuid;
		Jedis jedis = RedisManager.getRedis();
		String money = jedis.get(key);
		RedisManager.returnResource(jedis);
		return Strings.isNullOrEmpty(money) ? 0 : Integer.parseInt(money);
	}

	@Override
	public Integer getUserLotteryRefreshTimes(String uuid) {
		String key = "payment:LOTTERY:CD:" + uuid;
		Jedis jedis = RedisManager.getRedis();
		String times = jedis.get(key);
		RedisManager.returnResource(jedis);
		return StringUtils.isEmpty(times) ? 0 : Integer.parseInt(times);
	}

	@Override
	public void setUserLotteryRefreshTimes(String uuid) {
		String key = "payment:LOTTERY:CD:" + uuid;
		Jedis jedis = RedisManager.getRedis();
		String times = jedis.get(key);
		int time = 1;
		if(!StringUtils.isEmpty(times)){
		    time = Integer.parseInt(times) + 1;
		}
		jedis.incrBy(key, time);
		//jedis.expire(key, 5 * 60);// 5mins
		//TODO PROD remove
		jedis.expire(key, 10);// 10sec
		RedisManager.returnResource(jedis);
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

    @Override
    public void cleanLotteryCD(String uuid) {
        String key = "payment:LOTTERY:CD:" + uuid;
        Jedis jedis = RedisManager.getRedis();
        jedis.del(key);
        RedisManager.returnResource(jedis);
    }
}