package ars.module.people.service;

import java.util.Map;
import java.util.List;

import ars.invoke.local.Api;
import ars.invoke.local.Param;
import ars.invoke.request.Requester;
import ars.module.system.model.Menu;
import ars.module.people.model.User;
import ars.module.people.model.Logined;

/**
 * 当前用户业务操作接口
 * 
 * @author yongqiangwu
 * 
 */
@Api("people/owner")
public interface OwnerService {
	/**
	 * 获取当前用户
	 * 
	 * @param requester
	 *            请求对象
	 * @param parameters
	 *            请求参数
	 * @return 用户对象
	 */
	@Api("info")
	public User info(Requester requester, Map<String, Object> parameters);

	/**
	 * 获取当前用户上次登录日志
	 * 
	 * @param requester
	 *            请求对象
	 * @param parameters
	 *            请求参数
	 * @return 登录日志
	 */
	@Api("logined")
	public Logined logined(Requester requester, Map<String, Object> parameters);

	/**
	 * 获取当前用户菜单树列表
	 * 
	 * @param requester
	 *            请求对象
	 * @param parameters
	 *            请求参数
	 * @return 菜单树列表
	 */
	@Api("menus")
	public List<Menu> menus(Requester requester, Map<String, Object> parameters);

	/**
	 * 更新个人信息
	 * 
	 * @param requester
	 *            请求对象
	 * @param id
	 *            用户主键
	 * @param parameters
	 *            请求参数
	 */
	@Api("update")
	public void update(Requester requester, @Param(name = "id", required = true) Integer id,
			Map<String, Object> parameters);

	/**
	 * 修改密码
	 * 
	 * @param requester
	 *            请求对象
	 * @param original
	 *            原始密码
	 * @param password
	 *            新密码
	 * @param parameters
	 *            请求参数
	 */
	@Api("password/update")
	public void password(Requester requester, @Param(name = "original", required = true) String original,
			@Param(name = "password", required = true) String password, Map<String, Object> parameters);

}
