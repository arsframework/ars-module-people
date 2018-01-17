package ars.module.people.assist;

import org.springframework.beans.factory.FactoryBean;

import ars.module.people.assist.Passwords;

/**
 * 密码工厂对象
 * 
 * @author yongqiangwu
 * 
 */
public class PasswordFactoryBean implements FactoryBean<String> {
	private String encrypt; // 密码密文
	protected final String password; // 密码明文

	public PasswordFactoryBean() {
		this(Passwords.DEFAULT_PASSWORD);
	}

	public PasswordFactoryBean(String password) {
		if (password == null) {
			throw new IllegalArgumentException("Illegal password:" + password);
		}
		this.password = password;
	}

	@Override
	public String getObject() throws Exception {
		if (this.encrypt == null) {
			synchronized (this) {
				if (this.encrypt == null) {
					this.encrypt = Passwords.encode(this.password);
				}
			}
		}
		return this.encrypt;
	}

	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
