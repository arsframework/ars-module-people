package ars.module.people.repository;

import ars.module.people.model.User;
import ars.database.repository.Repository;

/**
 * 用户数据操作接口
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
public interface UserRepository<T extends User> extends Repository<T> {

}
