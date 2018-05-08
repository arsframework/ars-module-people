package ars.module.people.repository;

import ars.module.people.model.Role;
import ars.database.hibernate.HibernateSimpleRepository;

/**
 * 角色数据持久抽象实现
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public abstract class AbstractRoleRepository<T extends Role> extends HibernateSimpleRepository<T>
    implements RoleRepository<T> {

}
