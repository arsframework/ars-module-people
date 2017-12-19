package ars.module.people.assist;

import org.springframework.beans.factory.FactoryBean;

import ars.util.Strings;
import ars.module.people.assist.Passwords;

/**
 * 密码工厂对象
 * 
 * @author yongqiangwu
 * 
 */
public class PasswordFactoryBean implements FactoryBean<String> {
	private String password;

	public PasswordFactoryBean(String password) {
		this.password = Passwords.encode(password);
	}

	@Override
	public String getObject() throws Exception {
		return this.password;
	}

	@Override
	public Class<?> getObjectType() {
		return Strings.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
