package ars.module.people.service;

import ars.invoke.local.Api;
import ars.module.people.model.Logined;
import ars.database.service.SearchService;

/**
 * 登录日志业务操作接口
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
@Api("people/logined")
public interface LoginedService<T extends Logined> extends SearchService<T> {

}
