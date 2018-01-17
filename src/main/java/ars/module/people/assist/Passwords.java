package ars.module.people.assist;

import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * 密码操作工具类
 * 
 * @author yongqiangwu
 * 
 */
public final class Passwords {
	/**
	 * 默认密码
	 */
	public static final String DEFAULT_PASSWORD = "123456";

	private static final StandardPasswordEncoder passwordEncoder = new StandardPasswordEncoder();

	private Passwords() {

	}

	/**
	 * 明文加密
	 * 
	 * @param express
	 *            明文
	 * @return 密文
	 */
	public static String encode(String express) {
		return express == null ? null : passwordEncoder.encode(express);
	}

	/**
	 * 明文与密文是否相同
	 * 
	 * @param express
	 *            明文
	 * @param password
	 *            密文
	 * @return true/false
	 */
	public static boolean matches(String express, String password) {
		return express == null || password == null ? false : passwordEncoder.matches(express, password);
	}

}
