package ars.module.people.assist;

import ars.module.people.model.User;

/**
 * 用户密码生成器简单实现
 *
 * @author wuyongqiang
 */
public class SimplePasswordGenerator implements PasswordGenerator {
    protected final String password; // 密码明文

    public SimplePasswordGenerator() {
        this(Passwords.DEFAULT_PASSWORD);
    }

    public SimplePasswordGenerator(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        this.password = password;
    }

    @Override
    public String generate(User user) {
        return this.password;
    }

}
