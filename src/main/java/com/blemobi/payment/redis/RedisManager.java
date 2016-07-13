package com.blemobi.payment.redis;

import com.blemobi.payment.global.Constant;

/**
 * @author 李子才<davis.lee@blemobi.com>
 * 这是提供给所有模块的redis服务的调用工具类。
 */

import com.google.common.base.Strings;

import lombok.extern.log4j.Log4j;
import redis.clients.jedis.Jedis;

@Log4j
public class RedisManager {
	/**
	 * 从consul服务中获取到redis服务的IP，端口，token等信息，为其他模块提供redis对象服务。
	 * @return 返回Redis对象。
	 */
	public static Jedis getRedis() {
		String[] redisInfo = Constant.getRedisServer();
		log.info("Redis server addr=["+redisInfo[0]+"], port=["+redisInfo[1]+"]");
		Jedis jedis = new Jedis(redisInfo[0], Integer.parseInt(redisInfo[1]));
		String redisAuth = Constant.getRedisUserAuth();
		if(!Strings.isNullOrEmpty(redisAuth)) jedis.auth(redisAuth); 
		return jedis;
	}
	
}