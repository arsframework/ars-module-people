package ars.module.people.repository;

import ars.module.people.model.User;
import ars.module.people.repository.UserRepository;
import ars.database.hibernate.HibernateSimpleRepository;

/**
 * 用户数据持久抽象实现
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
public abstract class AbstractUserRepository<T extends User> extends HibernateSimpleRepository<T>
		implements UserRepository<T> {

}
