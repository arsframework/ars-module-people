package ars.module.people.assist;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ars.server.task.AbstractTaskServer;
import ars.module.people.assist.TokenContainer;

/**
 * 令牌容器简单实现
 * 
 * @author yongqiangwu
 *
 */
public class SimpleTokenContainer implements TokenContainer {
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private Map<String, TokenWrapper> tokens = new HashMap<String, TokenWrapper>();

	public SimpleTokenContainer() {
		this.initializeCleanupServer();
	}

	/**
	 * 令牌包装类
	 * 
	 * @author yongqiangwu
	 *
	 */
	private class TokenWrapper {
		public final String code; // 令牌标识
		public final int timeout; // 超时时间（秒）
		public final long timestamp; // 当前系统时间戳

		public TokenWrapper(String code, int timeout) {
			this.code = code;
			this.timeout = timeout;
			this.timestamp = System.currentTimeMillis();
		}

	}

	/**
	 * 初始化令牌清理服务
	 */
	protected void initializeCleanupServer() {
		final AbstractTaskServer cleanup = new AbstractTaskServer() {

			@Override
			protected void execute() throws Exception {
				lock.writeLock().lock();
				try {
					Iterator<String> iterator = tokens.keySet().iterator();
					while (iterator.hasNext()) {
						TokenWrapper wrapper = tokens.get(iterator.next());
						if (System.currentTimeMillis() - wrapper.timeout >= wrapper.timestamp) {
							iterator.remove();
						}
					}
				} finally {
					lock.writeLock().unlock();
				}
			}

		};

		cleanup.setConcurrent(true);
		cleanup.setExpression("0 0 0/2 * * ?");
		cleanup.start();
	}

	@Override
	public String get(String user) {
		lock.readLock().lock();
		try {
			TokenWrapper wrapper = this.tokens.get(user);
			return wrapper == null ? null : wrapper.code;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void set(String user, String token, int timeout) {
		lock.writeLock().lock();
		try {
			this.tokens.put(user, new TokenWrapper(token, timeout));
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void remove(String user) {
		lock.writeLock().lock();
		try {
			this.tokens.remove(user);
		} finally {
			lock.writeLock().unlock();
		}
	}

}
