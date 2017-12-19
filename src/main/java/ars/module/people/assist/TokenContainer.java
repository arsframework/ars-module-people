package ars.module.people.assist;

/**
 * 令牌容器接口
 * 
 * @author yongqiangwu
 *
 */
public interface TokenContainer {
	/**
	 * 获取用户标识获取令牌标识
	 * 
	 * @param user
	 *            用户标识
	 * @return 令牌标识
	 */
	public String get(String user);

	/**
	 * 设置用户令牌标识
	 * 
	 * @param user
	 *            用户标识
	 * @param token
	 *            令牌标识
	 * @param timeout
	 *            令牌超时时间（秒）
	 */
	public void set(String user, String token, int timeout);

	/**
	 * 移除用户令牌标识
	 * 
	 * @param user
	 *            用户标识
	 */
	public void remove(String user);

}
