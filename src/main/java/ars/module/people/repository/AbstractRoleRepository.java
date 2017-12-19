package ars.module.people.repository;

import ars.module.people.model.Role;
import ars.module.people.repository.RoleRepository;
import ars.database.hibernate.HibernateSimpleRepository;

/**
 * 角色数据持久抽象实现
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
public abstract class AbstractRoleRepository<T extends Role> extends HibernateSimpleRepository<T>
		implements RoleRepository<T> {

}
