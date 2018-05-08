package ars.module.people.service;

import ars.invoke.request.Requester;
import ars.module.people.model.User;
import ars.module.people.model.Role;
import ars.database.repository.Repositories;
import ars.database.service.StandardGeneralService;
import ars.database.repository.DataConstraintException;

/**
 * 角色业务操作抽象实现
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public abstract class AbstractRoleService<T extends Role> extends StandardGeneralService<T> implements RoleService<T> {

    @Override
    public void deleteObject(Requester requester, T object) {
        if (object.getInnate() == Boolean.TRUE) {
            throw new IllegalStateException("Innate role is not deletable:" + object);
        }
        User user = Repositories.getRepository(User.class).query().in("roles.id", new Integer[]{object.getId()})
            .paging(1, 1).single();
        if (user != null) {
            String message = new StringBuilder(requester.format(user.getClass().getName())).append('[')
                .append(user.toString()).append(']').toString();
            throw new DataConstraintException(message);
        }
        super.deleteObject(requester, object);
    }

}
