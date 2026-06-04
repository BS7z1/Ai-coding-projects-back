package com.bank.beijing.loanledger.web.controller.dwr;

import com.bank.beijing.loanledger.common.pack.LoanLedgerPack;
import com.bank.beijing.loanledger.core.iservice.ILoanLedgerService;
import com.bank.beijing.loanledger.core.pojo.LoanLedger;
import com.bank.common.exception.BusinessException;
import com.bank.common.module.ResultBean;
import com.bank.common.result.Result;
import com.bank.common.util.WebUtilWork;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 借据台账管理 Controller
 *
 * @author hermes-agent
 * @since 2026-05-17
 */
@Slf4j
@Service("dwrLoanLedgerService")
public class DwrLoanLedgerService {

    @Autowired
    private ILoanLedgerService loanLedgerService;

    // ==================== 分页查询 ====================

    /**
     * 借据列表分页查询
     */

    public ResultBean queryPage(@RequestBody LoanLedgerPack pack) {
        if (pack.getQueryParams() == null) {
            pack.setQueryParams(new java.util.HashMap<>());
        }
        int pageNum = pack.getPageNum() != null ? pack.getPageNum() : 1;
        int pageSize = pack.getPageSize() != null ? pack.getPageSize() : 20;
        String userId = pack.getUserId() != null ? pack.getUserId() : "system";
        String roleType = pack.getRoleType() != null ? pack.getRoleType() : "applicant";

        IPage<LoanLedger> page = new Page<>(pageNum, pageSize);
        IPage<LoanLedger> result = loanLedgerService.queryPage(page, pack.getQueryParams(), userId, roleType);
        return WebUtilWork.WebResultPack(true, "查询成功", result);
    }

    // ==================== 新增 ====================

    /**
     * 新增借据
     */

