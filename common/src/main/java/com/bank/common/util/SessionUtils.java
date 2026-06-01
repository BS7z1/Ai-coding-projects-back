package com.bank.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Session 工具类
 * <p>
 * 负责将 LoginUserInfo 存取到当前请求的 HttpSession 中。
 * AssoUtil 通过 new SessionUtils().getLoginUserInfo() 调用。
 */
public class SessionUtils {

    private static final Logger logger = LoggerFactory.getLogger(SessionUtils.class);

    // ------------------------------------------------------------------ 内部方法

    /**
     * 获取当前请求的 HttpSession（不自动创建）
     */
    private HttpSession getSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            logger.warn("SessionUtils: 当前线程无 RequestAttributes，无法获取 Session");
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return request.getSession(false);
    }

    /**
     * 获取当前请求的 HttpSession（不存在时自动创建）
     */
    private HttpSession getOrCreateSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            logger.warn("SessionUtils: 当前线程无 RequestAttributes，无法创建 Session");
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return request.getSession(true);
    }

    // ------------------------------------------------------------------ 公开 API

    /**
     * 从 Session 中读取登录用户信息（供 AssoUtil 调用）
     *
     * @return 已登录时返回 LoginUserInfo，未登录或 Session 不存在时返回 null
     */
    public LoginUserInfo getLoginUserInfo() {
        HttpSession session = getSession();
        if (session == null) {
            return null;
        }
        Object obj = session.getAttribute(LoginUserInfo.SESSION_KEY);
        if (obj instanceof LoginUserInfo) {
            return (LoginUserInfo) obj;
        }
        return null;
    }

    /**
     * 将登录用户信息写入 Session（登录成功后调用）
     *
     * @param loginUserInfo 登录用户信息
     */
    public void setLoginUserInfo(LoginUserInfo loginUserInfo) {
        HttpSession session = getOrCreateSession();
        if (session == null) {
            logger.error("SessionUtils: 无法创建 Session，登录信息写入失败");
            return;
        }
        session.setAttribute(LoginUserInfo.SESSION_KEY, loginUserInfo);
        logger.info("SessionUtils: 用户 [{}] 登录信息已写入 Session，SessionId={}",
                loginUserInfo.getUser() != null ? loginUserInfo.getUser().getLoginNo() : "unknown",
                session.getId());
    }

    /**
     * 清除 Session 中的登录信息并使 Session 失效（退出登录时调用）
     */
    public void clearLoginUserInfo() {
        HttpSession session = getSession();
        if (session != null) {
            session.removeAttribute(LoginUserInfo.SESSION_KEY);
            session.invalidate();
            logger.info("SessionUtils: Session 已清除并失效");
        }
    }
}
