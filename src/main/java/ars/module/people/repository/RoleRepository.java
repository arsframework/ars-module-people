package ars.module.people.repository;

import ars.module.people.model.Role;
import ars.database.repository.Repository;

/**
 * 角色数据操作接口
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public interface RoleRepository<T extends Role> extends Repository<T> {

}
