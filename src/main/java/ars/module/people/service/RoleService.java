package ars.module.people.service;

import ars.invoke.local.Api;
import ars.module.people.model.Role;
import ars.database.service.BasicService;

/**
 * 角色业务操作接口
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
@Api("people/role")
public interface RoleService<T extends Role> extends BasicService<T> {

}