    public ResultBean add(@RequestBody LoanLedgerPack pack) {
        if (pack.getDataList() == null || pack.getDataList().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "借据数据不能为空", null);
        }
        String userId = pack.getUserId() != null ? pack.getUserId() : "system";
        LoanLedger result = loanLedgerService.addLoanLedger(pack.getDataList().get(0), userId);
        return WebUtilWork.WebResultPack(true, "新增成功", result);
    }

    // ==================== 修改 ====================

    /**
     * 修改借据
     */

    public ResultBean update(@RequestBody LoanLedgerPack pack) {
        if (pack.getDataList() == null || pack.getDataList().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "借据数据不能为空", null);
        }
        String userId = pack.getUserId() != null ? pack.getUserId() : "system";
        LoanLedger result = loanLedgerService.updateLoanLedger(pack.getDataList().get(0), userId);
        return WebUtilWork.WebResultPack(true, "修改成功", result);
    }

    // ==================== 删除 ====================

    /**
     * 删除借据
     */

    public ResultBean delete(@RequestBody LoanLedgerPack pack) {
        if (pack.getIds() == null || pack.getIds().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "请选择要删除的记录", null);
        }
        String userId = pack.getUserId() != null ? pack.getUserId() : "system";
        loanLedgerService.deleteLoanLedger(pack.getIds().get(0), userId);
        return WebUtilWork.WebResultPack(true, "删除成功", null);
    }

    /**
     * 批量删除借据
     */

    public ResultBean batchDelete(@RequestBody LoanLedgerPack pack) {
        if (pack.getIds() == null || pack.getIds().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "请选择要删除的记录", null);
        }
        String userId = pack.getUserId() != null ? pack.getUserId() : "system";
        int count = loanLedgerService.batchDeleteLoanLedger(pack.getIds(), userId);
        return WebUtilWork.WebResultPack(true, "成功删除 " + count + " 条记录", null);
    }

    // ==================== 提交复核 ====================

    /**
     * 提交复核
     */

    public ResultBean submit(@RequestBody LoanLedgerPack pack) {
        if (pack.getIds() == null || pack.getIds().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "请选择要提交的记录", null);
        }
        String userId = pack.getUserId() != null ? pack.getUserId() : "system";
        loanLedgerService.submitForReview(pack.getIds().get(0), userId);
        return WebUtilWork.WebResultPack(true, "提交复核成功", null);
    }

    // ==================== 批量导入 ====================

    /**
     * 批量导入借据
     */

    public ResultBean batchImport(@RequestBody LoanLedgerPack pack) {
        if (pack.getDataList() == null || pack.getDataList().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "导入数据不能为空", null);
        }
        String userId = pack.getUserId() != null ? pack.getUserId() : "system";
        Map<String, Object> result = loanLedgerService.batchImport(pack.getDataList(), userId);
        return WebUtilWork.WebResultPack(true, "批量导入完成", result);
    }

    // ==================== 导出 ====================

    /**
     * 导出借据列表
     */

    public ResultBean exportList(@RequestBody LoanLedgerPack pack) {
        if (pack.getQueryParams() == null) {
            pack.setQueryParams(new java.util.HashMap<>());
        }
        String userId = pack.getUserId() != null ? pack.getUserId() : "system";
        String roleType = pack.getRoleType() != null ? pack.getRoleType() : "applicant";
        List<LoanLedger> list = loanLedgerService.exportList(pack.getQueryParams(), userId, roleType);
        return WebUtilWork.WebResultPack(list);
    }

    // ==================== 审批 ====================

    /**
     * 审批通过
     */

    public ResultBean approve(@RequestBody LoanLedgerPack pack) {
        if (pack.getIds() == null || pack.getIds().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "请选择要审批的记录", null);
        }
        String reviewBy = pack.getUserId() != null ? pack.getUserId() : "system";
        loanLedgerService.approve(pack.getIds().get(0), reviewBy);
        return WebUtilWork.WebResultPack(true, "审批通过", null);
    }

    /**
     * 批量审批通过
     */

    public ResultBean batchApprove(@RequestBody LoanLedgerPack pack) {
        if (pack.getIds() == null || pack.getIds().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "请选择要审批的记录", null);
        }
        String reviewBy = pack.getUserId() != null ? pack.getUserId() : "system";
        int count = loanLedgerService.batchApprove(pack.getIds(), reviewBy);
        return WebUtilWork.WebResultPack(true, "成功审批通过 " + count + " 条记录", null);
    }

    /**
     * 审批拒绝
     */

    public ResultBean reject(@RequestBody LoanLedgerPack pack) {
        if (pack.getIds() == null || pack.getIds().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "请选择要审批的记录", null);
        }
        String reviewBy = pack.getUserId() != null ? pack.getUserId() : "system";
        String rejectReason = pack.getRejectReason();
        loanLedgerService.reject(pack.getIds().get(0), reviewBy, rejectReason);
        return WebUtilWork.WebResultPack(true, "审批已拒绝", null);
    }

    // ==================== 借据号唯一性校验 ====================

    /**
     * 校验借据号唯一性
     */

    public ResultBean checkLoanId(@RequestBody LoanLedgerPack pack) {
        if (pack.getDataList() == null || pack.getDataList().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "借据数据不能为空", null);
        }
        String loanId = pack.getDataList().get(0).getLoanId();
        Long excludeId = pack.getDataList().get(0).getId();
        boolean unique = loanLedgerService.isLoanIdUnique(loanId, excludeId);
        return WebUtilWork.WebResultPack(true, "查询成功", unique);
    }

    /**
     * 查询单条借据详情
     */

    public ResultBean detail(@RequestBody LoanLedgerPack pack) {
        if (pack.getIds() == null || pack.getIds().isEmpty()) {
            return WebUtilWork.WebResultPack(false, "请选择要查看的记录", null);
        }
        LoanLedger loanLedger = loanLedgerService.getById(pack.getIds().get(0));
        if (loanLedger == null) {
            return WebUtilWork.WebResultPack(false, "借据记录不存在", null);
        }
        return WebUtilWork.WebResultPack(true, "查询成功", loanLedger);
    }
}
