package com.bank.common.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class AssoUtil {
    private final static Logger logger = LoggerFactory.getLogger(AssoUtil.class);

    public static LoginUserInfo getLoginUserInfo(){
        SessionUtils sessionUtils = new SessionUtils();
        LoginUserInfo loginUserInfo = sessionUtils.getLoginUserInfo();
        return loginUserInfo;
    }

    public static String getDicd(){
        LoginUserInfo loginUserInfo = getLoginUserInfo();
        if(loginUserInfo == null){
            logger.error("loginUserInfo is null");
            return null;
        }
        Org org = loginUserInfo.getOrg();
        if(org==null){
            logger.error("org is null");
            return null;
        }
        String bankCode = org.getBankCode();
        return bankCode;
    }

    public static String getEmpNo(){
        User user = getUser();
        if(user == null){
            logger.error("user is null");
            return null;
        }
        String empNo = user.getEmpNo();
        return empNo;
    }

    public static User getUser(){
        LoginUserInfo loginUserInfo = getLoginUserInfo();
        if(loginUserInfo == null){
            logger.error("loginUserInfo is null");
            return null;
        }
        User user = loginUserInfo.getUser();
        return user;
    }

    public static String getLoginNo(){
        User user = getUser();
        if(user == null){
            logger.error("获取当前登录用户信息失败");
            return null;
        }
        String loginNo = user.getLoginNo();
        if(StringUtils.isBlank(loginNo)){
            logger.error("获取当前用户名失败");
        }
        return loginNo;
    }

    public static String getOrgNo(){
        User user = getUser();
        if(user == null){
            logger.error("user is null");
            return null;
        }
        Org org = user.getOrg();
        if(org==null){
            logger.error("org is null");
            return null;
        }
        String orgNo = org.getOrgNo();
        logger.info("用户机构号: "+orgNo);
        return orgNo;
    }

    public static HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null){
            return null;
        }else{
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return request;
        }
    }
}
