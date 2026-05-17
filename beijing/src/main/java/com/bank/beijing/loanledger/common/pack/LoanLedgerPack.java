package com.bank.beijing.loanledger.common.pack;

import com.bank.beijing.loanledger.core.pojo.LoanLedger;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 借据台账数据包
 *
 * @author hermes-agent
 * @since 2026-05-17
 */
@Data
public class LoanLedgerPack implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 操作类型：query/add/update/delete/batchDelete/submit/batchImport/export/approve/batchApprove/reject */
    private String action;

    /** 借据数据列表 */
    private List<LoanLedger> dataList;

    /** 查询条件 */
    private Map<String, Object> queryParams;

    /** 操作人员工号 */
    private String userId;

    /** 角色类型：applicant / reviewer */
    private String roleType;

    /** 分页参数 - 当前页 */
    private Integer pageNum;

    /** 分页参数 - 每页条数 */
    private Integer pageSize;

    /** 拒绝原因（审批拒绝时使用） */
    private String rejectReason;

    /** 批量操作的ID列表 */
    private List<Long> ids;
}
