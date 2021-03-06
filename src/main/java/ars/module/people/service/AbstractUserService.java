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
import ars.util.SimpleTree;
import ars.invoke.request.Requester;
import ars.invoke.request.AccessDeniedException;
import ars.invoke.request.ParameterInvalidException;
import ars.module.people.model.Role;
import ars.module.people.model.User;
import ars.module.people.model.Group;
import ars.module.people.assist.Passwords;
import ars.module.people.assist.PasswordGenerator;
import ars.module.people.assist.SimplePasswordGenerator;
import ars.module.people.service.UserService;
import ars.database.repository.Query;
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
public abstract class AbstractUserService<T extends User> extends StandardGeneralService<T> implements UserService<T> {
	private PasswordGenerator passwordGenerator;

	public PasswordGenerator getPasswordGenerator() {
		if (this.passwordGenerator == null) {
			synchronized (this) {
				if (this.passwordGenerator == null) {
					this.passwordGenerator = new SimplePasswordGenerator();
				}
			}
		}
		return passwordGenerator;
	}

	public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
		this.passwordGenerator = passwordGenerator;
	}

	@Override
	public void initObject(Requester requester, T entity) {
		super.initObject(requester, entity);
		Group group = entity.getGroup();
		if (group == null) {
			throw new ParameterInvalidException("group", "required");
		}
		User owner = this.getRepository().query().eq("code", requester.getUser()).single();
		if (!owner.getAdmin() && !group.getKey().startsWith(owner.getGroup().getKey())) {
			throw new ParameterInvalidException("group", "illegal");
		}
	}

	@Override
	public Serializable saveObject(Requester requester, T object) {
		if (object.getPassword() == null) {
			object.setPassword(Passwords.encode(this.getPasswordGenerator().generate(object)));
		} else {
			object.setPassword(Passwords.encode(object.getPassword()));
		}
		return super.saveObject(requester, object);
	}

	@Override
	public void deleteObject(Requester requester, T object) {
		User owner = this.getRepository().query().eq("code", requester.getUser()).single();
		if (!owner.getAdmin() && !object.getGroup().getKey().startsWith(owner.getGroup().getKey())) {
			throw new AccessDeniedException("error.data.unauthorized");
		}
		super.deleteObject(requester, object);
	}

	@Override
	public List<SimpleTree> trees(Requester requester, Integer[] groups, Integer level,
			Map<String, Object> parameters) {
		boolean grouped = groups != null && groups.length > 0;
		List<Group> departments = grouped
				? Repositories.getRepository(Group.class).query().ge("level", level).in("id", groups).asc("order")
						.list()
				: new LinkedList<Group>();
		Query<T> query = this.getQuery(requester).custom(parameters);
		if (grouped) {
			query.in("group.id", groups);
		}
		List<T> objects = query.list();
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
	}

}
