package ars.module.people.service;

import java.util.Map;
import java.util.List;

import ars.invoke.request.Requester;
import ars.invoke.request.AccessDeniedException;
import ars.invoke.request.ParameterInvalidException;
import ars.database.repository.Query;
import ars.database.repository.Repositories;
import ars.database.repository.Repository;
import ars.database.service.StandardGeneralService;
import ars.module.people.model.User;
import ars.module.people.model.Group;
import ars.module.people.service.GroupService;

/**
 * 部门业务操作抽象实现
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
public abstract class AbstractGroupService<T extends Group> extends StandardGeneralService<T>
		implements GroupService<T> {

	@Override
	public void initObject(Requester requester, T entity, Map<String, Object> parameters) {
		super.initObject(requester, entity, parameters);
		Group parent = entity.getParent();
		User owner = Repositories.getRepository(User.class).query().eq("code", requester.getUser()).single();
		if (!owner.getAdmin() && owner.getGroup().getParent() != null
				&& (parent == null || !parent.getKey().startsWith(owner.getGroup().getKey()))) {
			throw new ParameterInvalidException("parent", "illegal");
		}
		Query<T> query = this.getRepository().query().ne("id", entity.getId()).eq("name", entity.getName());
		if (parent == null) {
			query.empty("parent");
		} else {
			query.eq("parent", parent);
		}
		if (query.count() > 0) {
			throw new ParameterInvalidException("name", "exist");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateObject(Requester requester, T object) {
		T parent = (T) object.getParent();
		Repository<T> repository = this.getRepository();
		T old = repository.get(object.getId());
		super.updateObject(requester, object);
		if (old.getActive() != object.getActive()) {
			if (object.getActive() == Boolean.TRUE) {
				while (parent != null) {
					if (parent.getActive() == Boolean.FALSE) {
						parent.setActive(true);
						repository.update(parent);
					}
					parent = (T) parent.getParent();
				}
			} else if (object.getActive() == Boolean.FALSE) {
				List<T> groups = repository.query().ne("id", object.getId()).eq("active", true)
						.start("key", object.getKey()).list();
				for (int i = 0; i < groups.size(); i++) {
					T group = groups.get(i);
					group.setActive(false);
					repository.update(group);
				}
			}
		}
	}

	@Override
	public void deleteObject(Requester requester, T object) {
		User owner = Repositories.getRepository(User.class).query().eq("code", requester.getUser()).single();
		if (!owner.getAdmin() && !object.getKey().startsWith(owner.getGroup().getKey())) {
			throw new AccessDeniedException("error.data.unauthorized");
		}
		super.deleteObject(requester, object);
	}

}
