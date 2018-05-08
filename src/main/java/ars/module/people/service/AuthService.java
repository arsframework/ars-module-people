package ars.module.people.service;

import java.io.IOException;

import ars.invoke.local.Api;
import ars.invoke.local.Param;
import ars.invoke.request.Token;
import ars.invoke.request.Requester;

/**
 * 用户认证业务操作接口
 *
 * @author wuyongqiang
 */
@Api("people/auth")
public interface AuthService {
    /**
     * 获取登录验证码
     *
     * @param requester 请求对象
     * @return 验证码图片字节数组
     * @throws IOException IO操作异常
     */
    @Api("verifycode")
    public byte[] verifycode(Requester requester) throws IOException;

    /**
     * 用户登陆
     *
     * @param requester 请求对象
     * @param code      用户编号
     * @param password  用户密码（明文）
     * @return 令牌对象
     */
    @Api("login")
    public Token login(Requester requester, @Param(name = "code", required = true) String code,
                       @Param(name = "password", required = true) String password);

    /**
     * 用户注销
     *
     * @param requester 请求对象
     */
    @Api("logout")
    public void logout(Requester requester);

    /**
     * 判断资源是否允许访问
     *
     * @param requester 请求对象
     * @param uri       资源地址
     * @return true/false
     */
    @Api("permissible")
    public boolean permissible(Requester requester, @Param(name = "uri", required = true) String uri);

}
