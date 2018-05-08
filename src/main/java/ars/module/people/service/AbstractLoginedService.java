package ars.module.people.service;

import ars.module.people.model.Logined;
import ars.database.service.StandardGeneralService;

/**
 * 登录日志业务操作抽象实现
 *
 * @param <T> 数据模型
 * @author wuyongqiang
 */
public abstract class AbstractLoginedService<T extends Logined> extends StandardGeneralService<T>
    implements LoginedService<T> {

}
