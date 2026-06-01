package com.bank.common.auth.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 * 对应数据库表 t_sys_user
 */
@Data
@TableName("t_sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录名（用户名），对应 User.loginNo */
    private String username;

    /** 密码（BCrypt 加密） */
    private String password;

    /** 员工号，对应 User.empNo */
    private String empNo;

    /** 真实姓名 */
    private String realName;

    /** 银行机构代码，对应 Org.bankCode */
    private String bankCode;

    /** 机构号，对应 Org.orgNo */
    private String orgNo;

    /** 机构名称，对应 Org.orgName */
    private String orgName;

    /** 账号状态：0=禁用，1=启用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 软删除标志：0=正常，1=已删除 */
    @TableLogic
    private Integer deleted;
}
