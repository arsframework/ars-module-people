package ars.module.people.repository;

import ars.module.people.model.Group;
import ars.database.repository.Repository;

/**
 * 部门数据操作接口
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public interface GroupRepository<T extends Group> extends Repository<T> {

}
