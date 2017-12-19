package ars.module.people.model;

import java.util.Date;
import java.io.Serializable;

/**
 * 用户登录日志数据模型
 * 
 * @author yongqiangwu
 * 
 */
public class Logined implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id; // 主键
	private String user; // 用户编号
	private String host; // 登陆地址
	private Long spend; // 请求耗时（毫秒）
	private Date dateJoined = new Date(); // 登陆时间

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Long getSpend() {
		return spend;
	}

	public void setSpend(Long spend) {
		this.spend = spend;
	}

	public Date getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	@Override
	public String toString() {
		return this.user == null || this.host == null ? super.toString()
				: new StringBuilder(this.user).append(':').append(this.host).toString();
	}

}
