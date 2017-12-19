package ars.module.people.service;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Collections;

import ars.util.Beans;
import ars.util.Strings;
import ars.invoke.request.Requester;
import ars.invoke.request.ParameterInvalidException;
import ars.module.system.model.Menu;
import ars.module.people.model.User;
import ars.module.people.model.Role;
import ars.module.people.model.Logined;
import ars.module.people.assist.Passwords;
import ars.module.people.service.OwnerService;
import ars.database.repository.Repository;
import ars.database.repository.Repositories;

/**
 * 当前用户业务操作接口标准实现
 * 
 * @author yongqiangwu
 * 
 */
public class StandardOwnerService implements OwnerService {

	@Override
	public User info(Requester requester, Map<String, Object> parameters) {
		return Repositories.getRepository(User.class).query().eq("code", requester.getUser()).single();
	}

	@Override
	public Logined logined(Requester requester, Map<String, Object> parameters) {
		List<Logined> logs = Repositories.getRepository(Logined.class).query().eq("user", requester.getUser())
				.paging(1, 2).desc("dateJoined").list();
		return logs.size() < 2 ? null : logs.get(0);
	}

	@Override
	public List<Menu> menus(Requester requester, Map<String, Object> parameters) {
		User owner = Repositories.getRepository(User.class).query().eq("code", requester.getUser()).single();
		if (owner.getAdmin() == Boolean.TRUE) {
			List<Menu> menus = Repositories.getRepository(Menu.class).query().eq("active", true).asc("order").list();
			return Repositories.mergeTrees(menus);
		}
		Set<Role> roles = owner.getRoles();
		if (roles.isEmpty()) {
			return new ArrayList<Menu>(0);
		}

		int i = 0;
		Integer[] roleIds = new Integer[roles.size()];
		for (Role role : roles) {
			roleIds[i++] = role.getId();
		}
		List<Menu> menus = new LinkedList<Menu>();
		List<Role> nroles = Repositories.getRepository(Role.class).query().in("id", roleIds).list();
		for (Role role : nroles) {
			for (Menu menu : role.getMenus()) {
				if (menu.getActive() == Boolean.TRUE && !menus.contains(menu)) {
					menus.add(menu);
				}
			}
		}
		Collections.sort(menus, new Comparator<Menu>() {

			@Override
			public int compare(Menu o1, Menu o2) {
				return o1.getOrder() < o2.getOrder() ? -1 : o1.getOrder() == o2.getOrder() ? 0 : 1;
			}

		});
		return Repositories.mergeTrees(menus);
	}

	@Override
	public void update(Requester requester, Integer id, Map<String, Object> parameters) {
		Repository<User> repository = Repositories.getRepository(User.class);
		User owner = repository.get(id);
		if (owner != null && !parameters.isEmpty()) {
			boolean modified = false;
			for (Entry<String, Object> entry : parameters.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (key.equals("name")) {
					owner.setName(Strings.toString(value));
					modified = true;
				} else if (key.equals("sex")) {
					owner.setSex(Beans.toEnum(User.Sex.class, value));
					modified = true;
				} else if (key.equals("logo")) {
					owner.setLogo(Strings.toString(value));
					modified = true;
				} else if (key.equals("phone")) {
					owner.setPhone(Strings.toString(value));
					modified = true;
				} else if (key.equals("email")) {
					owner.setEmail(Strings.toString(value));
					modified = true;
				}
			}
			if (modified) {
				repository.update(owner);
			}
		}
	}

	@Override
	public void password(Requester requester, String original, String password, Map<String, Object> parameters) {
		Repository<User> repository = Repositories.getRepository(User.class);
		User user = repository.query().eq("code", requester.getUser()).single();
		if (!Passwords.matches(original, user.getPassword())) {
			throw new ParameterInvalidException("original", "invalid");
		} else if (password.length() < 6) {
			throw new ParameterInvalidException("password", "invalid");
		}
		user.setPassword(Passwords.encode(password));
		repository.update(user);
	}

}
