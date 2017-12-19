package ars.module.people.assist;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import ars.module.people.assist.TokenContainer;

/**
 * 基于Redis令牌容器实现
 * 
 * @author yongqiangwu
 *
 */
public class RedisTokenContainer implements TokenContainer {
	/**
	 * Redis 令牌标识前缀
	 */
	public static final String KEY_PREFIX = "ars_auth_token_";

	private JedisPool pool = new JedisPool();

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	@Override
	public String get(String user) {
		Jedis jedis = this.pool.getResource();
		synchronized (user.intern()) {
			return jedis.get(KEY_PREFIX + user);
		}
	}

	@Override
	public void set(String user, String token, int timeout) {
		Jedis jedis = this.pool.getResource();
		synchronized (user.intern()) {
			jedis.setex(KEY_PREFIX + user, timeout, token);
		}
	}

	@Override
	public void remove(String user) {
		Jedis jedis = this.pool.getResource();
		synchronized (user.intern()) {
			jedis.del(KEY_PREFIX + user);
		}
	}

}
