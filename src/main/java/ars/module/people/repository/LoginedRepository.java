package ars.module.people.repository;

import ars.module.people.model.Logined;
import ars.database.repository.Repository;

/**
 * 登录日志数据操作接口
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
public interface LoginedRepository<T extends Logined> extends Repository<T> {

}
