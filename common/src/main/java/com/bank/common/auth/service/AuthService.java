package com.bank.common.auth.service;

import com.bank.common.auth.mapper.SysUserMapper;
import com.bank.common.auth.pojo.SysUser;
import com.bank.common.util.LoginUserInfo;
import com.bank.common.util.Org;
import com.bank.common.util.SessionUtils;
import com.bank.common.util.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 认证服务
 * 负责用户名密码校验、Session 写入、注销等
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 登录校验
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录成功后的 LoginUserInfo；用户不存在或密码错误时返回 null
     */
    public LoginUserInfo login(String username, String password) {
        // 1. 查询用户
        SysUser sysUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getDeleted, 0)
        );

        if (sysUser == null) {
            logger.warn("登录失败：用户 [{}] 不存在", username);
            return null;
        }

        // 2. 账号状态检查
        if (sysUser.getStatus() == null || sysUser.getStatus() != 1) {
            logger.warn("登录失败：用户 [{}] 已被禁用", username);
            return null;
        }

        // 3. 密码校验
        if (!passwordEncoder.matches(password, sysUser.getPassword())) {
            logger.warn("登录失败：用户 [{}] 密码错误", username);
            return null;
        }

        // 4. 构建 Org
        Org org = new Org(
                sysUser.getBankCode(),
                sysUser.getOrgNo(),
                sysUser.getOrgName()
        );

        // 5. 构建 User（loginNo = username，empNo = empNo）
        User user = new User(
                sysUser.getEmpNo() != null ? sysUser.getEmpNo() : username,
                username,
                sysUser.getRealName(),
                org
        );

        // 6. 构建 LoginUserInfo 并写入 Session（供 AssoUtil 等工具类使用）
        LoginUserInfo loginUserInfo = new LoginUserInfo(user, org);
        SessionUtils sessionUtils = new SessionUtils();
        sessionUtils.setLoginUserInfo(loginUserInfo);

        // 7. 设置 Spring Security 认证上下文（关键！）
        //    仅往 Session 写 LoginUserInfo 是不够的——Spring Security 的
        //    SecurityContextPersistenceFilter 只会从 Session 读取
        //    SPRING_SECURITY_CONTEXT；如果不设置 SecurityContextHolder，
        //    下一个请求会因为没有 Authentication 而被 401 拦截。
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);

        logger.info("用户 [{}] 登录成功", username);
        return loginUserInfo;
    }

    /**
     * 注销：清除 Session
     */
    public void logout() {
        SessionUtils sessionUtils = new SessionUtils();
        sessionUtils.clearLoginUserInfo();
        logger.info("用户已注销");
    }

    /**
     * 获取当前登录用户（从 Session 读取）
     */
    public LoginUserInfo getCurrentUser() {
        SessionUtils sessionUtils = new SessionUtils();
        return sessionUtils.getLoginUserInfo();
    }
}
