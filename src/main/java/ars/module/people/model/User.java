package ars.module.people.model;

import java.util.Set;
import java.util.HashSet;

import ars.database.model.AbstractModel;

/**
 * 用户数据模型
 *
 * @author wuyongqiang
 */
public class User extends AbstractModel {
    private static final long serialVersionUID = 1L;

    private String code; // 编号
    private String name; // 名称
    private Boolean admin = false; // 是否为管理
    private String password; // 用户密码密文
    private Sex sex; // 性别
    private String logo; // 头像
    private String email; // 电子邮箱
    private String phone; // 固定电话
    private Group group; // 所属部门
    private Set<Role> roles = new HashSet<Role>(0); // 角色集合

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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return this.name == null ? super.toString() : this.name;
    }

    /**
     * 用户性别
     *
     * @author wuyongqiang
     */
    public enum Sex {
        /**
         * 男
         */
        MAN,

        /**
         * 女
         */
        WOMAN;

    }

}
