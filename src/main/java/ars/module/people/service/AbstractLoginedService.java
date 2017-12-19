package ars.module.people.service;

import ars.util.Beans;
import ars.server.Servers;
import ars.invoke.request.Requester;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeAfterEvent;
import ars.module.people.model.Logined;
import ars.module.people.service.UserService;
import ars.module.people.service.LoginedService;
import ars.database.service.StandardGeneralService;

/**
 * 登录日志业务操作抽象实现
 * 
 * @author yongqiangwu
 * 
 * @param <T>
 *            数据模型
 */
public abstract class AbstractLoginedService<T extends Logined> extends StandardGeneralService<T>
		implements LoginedService<T>, InvokeListener<InvokeAfterEvent> {

	/**
	 * 日志记录
	 * 
	 * @param requester
	 *            请求对象
	 */
	protected void record(Requester requester) {
		T logined = Beans.getInstance(this.getModel());
		logined.setUser(requester.getUser());
		logined.setHost(requester.getHost());
		logined.setSpend(System.currentTimeMillis() - requester.getCreated().getTime());
		this.saveObject(requester, logined);
	}

	@Override
	public void onInvokeEvent(InvokeAfterEvent event) {
		final Requester requester = event.getSource();
		if (UserService.LOGIN_URI.equals(requester.getUri())) {
			Servers.execute(new Runnable() {

				@Override
				public void run() {
					record(requester);
				}

			});
		}
	}

}
