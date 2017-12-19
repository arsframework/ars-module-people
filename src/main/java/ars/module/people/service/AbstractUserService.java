package ars.module.people.service;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.Serializable;

import ars.util.Beans;
import ars.util.Nfile;
import ars.util.Strings;
import ars.util.SimpleTree;
import ars.file.Operator;
import ars.file.DiskOperator;
import ars.file.RandomNameGenerator;
import ars.file.DateDirectoryGenerator;
import ars.invoke.request.Token;
import ars.invoke.request.Requester;
import ars.invoke.request.TokenInvalidException;
import ars.invoke.request.AccessDeniedException;
import ars.invoke.request.RequestHandleException;
import ars.invoke.request.ParameterInvalidException;
import ars.invoke.event.InvokeListener;
import ars.invoke.Invokes;
import ars.invoke.event.InvokeBeforeEvent;
import ars.module.people.model.Role;
import ars.module.people.model.User;
import ars.module.people.model.Group;
import ars.module.people.assist.Passwords;
import ars.module.people.assist.SimpleTokenContainer;
import ars.module.people.assist.TokenContainer;
import ars.module.people.service.UserService;
import ars.database.repository.Repositories;
import ars.database.service.StandardGeneralService;

/**
 * 用户业务操作抽象实现
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
public abstract class AbstractUserService<T extends User> extends StandardGeneralService<T>
		implements UserService<T>, InvokeListener<InvokeBeforeEvent> {
	/**
	 * 用户权限标识
	 */
	public static final String TOKEN_KEY_PERMISSION = "permission";

	private String auth; // 请求认证资源地址匹配模式
	private String staticDirectory; // 用户静态资源目录
	private Operator staticOperator;
	private int tokenTimeout = 24 * 60 * 60; // 令牌超时时间（秒）
	private TokenContainer tokenContainer = new SimpleTokenContainer(); // 令牌容器

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getStaticDirectory() {
		return staticDirectory;
	}

	public void setStaticDirectory(String staticDirectory) {
		this.staticDirectory = staticDirectory;
		DiskOperator operator = new DiskOperator(staticDirectory);
		operator.setNameGenerator(new RandomNameGenerator());
		operator.setDirectoryGenerator(new DateDirectoryGenerator());
		this.staticOperator = operator;
	}

	public Operator getStaticOperator() {
		return staticOperator;
	}

	public void setStaticOperator(Operator staticOperator) {
		this.staticOperator = staticOperator;
	}

	public int getTokenTimeout() {
		return tokenTimeout;
	}

	public void setTokenTimeout(int tokenTimeout) {
		if (tokenTimeout < 1) {
			throw new IllegalArgumentException("Illegal tokenTimeout:" + tokenTimeout);
		}
		this.tokenTimeout = tokenTimeout;
	}

	public TokenContainer getTokenContainer() {
		return tokenContainer;
	}

	public void setTokenContainer(TokenContainer tokenContainer) {
		this.tokenContainer = tokenContainer;
	}

	/**
	 * 用户请求认证
	 * 
	 * @param requester
	 *            请求对象
	 */
	protected void authentication(Requester requester) {
		Token token = requester.getToken();
		if (token == null) {
			throw new TokenInvalidException("Token unbound");
		}
		token.validate();
		String code = this.tokenContainer.get(requester.getUser());
		if (code == null) {
			throw new TokenInvalidException("User reset");
		} else if (!code.equals(token.getCode())) {
			throw new TokenInvalidException("Token reset");
		}
		String permission = (String) token.get(TOKEN_KEY_PERMISSION);
		if (permission == null || !Strings.matches(requester.getUri(), permission)) {
			throw new AccessDeniedException("No resource permission");
		}
	}

	@Override
	public void onInvokeEvent(InvokeBeforeEvent event) {
		Requester requester = event.getSource();
		if (!LOGIN_URI.equals(requester.getUri())) {
			Requester root = null;
			if (this.auth == null) {
				this.authentication(requester);
			} else if ((root = Invokes.getRootRequester(requester)) == requester) {
				if (Strings.matches(requester.getUri(), this.auth)) {
					this.authentication(requester);
				}
			} else if (Strings.matches(root.getUri(), this.auth) && Strings.matches(requester.getUri(), this.auth)) {
				this.authentication(requester);
			}
		}
	}

	@Override
	public void initObject(Requester requester, T entity, Map<String, Object> parameters) {
		super.initObject(requester, entity, parameters);
		Group group = entity.getGroup();
		if (group == null) {
			throw new AccessDeniedException("Illegal operation");
		}
		User owner = this.getRepository().query().eq("code", requester.getUser()).single();
		if (!owner.getAdmin() && !group.getKey().startsWith(owner.getGroup().getKey())) {
			throw new AccessDeniedException("Illegal operation");
		}
	}

	@Override
	public Serializable saveObject(Requester requester, T object) {
		if (object.getPassword() == null) {
			object.setPassword(Passwords.encode("123456"));
		} else {
			object.setPassword(Passwords.encode(object.getPassword()));
		}
		return super.saveObject(requester, object);
	}

	@Override
	public void updateObject(Requester requester, T object) {
		User old = this.getRepository().get(object.getId());
		boolean reset = !Beans.isEqual(object.getActive(), old.getActive())
				|| !Beans.isEqual(object.getGroup(), old.getGroup())
				|| !Beans.isEqual(object.getRoles(), old.getRoles());
		super.updateObject(requester, object);
		if (reset) {
			this.tokenContainer.remove(object.getCode());
		}
	}

	@Override
	public void deleteObject(Requester requester, T object) {
		User owner = this.getRepository().query().eq("code", requester.getUser()).single();
		if (!owner.getAdmin() && !object.getGroup().getKey().startsWith(owner.getGroup().getKey())) {
			throw new RequestHandleException("Unauthorized operation");
		}
		super.deleteObject(requester, object);
		this.tokenContainer.remove(object.getCode());
	}

	@Override
	public List<SimpleTree> trees(Requester requester, Integer[] groups, Integer level,
			Map<String, Object> parameters) {
		boolean grouped = groups != null && groups.length > 0;
		List<Group> departments = grouped
				? Repositories.getRepository(Group.class).query().ge("level", level).in("id", groups).asc("order")
						.list()
				: new LinkedList<Group>();
		List<T> objects = grouped ? this.getQuery(requester, parameters).in("group.id", groups).list()
				: this.getQuery(requester, parameters).list();
		if (!grouped && objects.isEmpty()) {
			return new ArrayList<SimpleTree>(0);
		}
		Map<Group, List<SimpleTree>> leaves = new HashMap<Group, List<SimpleTree>>();
		for (int i = 0; i < objects.size(); i++) {
			User user = objects.get(i);
			Group group = user.getGroup();
			List<SimpleTree> users = leaves.get(group);
			if (users == null) {
				users = new LinkedList<SimpleTree>();
				leaves.put(group, users);
			}
			users.add(Repositories.getSimpleTree(user));
			while (!grouped && group != null && (level == null || group.getLevel() >= level)
					&& !departments.contains(group)) {
				departments.add(group);
				group = group.getParent();
			}
		}
		Map<Group, SimpleTree> mappings = new HashMap<Group, SimpleTree>(departments.size());
		List<SimpleTree> trunks = Repositories.getSimpleTrees(Repositories.mergeTrees(departments), mappings);
		for (Entry<Group, List<SimpleTree>> entry : leaves.entrySet()) {
			SimpleTree leaf = mappings.get(entry.getKey());
			List<SimpleTree> children = entry.getValue();
			leaf.getChildren().addAll(0, children);
			for (SimpleTree child : children) {
				child.setParent(leaf);
			}
		}
		return trunks;
	}

	@Override
	public void empower(Requester requester, Integer[] users, Integer[] roles, Boolean cover,
			Map<String, Object> parameters) {
		List<T> persons = this.getQuery(requester).in("id", users).list();
		Set<Role> powers = new HashSet<Role>(Repositories.getRepository(Role.class).query().in("id", roles).list());
		if (persons.isEmpty() || powers.isEmpty()) {
			return;
		}
		for (int i = 0; i < persons.size(); i++) {
			T person = persons.get(i);
			if (cover && !Beans.isEqual(powers, person.getRoles())) {
				person.setRoles(powers);
				updateObject(requester, person);
			} else if (!cover) {
				Set<Role> temp = new HashSet<Role>(person.getRoles());
				person.getRoles().addAll(powers);
				if (!Beans.isEqual(temp, person.getRoles())) {
					updateObject(requester, person);
				}
			}
		}
	}

	@Override
	public void password(Requester requester, Integer id, String code, String name, String password,
			Map<String, Object> parameters) {
		T user = this.getRepository().get(id);
		if (user == null || !code.equals(user.getCode()) || !name.equals(user.getName())) {
			throw new ParameterInvalidException("user", "invalid");
		} else if (password.length() < 6) {
			throw new ParameterInvalidException("password", "invalid");
		}
		user.setPassword(Passwords.encode(password));
		this.getRepository().update(user);
		this.tokenContainer.remove(code);
	}

	@Override
	public String upload(Requester requester, String path, Nfile file, Map<String, Object> parameters)
			throws Exception {
		if (this.staticOperator == null) {
			throw new RuntimeException("Static operator has not been initialize");
		}
		return this.staticOperator.write(file, path);
	}

	@Override
	public Nfile download(Requester requester, String path, Map<String, Object> parameters) throws Exception {
		if (this.staticOperator == null) {
			throw new RuntimeException("Static operator has not been initialize");
		}
		return this.staticOperator.read(path);
	}

	@Override
	public Token login(final Requester requester, final String code, String password, Map<String, Object> parameters) {
		User user = this.getRepository().query().eq("code", code).single();
		if (user == null || !code.equals(user.getCode())) {
			throw new AccessDeniedException("Unknown user");
		} else if (user.getActive() != Boolean.TRUE) {
			throw new AccessDeniedException("User disabled");
		} else if (!Passwords.matches(password, user.getPassword())) {
			throw new AccessDeniedException("Password invalid");
		}
		Map<String, Object> attributes = new HashMap<String, Object>(1);
		if (user.getAdmin()) {
			attributes.put(TOKEN_KEY_PERMISSION, "*");
		} else if (!user.getRoles().isEmpty()) {
			Set<String> operables = new HashSet<String>(user.getRoles().size());
			for (Role role : user.getRoles()) {
				String operable = role.getOperable();
				if (operable != null) {
					operables.add(operable);
				}
			}
			if (!operables.isEmpty()) {
				attributes.put(TOKEN_KEY_PERMISSION, Strings.join(operables, ','));
			}
		}
		Token token = Token.build(Strings.LOCALHOST_ADDRESS, code, this.tokenTimeout, attributes);
		this.tokenContainer.set(code, token.getCode(), token.getTimeout());
		return token;
	}

	@Override
	public void logout(Requester requester, Map<String, Object> parameters) {
		this.tokenContainer.remove(requester.getUser());
	}

}
