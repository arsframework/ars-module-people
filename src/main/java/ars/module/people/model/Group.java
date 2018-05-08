package ars.module.people.model;

import ars.database.model.AbstractTreeModel;

/**
 * 部门数据模型
 *
 * @author wuyongqiang
 */
public class Group extends AbstractTreeModel<Group> {
    private static final long serialVersionUID = 1L;

    private String code; // 编号
    private String name; // 名称
    private String phone; // 联系电话
    private String fax; // 传真号码

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public String toString() {
        return this.name == null ? super.toString() : this.name;
    }

}
