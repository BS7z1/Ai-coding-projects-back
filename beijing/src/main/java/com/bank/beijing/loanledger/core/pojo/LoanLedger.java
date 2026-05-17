package com.bank.beijing.loanledger.core.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 借据台账实体类
 * 对应数据库表 t_loan_ledger
 *
 * @author hermes-agent
 * @since 2026-05-17
 */
@Data
@TableName("t_loan_ledger")
public class LoanLedger implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 借据号，全局唯一 */
    private String loanId;

    /** 公司名称 */
    private String companyName;

    /** 企业统一信用代码，18位 */
    private String creditCode;

    /** 借据金额（元），精度(15,2) */
    private BigDecimal loanAmount;

    /** 年利率（%），精度(7,4)，范围 0.0001~100 */
    private BigDecimal interestRate;

    /** 放款日期 */
    private LocalDate loanDate;

    /** 到期日期，必须晚于放款日期 */
    private LocalDate dueDate;

    /** 是否绿色信贷：0=否，1=是 */
    private Integer isGreen;

    /** 是否涉农：0=否，1=是 */
    private Integer isAgriculture;

    /** 是否有担保：0=否，1=是 */
    private Integer isGuarantee;

    /** 担保机构名称，isGuarantee=1 时必填 */
    private String guaranteeOrg;

    /** 台账状态：0=草稿，1=待复核，2=已通过，3=已拒绝 */
    private Integer status;

    /** 提交人员工号 */
    private String submitBy;

    /** 提交时间 */
    private LocalDateTime submitTime;

    /** 复核人员工号 */
    private String reviewBy;

    /** 复核时间 */
    private LocalDateTime reviewTime;

    /** 审批结论：NULL=未审批，1=通过，0=拒绝 */
    private Integer isApproved;

    /** 拒绝原因，isApproved=0 时必填，≥5字 */
    private String rejectReason;

    /** 备注 */
    private String remark;

    /** 创建人员工号 */
    private String createdBy;

    /** 创建时间 */
    private LocalDateTime createdTime;

    /** 更新人员工号 */
    private String updatedBy;

    /** 更新时间 */
    private LocalDateTime updatedTime;

    /** 软删除标志：0=正常，1=已删除 */
    @TableLogic
    private Integer isDeleted;
}
