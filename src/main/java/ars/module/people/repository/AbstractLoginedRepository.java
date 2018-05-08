package ars.module.people.repository;

import ars.module.people.model.Logined;
import ars.database.hibernate.HibernateSimpleRepository;

/**
 * 登录日志数据持久抽象实现
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public abstract class AbstractLoginedRepository<T extends Logined> extends HibernateSimpleRepository<T>
    implements LoginedRepository<T> {

}
