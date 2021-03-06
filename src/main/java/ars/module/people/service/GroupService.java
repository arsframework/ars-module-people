package ars.module.people.service;

import ars.invoke.local.Api;
import ars.module.people.model.Group;
import ars.database.service.TreeService;
import ars.database.service.BasicService;

/**
 * 部门业务操作接口
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
@Api("people/group")
public interface GroupService<T extends Group> extends BasicService<T>, TreeService<T> {

}
