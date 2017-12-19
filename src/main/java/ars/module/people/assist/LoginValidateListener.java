package ars.module.people.assist;

import java.util.Map;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import ars.util.Opcodes;
import ars.util.Strings;
import ars.invoke.local.Api;
import ars.invoke.request.Requester;
import ars.invoke.request.AccessDeniedException;
import ars.invoke.event.InvokeEvent;
import ars.invoke.event.InvokeListener;
import ars.invoke.event.InvokeBeforeEvent;
import ars.invoke.event.InvokeCompleteEvent;
import ars.invoke.channel.http.HttpRequester;
import ars.module.people.service.UserService;

/**
 * 用户登录验证监听器
 * 
 * @author yongqiangwu
 * 
 */
@Api("people/user")
public class LoginValidateListener implements InvokeListener<InvokeEvent> {
	private static final String SESSION_KEY_ERRORS = "__login_errors";
	private static final String SESSION_KEY_VALID_CODE = "__login_valid_code";
	private static final Character[] CHARS = new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
			'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
			'Y', 'Z' }; // 验证码字符数组

	/**
	 * 获取登录验证码
	 * 
	 * @param requester
	 *            请求对象
	 * @param parameters
	 *            请求参数
	 * @return 验证码图片字节数组
	 * @throws IOException
	 *             IO操作异常
	 */
	@Api("verifycode")
	public byte[] verifycode(Requester requester, Map<String, Object> parameters) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		String content = Strings.random(4, CHARS);
		ImageIO.write(Opcodes.encode(content), "jpg", output);
		HttpSession session = ((HttpRequester) requester).getHttpServletRequest().getSession();
		session.setAttribute(SESSION_KEY_VALID_CODE, content);
		return output.toByteArray();
	}

	@Override
	public void onInvokeEvent(InvokeEvent event) {
		Requester requester = event.getSource();
		if (requester instanceof HttpRequester && UserService.LOGIN_URI.equals(requester.getUri())) {
			if (event instanceof InvokeBeforeEvent) {
				HttpSession session = ((HttpRequester) requester).getHttpServletRequest().getSession();
				Integer errors = (Integer) session.getAttribute(SESSION_KEY_ERRORS);
				if (errors != null && errors >= 5) {
					String verifycode = (String) requester.getParameter("verifycode");
					if (verifycode == null) {
						throw new AccessDeniedException("Verifycode required");
					} else if (!verifycode.equalsIgnoreCase((String) session.getAttribute(SESSION_KEY_VALID_CODE))) {
						throw new AccessDeniedException("Verifycode invalid");
					}
				}
			} else if (event instanceof InvokeCompleteEvent) {
				Object value = ((InvokeCompleteEvent) event).getValue();
				HttpSession session = ((HttpRequester) requester).getHttpServletRequest().getSession();
				Integer errors = (Integer) session.getAttribute(SESSION_KEY_ERRORS);
				if (value instanceof Exception) {
					if (errors == null) {
						session.setAttribute(SESSION_KEY_ERRORS, 1);
					} else if (errors < 5) {
						session.setAttribute(SESSION_KEY_ERRORS, errors + 1);
					}
				} else {
					session.removeAttribute(SESSION_KEY_ERRORS);
				}
				session.removeAttribute(SESSION_KEY_VALID_CODE);
			}
		}
	}

}
