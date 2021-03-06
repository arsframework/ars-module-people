package ars.module.people.service;

import java.util.Map;
import java.util.List;

import ars.util.SimpleTree;
import ars.invoke.local.Api;
import ars.invoke.local.Param;
import ars.invoke.request.Requester;
import ars.module.people.model.User;
import ars.database.service.BasicService;

/**
 * 用户业务操作接口
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
@Api("people/user")
public interface UserService<T extends User> extends BasicService<T> {
	/**
	 * 获取用户树（部门为树枝、用户为树干）
	 * 
	 * @param requester
	 *            请求对象
	 * @param groups
	 *            所属部门主键数组
	 * @param level
	 *            开始层级
	 * @param parameters
	 *            请求参数
	 * @return 用户组织架构树列表
	 */
	@Api("trees")
	public List<SimpleTree> trees(Requester requester, @Param(name = "group") Integer[] groups,
			@Param(name = "level") Integer level, Map<String, Object> parameters);

	/**
	 * 用户批量授权
	 * 
	 * @param requester
	 *            请求对象
	 * @param users
	 *            用户主键数组
	 * @param roles
	 *            角色主键数组
	 * @param cover
	 *            是否为覆盖模式，否则为追加模式
	 * @param parameters
	 *            请求参数
	 */
	@Api("empower")
	public void empower(Requester requester, @Param(name = "user", required = true) Integer[] users,
			@Param(name = "role", required = true) Integer[] roles,
			@Param(name = "cover", required = true) Boolean cover, Map<String, Object> parameters);

	/**
	 * 重置密码
	 * 
	 * @param requester
	 *            请求对象
	 * @param id
	 *            用户主键
	 * @param code
	 *            用户编号
	 * @param name
	 *            用户名称
	 * @param password
	 *            用户密码（明文）
	 * @param parameters
	 *            请求参数
	 */
	@Api("password/reset")
	public void password(Requester requester, @Param(name = "id", required = true) Integer id,
			@Param(name = "code", required = true) String code, @Param(name = "name", required = true) String name,
			@Param(name = "password", required = true) String password, Map<String, Object> parameters);

}
