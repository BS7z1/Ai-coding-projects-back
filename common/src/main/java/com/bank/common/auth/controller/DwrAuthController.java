package com.bank.common.auth.controller;

import com.bank.common.auth.service.AuthService;
import com.bank.common.result.Result;
import com.bank.common.util.LoginUserInfo;
import com.bank.common.util.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口 Controller
 * <p>
 * 接口列表：
 *   POST /api/login.do          — 登录（form 表单）
 *   GET  /api/sys/sessionUser   — 获取当前登录用户
 *   GET  /api/logout            — 注销
 */
@RestController
public class DwrAuthController {

    private static final Logger logger = LoggerFactory.getLogger(DwrAuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * 登录接口
     * 前端 api/app.js 中的 login() 以 application/x-www-form-urlencoded + params 发送
     *
     * @param username 用户名
     * @param password 密码
     */
    @PostMapping("/api/login.do")
    public Result<?> login(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password) {

        if (username == null || username.trim().isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.error(400, "密码不能为空");
        }

        LoginUserInfo loginUserInfo = authService.login(username.trim(), password);

        if (loginUserInfo == null) {
            return Result.error(401, "用户名或密码错误");
        }

        // 返回用户基本信息给前端
        Map<String, Object> data = new HashMap<>();
        User user = loginUserInfo.getUser();
        if (user != null) {
            data.put("username", user.getLoginNo());
            data.put("realName", user.getRealName());
            data.put("empNo", user.getEmpNo());
        }
        if (loginUserInfo.getOrg() != null) {
            data.put("orgName", loginUserInfo.getOrg().getOrgName());
            data.put("orgNo", loginUserInfo.getOrg().getOrgNo());
        }

        return Result.success("登录成功", data);
    }

    /**
     * 获取当前登录用户信息
     * 前端 api/app.js 中的 getSessionUser() 调用
     */
    @GetMapping("/api/sys/sessionUser")
    public Result<?> getSessionUser() {
        LoginUserInfo loginUserInfo = authService.getCurrentUser();
        if (loginUserInfo == null) {
            return Result.error(401, "未登录或登录已过期");
        }

        Map<String, Object> data = new HashMap<>();
        User user = loginUserInfo.getUser();
        if (user != null) {
            data.put("username", user.getLoginNo());
            data.put("realName", user.getRealName());
            data.put("empNo", user.getEmpNo());
        }
        if (loginUserInfo.getOrg() != null) {
            data.put("orgName", loginUserInfo.getOrg().getOrgName());
            data.put("orgNo", loginUserInfo.getOrg().getOrgNo());
        }
        data.put("loginTime", loginUserInfo.getLoginTime());

        return Result.success(data);
    }

    /**
     * 注销
     */
    @GetMapping("/api/logout")
    public Result<?> logout() {
        authService.logout();
        return Result.success("已退出登录");
    }
}
