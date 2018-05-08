package ars.module.people.assist;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;

import ars.module.people.model.Role;
import ars.module.people.model.User;
import ars.database.repository.Repositories;

/**
 * Activiti组管理器实现
 *
 * @author wuyongqiang
 */
public class ActivitiGroupEntityManager extends GroupEntityManager {

    @Override
    public List<Group> findGroupsByUser(String code) {
        User user = Repositories.getRepository(User.class).query().eq("code", code).single();
        Set<Role> roles = user.getRoles();
        List<Group> groups = new ArrayList<Group>(roles.size());
        for (Role role : roles) {
            if (role.getActive()) {
                GroupEntity group = new GroupEntity();
                group.setId(role.getCode());
                group.setName(role.getName());
                group.setType("assignment");
                groups.add(group);
            }
        }
        return groups;
    }

}
