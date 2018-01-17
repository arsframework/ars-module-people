package ars.module.people.assist;

import ars.module.people.model.User;

/**
 * 用户密码明文生成器接口
 * 
 * @author yongqiangwu
 *
 */
public interface PasswordGenerator {
	/**
	 * 生成用户密码明文
	 * 
	 * @param user
	 *            用户对象
	 * @return 密码明文
	 */
	public String generate(User user);

}
