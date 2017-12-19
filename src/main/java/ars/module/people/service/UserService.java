package ars.module.people.service;

import java.util.Map;
import java.util.List;

import ars.util.Nfile;
import ars.util.SimpleTree;
import ars.invoke.local.Api;
import ars.invoke.local.Param;
import ars.invoke.request.Token;
import ars.invoke.request.Requester;
import ars.module.people.model.User;
import ars.database.service.BasicService;
import ars.database.service.ImportService;
import ars.database.service.ExportService;

/**
 * 用户业务操作接口
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
@Api("people/user")
public interface UserService<T extends User> extends BasicService<T>, ImportService<T>, ExportService<T> {
	/**
	 * 用户登录资源地址
	 */
	public static final String LOGIN_URI = "people/user/login";

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

	/**
	 * 上传文件
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件目录
	 * @param file
	 *            文件对象
	 * @param parameters
	 *            请求参数
	 * @return 文件路径
	 * @throws Exception
	 *             操作异常
	 */
	@Api("static/upload")
	public String upload(Requester requester, @Param(name = "path") String path,
			@Param(name = "file", required = true) Nfile file, Map<String, Object> parameters) throws Exception;

	/**
	 * 下载文件
	 * 
	 * @param requester
	 *            请求对象
	 * @param path
	 *            文件路径
	 * @param parameters
	 *            附件过滤参数
	 * @return 文件对象
	 * @throws Exception
	 *             操作异常
	 */
	@Api("static/download")
	public Nfile download(Requester requester, @Param(name = "path", required = true) String path,
			Map<String, Object> parameters) throws Exception;

	/**
	 * 用户登陆
	 * 
	 * @param requester
	 *            请求对象
	 * @param code
	 *            用户编号
	 * @param password
	 *            用户密码（明文）
	 * @param parameters
	 *            请求参数
	 * @return 令牌对象
	 */
	@Api("login")
	public Token login(Requester requester, @Param(name = "code", required = true) String code,
			@Param(name = "password", required = true) String password, Map<String, Object> parameters);

	/**
	 * 用户注销
	 * 
	 * @param requester
	 *            请求对象
	 * @param parameters
	 *            请求参数
	 */
	@Api("logout")
	public void logout(Requester requester, Map<String, Object> parameters);

}
