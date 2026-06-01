package com.bank.common.util;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录用户信息容器（存放于 HttpSession）
 * <p>
 * AssoUtil 使用链：
 *   AssoUtil.getLoginUserInfo()  → 本对象
 *   AssoUtil.getUser()           → this.user
 *   AssoUtil.getDicd()           → this.org.bankCode
 *   AssoUtil.getEmpNo()          → this.user.empNo
 *   AssoUtil.getLoginNo()        → this.user.loginNo
 *   AssoUtil.getOrgNo()          → this.user.org.orgNo
 */
public class LoginUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Session 中存储本对象时使用的 key */
    public static final String SESSION_KEY = "LOGIN_USER_INFO";

    /** 用户信息（empNo / loginNo / realName / org） */
    private User user;

    /**
     * 顶层机构信息（对应 AssoUtil.getDicd() 走的路径：loginUserInfo.getOrg()）
     * 通常与 user.getOrg() 指向同一机构，单独保留是为了兼容 AssoUtil 的两种取法
     */
    private Org org;

    /** 登录时间 */
    private LocalDateTime loginTime;

    public LoginUserInfo() {}

    public LoginUserInfo(User user, Org org) {
        this.user = user;
        this.org = org;
        this.loginTime = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    @Override
    public String toString() {
        return "LoginUserInfo{user=" + user + ", org=" + org + ", loginTime=" + loginTime + "}";
    }
}
