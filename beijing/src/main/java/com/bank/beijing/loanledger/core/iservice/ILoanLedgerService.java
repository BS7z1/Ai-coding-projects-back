package com.bank.beijing.loanledger.core.iservice;

import com.bank.beijing.loanledger.core.pojo.LoanLedger;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 借据台账 Service 接口
 *
 * @author hermes-agent
 * @since 2026-05-17
 */
public interface ILoanLedgerService extends IService<LoanLedger> {

    /**
     * 分页查询借据列表（申请岗：仅查看本人录入的）
     *
     * @param page     分页参数
     * @param params   查询条件 Map
     * @param userId   当前用户工号
     * @param roleType 角色类型：applicant/reviewer
     * @return 分页结果
     */
    IPage<LoanLedger> queryPage(IPage<LoanLedger> page, Map<String, Object> params, String userId, String roleType);

    /**
     * 新增借据（保存为草稿）
     *
     * @param loanLedger 借据信息
     * @param userId     操作人员工号
     * @return 新增后的借据
     */
    LoanLedger addLoanLedger(LoanLedger loanLedger, String userId);

    /**
     * 修改借据（仅草稿/已拒绝状态可修改）
     *
     * @param loanLedger 借据信息
     * @param userId     操作人员工号
     * @return 修改后的借据
     */
    LoanLedger updateLoanLedger(LoanLedger loanLedger, String userId);

    /**
     * 删除借据（仅草稿状态可删除，软删除）
     *
     * @param id     借据主键
     * @param userId 操作人员工号
     */
    void deleteLoanLedger(Long id, String userId);

    /**
     * 批量删除借据（仅草稿状态可删）
     *
     * @param ids    借据主键列表
     * @param userId 操作人员工号
     * @return 成功删除数量
     */
    int batchDeleteLoanLedger(List<Long> ids, String userId);

    /**
     * 提交复核（状态从草稿/已拒绝→待复核）
     *
     * @param id     借据主键
     * @param userId 操作人员工号
     */
    void submitForReview(Long id, String userId);

    /**
     * 批量导入借据
     *
     * @param list   借据列表
     * @param userId 操作人员工号
     * @return 导入结果 Map，含 successCount / failCount / failDetails
     */
    Map<String, Object> batchImport(List<LoanLedger> list, String userId);

    /**
     * 审批通过
     *
     * @param id       借据主键
     * @param reviewBy 复核人员工号
     */
    void approve(Long id, String reviewBy);

    /**
     * 批量审批通过
     *
     * @param ids      借据主键列表
     * @param reviewBy 复核人员工号
     * @return 成功审批数量
     */
    int batchApprove(List<Long> ids, String reviewBy);

    /**
     * 审批拒绝
     *
     * @param id           借据主键
     * @param reviewBy     复核人员工号
     * @param rejectReason 拒绝原因
     */
    void reject(Long id, String reviewBy, String rejectReason);

    /**
     * 校验借据号唯一性
     *
     * @param loanId      借据号
     * @param excludeId   排除的借据ID（修改时排除自身），可为 null
     * @return true=唯一，false=重复
     */
    boolean isLoanIdUnique(String loanId, Long excludeId);

    /**
     * 导出查询结果
     *
     * @param params 查询条件
     * @param userId 当前用户工号
     * @param roleType 角色类型
     * @return 借据列表
     */
    List<LoanLedger> exportList(Map<String, Object> params, String userId, String roleType);
}
