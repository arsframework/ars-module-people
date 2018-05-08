package ars.module.people.model;

import java.util.Set;
import java.util.HashSet;

import ars.module.system.model.Menu;
import ars.database.model.AbstractModel;

/**
 * 角色数据模型
 *
 * @author wuyongqiang
 */
public class Role extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private String code; // 编号
    private String name; // 名称
    private Boolean innate = false; // 内置角色
    private String operable; // 操作权限
    private Set<Menu> menus = new HashSet<Menu>(0); // 菜单集合

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getInnate() {
        return innate;
    }

    public void setInnate(Boolean innate) {
        this.innate = innate;
    }

    public String getOperable() {
        return operable;
    }

    public void setOperable(String operable) {
        this.operable = operable;
    }

    public Set<Menu> getMenus() {
        return menus;
    }

    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }

    @Override
    public String toString() {
        return this.name == null ? super.toString() : this.name;
    }

}
