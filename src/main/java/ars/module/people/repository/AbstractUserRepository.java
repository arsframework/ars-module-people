package ars.module.people.repository;

import ars.module.people.model.User;
import ars.database.hibernate.HibernateSimpleRepository;

/**
 * 用户数据持久抽象实现
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public abstract class AbstractUserRepository<T extends User> extends HibernateSimpleRepository<T>
    implements UserRepository<T> {

}
