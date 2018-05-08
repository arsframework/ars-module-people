package ars.module.people.repository;

import ars.module.people.model.Group;
import ars.database.hibernate.HibernateSimpleRepository;

/**
 * 部门数据持久抽象实现
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public abstract class AbstractGroupRepository<T extends Group> extends HibernateSimpleRepository<T>
    implements GroupRepository<T> {

}
