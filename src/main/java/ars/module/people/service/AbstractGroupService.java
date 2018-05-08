package ars.module.people.service;

import ars.invoke.request.Requester;
import ars.invoke.request.AccessDeniedException;
import ars.invoke.request.ParameterInvalidException;
import ars.module.people.model.User;
import ars.module.people.model.Group;
import ars.database.repository.Query;
import ars.database.repository.Repositories;
import ars.database.service.StandardGeneralService;

/**
 * 部门业务操作抽象实现
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public abstract class AbstractGroupService<T extends Group> extends StandardGeneralService<T>
    implements GroupService<T> {

    @Override
    public void initObject(Requester requester, T entity) {
        super.initObject(requester, entity);
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

    @Override
    public void deleteObject(Requester requester, T object) {
        User owner = Repositories.getRepository(User.class).query().eq("code", requester.getUser()).single();
        if (!owner.getAdmin() && !object.getKey().startsWith(owner.getGroup().getKey())) {
            throw new AccessDeniedException("error.data.unauthorized");
        }
        super.deleteObject(requester, object);
    }

}
