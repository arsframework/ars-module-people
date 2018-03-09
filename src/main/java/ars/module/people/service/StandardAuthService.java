package ars.module.people.service;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import ars.util.Cache;
import ars.util.Beans;
import ars.util.Opcodes;
import ars.util.Strings;
import ars.util.Servers;
import ars.util.SimpleCache;
import ars.invoke.Invokes;
import ars.invoke.request.Token;
import ars.invoke.request.Requester;
import ars.invoke.request.Session;
import ars.invoke.request.TokenInvalidException;
import ars.invoke.request.AccessDeniedException;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeBeforeEvent;
import ars.module.people.model.User;
import ars.module.people.model.Role;
import ars.module.people.model.Logined;
import ars.module.people.assist.Passwords;
import ars.module.people.service.AuthService;
import ars.database.repository.Repository;
import ars.database.repository.Repositories;
import ars.database.service.event.UpdateEvent;
import ars.database.service.event.ServiceListener;

/**
 * 用户认证业务操作标准实现
 * 
 * @author yongqiangwu
 *
 */
public class StandardAuthService
		implements AuthService, InvokeListener<InvokeBeforeEvent>, ServiceListener<UpdateEvent> {
	/**
	 * 令牌缓存标识前缀
	 */
	public static final String TOKEN_CACHE_PREFIX = "token_";

	/**
	 * 用户权限标识
	 */
	public static final String TOKEN_KEY_PERMISSION = "permission";

	/**
	 * 登录失败次数会话标识
	 */
	public static final String SESSION_KEY_ERRORS = "__login_errors";

	/**
	 * 验证码会话标识
	 */
	public static final String SESSION_KEY_VALID_CODE = "__login_valid_code";

	private String pattern; // 请求认证资源地址匹配模式
	private int errors = 5; // 可出错次数
	private Cache cache = new SimpleCache(); // 认证信息缓存
	private int timeout = 24 * 60 * 60; // 认证信息超时时间（秒）

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		if (errors < 0) {
			throw new IllegalArgumentException("Illegal errors:" + errors);
		}
		this.errors = errors;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		if (timeout < 1) {
			throw new IllegalArgumentException("Illegal timeout:" + timeout);
		}
		this.timeout = timeout;
	}

	/**
	 * 用户请求认证
	 * 
	 * @param requester
	 *            请求对象
	 */
	protected void authentication(Requester requester) {
		Token token = requester.getToken();
		if (token == null) {
			throw new TokenInvalidException("error.token.unbound");
		}
		token.validate();
		String code = (String) this.cache.get(TOKEN_CACHE_PREFIX + requester.getUser());
		if (code == null) {
			throw new TokenInvalidException("error.token.unfound");
		} else if (!code.equals(token.getCode())) {
			throw new TokenInvalidException("error.token.reset");
		}
		String permission = (String) token.get(TOKEN_KEY_PERMISSION);
		if (permission == null || !Strings.matches(requester.getUri(), permission)) {
			throw new AccessDeniedException("error.token.unauthorized");
		}
	}

	/**
	 * 用户登陆
	 * 
	 * @param requester
	 *            请求对象
	 * @param code
	 *            用户编号
	 * @param password
	 *            用户密码（明文）
	 * @return 令牌对象
	 */
	protected Token doLogin(final Requester requester, final String code, String password) {
		User user = Repositories.getRepository(User.class).query().eq("code", code).single();
		if (user == null || !code.equals(user.getCode())) {
			throw new AccessDeniedException("error.user.unknown");
		} else if (user.getActive() != Boolean.TRUE) {
			throw new AccessDeniedException("error.user.disabled");
		} else if (!Passwords.matches(password, user.getPassword())) {
			throw new AccessDeniedException("error.user.invalid");
		}
		Map<String, Object> attributes = new HashMap<String, Object>(1);
		if (user.getAdmin()) {
			attributes.put(TOKEN_KEY_PERMISSION, "*");
		} else {
			Set<String> operables = new HashSet<String>(user.getRoles().size());
			for (Role role : user.getRoles()) {
				if (!role.getActive()) {
					continue;
				}
				String operable = role.getOperable();
				if (operable != null) {
					operables.add(operable);
				}
			}
			if (!operables.isEmpty()) {
				attributes.put(TOKEN_KEY_PERMISSION, Strings.join(operables, ','));
			}
		}
		Token token = Token.build(Strings.LOCALHOST_ADDRESS, code, this.timeout, attributes);
		this.cache.set(TOKEN_CACHE_PREFIX + code, token.getCode());
		Servers.execute(new Runnable() {

			@Override
			public void run() {
				Repository<Logined> repository = Repositories.getRepository(Logined.class);
				Logined logined = Beans.getInstance(repository.getModel());
				logined.setUser(code);
				logined.setHost(requester.getHost());
				logined.setSpend(System.currentTimeMillis() - requester.getCreated().getTime());
				repository.save(logined);
			}

		});
		return token;
	}

	@Override
	public void onInvokeEvent(InvokeBeforeEvent event) {
		Requester requester = event.getSource();
		if (!"people/auth/login".equals(requester.getUri()) && !"people/auth/verifycode".equals(requester.getUri())) {
			Requester root = null;
			if (this.pattern == null) {
				this.authentication(requester);
			} else if ((root = Invokes.getRootRequester(requester)) == requester) {
				if (Strings.matches(requester.getUri(), this.pattern)) {
					this.authentication(requester);
				}
			} else if (Strings.matches(root.getUri(), this.pattern)
					&& Strings.matches(requester.getUri(), this.pattern)) {
				this.authentication(requester);
			}
		}
	}

	@Override
	public void onServiceEvent(UpdateEvent event) {
		Object entity = event.getEntity();
		if (entity instanceof User) {
			User user = (User) entity;
			User original = Repositories.getRepository(User.class).get(user.getId());
			if (!Beans.isEqual(user.getActive(), original.getActive())
					|| !Beans.isEqual(user.getGroup(), original.getGroup())
					|| !Beans.isEqual(user.getRoles(), original.getRoles())) {
				this.cache.remove(TOKEN_CACHE_PREFIX + user.getCode());
			}
		}
	}

	@Override
	public byte[] verifycode(Requester requester) throws IOException {
		String content = Strings.random(Strings.CHARS, 4).toUpperCase();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(Opcodes.encode(content), "jpg", bos);
		} finally {
			bos.close();
		}
		requester.getSession().setAttribute(SESSION_KEY_VALID_CODE, content);
		return bos.toByteArray();
	}

	@Override
	public Token login(Requester requester, String code, String password) {
		if (this.errors == 0) {
			return this.doLogin(requester, code, password);
		}
		Session session = requester.getSession();
		Integer failed = (Integer) session.getAttribute(SESSION_KEY_ERRORS);
		if (failed != null && failed >= this.errors) {
			String verifycode = (String) requester.getParameter("verifycode");
			if (verifycode == null) {
				throw new AccessDeniedException("error.verifycode.required");
			} else if (!verifycode.equalsIgnoreCase((String) session.getAttribute(SESSION_KEY_VALID_CODE))) {
				throw new AccessDeniedException("error.verifycode.invalid");
			}
		}
		try {
			Token token = this.doLogin(requester, code, password);
			session.removeAttribute(SESSION_KEY_ERRORS);
			return token;
		} catch (Exception e) {
			if (failed == null) {
				session.setAttribute(SESSION_KEY_ERRORS, 1);
			} else if (failed < this.errors) {
				session.setAttribute(SESSION_KEY_ERRORS, failed + 1);
			}
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e);
		} finally {
			session.removeAttribute(SESSION_KEY_VALID_CODE);
		}
	}

	@Override
	public void logout(Requester requester) {
		this.cache.remove(TOKEN_CACHE_PREFIX + requester.getUser());
	}

	@Override
	public boolean permissible(Requester requester, String uri) {
		String permission = (String) requester.getToken().get(TOKEN_KEY_PERMISSION);
		return permission != null && Strings.matches(uri, permission);
	}

}
