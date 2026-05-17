package com.bank.beijing.loanledger.core.service;

import com.bank.beijing.loanledger.core.dao.ILoanLedgerDao;
import com.bank.beijing.loanledger.core.iservice.ILoanLedgerService;
import com.bank.beijing.loanledger.core.pojo.LoanLedger;
import com.bank.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 借据台账 Service 实现
 * 覆盖需求文档 BR-F01-01 ~ BR-F01-11, BR-F02-01 ~ BR-F02-06 全部业务规则
 *
 * @author hermes-agent
 * @since 2026-05-17
 */
@Slf4j
@Service
public class LoanLedgerService extends ServiceImpl<ILoanLedgerDao, LoanLedger> implements ILoanLedgerService {

    // ==================== 查询 ====================

    @Override
    public IPage<LoanLedger> queryPage(IPage<LoanLedger> page, Map<String, Object> params,
                                        String userId, String roleType) {
        LambdaQueryWrapper<LoanLedger> wrapper = new LambdaQueryWrapper<>();

        // 借据号模糊查询
        String loanId = (String) params.get("loanId");
        if (loanId != null && !loanId.trim().isEmpty()) {
            wrapper.like(LoanLedger::getLoanId, loanId.trim());
        }
        // 公司名称模糊查询
        String companyName = (String) params.get("companyName");
        if (companyName != null && !companyName.trim().isEmpty()) {
            wrapper.like(LoanLedger::getCompanyName, companyName.trim());
        }
        // 台账状态
        Integer status = (Integer) params.get("status");
        if (status != null) {
            wrapper.eq(LoanLedger::getStatus, status);
        }
        // 放款日期范围
        LocalDate loanDateStart = (LocalDate) params.get("loanDateStart");
        if (loanDateStart != null) {
            wrapper.ge(LoanLedger::getLoanDate, loanDateStart);
        }
        LocalDate loanDateEnd = (LocalDate) params.get("loanDateEnd");
        if (loanDateEnd != null) {
            wrapper.le(LoanLedger::getLoanDate, loanDateEnd);
        }
        // 是否绿色信贷
        Integer isGreen = (Integer) params.get("isGreen");
        if (isGreen != null) {
            wrapper.eq(LoanLedger::getIsGreen, isGreen);
        }
        // 是否涉农
        Integer isAgriculture = (Integer) params.get("isAgriculture");
        if (isAgriculture != null) {
            wrapper.eq(LoanLedger::getIsAgriculture, isAgriculture);
        }
        // 提交人查询（复核岗使用）
        String submitBy = (String) params.get("submitBy");
        if (submitBy != null && !submitBy.trim().isEmpty()) {
            wrapper.like(LoanLedger::getSubmitBy, submitBy.trim());
        }
        // 提交时间范围（复核岗使用）
        LocalDateTime submitTimeStart = (LocalDateTime) params.get("submitTimeStart");
        if (submitTimeStart != null) {
            wrapper.ge(LoanLedger::getSubmitTime, submitTimeStart);
        }
        LocalDateTime submitTimeEnd = (LocalDateTime) params.get("submitTimeEnd");
        if (submitTimeEnd != null) {
            wrapper.le(LoanLedger::getSubmitTime, submitTimeEnd);
        }
        // 审批结论筛选（历史记录Tab）
        Integer isApproved = (Integer) params.get("isApproved");
        if (isApproved != null) {
            wrapper.eq(LoanLedger::getIsApproved, isApproved);
        }

        // 权限过滤：申请岗只能查看本人录入的记录 (BR-F01 per 3.1.5)
        if ("applicant".equals(roleType)) {
            wrapper.eq(LoanLedger::getCreatedBy, userId);
        }

        wrapper.orderByDesc(LoanLedger::getCreatedTime);
        return this.page(page, wrapper);
    }

    // ==================== 新增 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoanLedger addLoanLedger(LoanLedger loanLedger, String userId) {
        // 必填校验
        validateRequired(loanLedger);
        // 业务规则校验
        validateBusinessRules(loanLedger, null);
        // 借据号唯一性校验 (BR-F01-01)
        if (!isLoanIdUnique(loanLedger.getLoanId(), null)) {
            throw new BusinessException("借据号 " + loanLedger.getLoanId() + " 已存在，请确认后重新录入");
        }

        loanLedger.setStatus(0); // 草稿状态
        loanLedger.setCreatedBy(userId);
        loanLedger.setCreatedTime(LocalDateTime.now());
        loanLedger.setUpdatedBy(userId);
        loanLedger.setUpdatedTime(LocalDateTime.now());
        loanLedger.setIsDeleted(0);

        this.save(loanLedger);
        log.info("新增借据：loanId={}, userId={}", loanLedger.getLoanId(), userId);
        return loanLedger;
    }

    // ==================== 修改 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoanLedger updateLoanLedger(LoanLedger loanLedger, String userId) {
        LoanLedger existing = this.getById(loanLedger.getId());
        if (existing == null) {
            throw new BusinessException("借据记录不存在");
        }

        // 状态限制修改 (BR-F01-07)：仅草稿和已拒绝状态可修改
        if (existing.getStatus() != 0 && existing.getStatus() != 3) {
            throw new BusinessException("当前状态不允许修改");
        }

        // 必填校验
        validateRequired(loanLedger);
        // 业务规则校验
        validateBusinessRules(loanLedger, existing);
        // 借据号唯一性校验 (BR-F01-01)：排除自身
        if (!isLoanIdUnique(loanLedger.getLoanId(), loanLedger.getId())) {
            throw new BusinessException("借据号 " + loanLedger.getLoanId() + " 已存在，请确认后重新录入");
        }

        loanLedger.setStatus(existing.getStatus()); // 保持原有状态
        loanLedger.setUpdatedBy(userId);
        loanLedger.setUpdatedTime(LocalDateTime.now());
        // 保留创建信息
        loanLedger.setCreatedBy(existing.getCreatedBy());
        loanLedger.setCreatedTime(existing.getCreatedTime());

        this.updateById(loanLedger);
        log.info("修改借据：id={}, userId={}", loanLedger.getId(), userId);
        return loanLedger;
    }

    // ==================== 删除 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLoanLedger(Long id, String userId) {
        LoanLedger existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("借据记录不存在");
        }
        // 状态限制删除 (BR-F01-08)：仅草稿状态可删
        if (existing.getStatus() != 0) {
            throw new BusinessException("当前状态不允许删除");
        }
        this.removeById(id);
        log.info("删除借据：id={}, userId={}", id, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteLoanLedger(List<Long> ids, String userId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的记录");
        }
        int count = 0;
        for (Long id : ids) {
            LoanLedger existing = this.getById(id);
            if (existing != null && existing.getStatus() == 0) {
                this.removeById(id);
                count++;
            }
        }
        log.info("批量删除借据：count={}/{} , userId={}", count, ids.size(), userId);
        return count;
    }

    // ==================== 提交复核 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitForReview(Long id, String userId) {
        LoanLedger existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("借据记录不存在");
        }

        // 已通过状态不可提交 (BR-F01-11)
        if (existing.getStatus() == 2) {
            throw new BusinessException("已通过的记录不可再次提交复核");
        }
        // 已是待复核状态防重复提交
        if (existing.getStatus() == 1) {
            throw new BusinessException("该记录已在复核中，请勿重复提交");
        }
        // 仅草稿和已拒绝可提交
        if (existing.getStatus() != 0 && existing.getStatus() != 3) {
            throw new BusinessException("当前状态不允许提交复核");
        }

        // 提交前再次校验必填字段
        validateRequired(existing);

        existing.setStatus(1); // 待复核
        existing.setSubmitBy(userId);
        existing.setSubmitTime(LocalDateTime.now());
        existing.setUpdatedBy(userId);
        existing.setUpdatedTime(LocalDateTime.now());

        this.updateById(existing);
        log.info("提交复核：id={}, userId={}", id, userId);
    }

    // ==================== 批量导入 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchImport(List<LoanLedger> list, String userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<Map<String, Object>> failDetails = new ArrayList<>();

        // 单次导入行数限制 (BR-F01-09)
        if (list.size() > 1000) {
            throw new BusinessException("单次批量导入最多支持 1000 条，当前 " + list.size() + " 条，请拆分后分批导入");
        }

        for (int i = 0; i < list.size(); i++) {
            LoanLedger item = list.get(i);
            int rowNum = i + 2; // 第1行是表头，数据从第2行开始
            try {
                // 逐行校验 (BR-F01-10)
                validateRequired(item);
                validateBusinessRules(item, null);
                // 借据号唯一性
                if (!isLoanIdUnique(item.getLoanId(), null)) {
                    throw new BusinessException("借据号 " + item.getLoanId() + " 已存在");
                }

                item.setStatus(0);
                item.setCreatedBy(userId);
                item.setCreatedTime(LocalDateTime.now());
                item.setUpdatedBy(userId);
                item.setUpdatedTime(LocalDateTime.now());
                item.setIsDeleted(0);
                this.save(item);
                successCount++;
            } catch (Exception e) {
                failCount++;
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("row", rowNum);
                detail.put("reason", e.getMessage());
                failDetails.add(detail);
            }
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failDetails", failDetails);
        result.put("totalCount", list.size());

        log.info("批量导入借据：total={}, success={}, fail={}, userId={}",
                list.size(), successCount, failCount, userId);
        return result;
    }

    // ==================== 审批 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, String reviewBy) {
        LoanLedger existing = validateReviewEligibility(id, reviewBy);

        existing.setStatus(2); // 已通过
        existing.setIsApproved(1);
        existing.setReviewBy(reviewBy);
        existing.setReviewTime(LocalDateTime.now());
        existing.setRejectReason(null);
        existing.setUpdatedBy(reviewBy);
        existing.setUpdatedTime(LocalDateTime.now());

        this.updateById(existing);
        log.info("审批通过：id={}, reviewBy={}", id, reviewBy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchApprove(List<Long> ids, String reviewBy) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要审批的记录");
        }

        // 校验是否含非待复核状态 (BR-F02 异常处理3)
        for (Long id : ids) {
            LoanLedger existing = this.getById(id);
            if (existing == null) {
                throw new BusinessException("借据记录不存在：" + id);
            }
            if (existing.getStatus() != 1) {
                throw new BusinessException("所选记录中含有非待复核状态的记录，请重新选择");
            }
            // 不可审批自己提交的 (BR-F02-04)
            if (reviewBy.equals(existing.getSubmitBy())) {
                throw new BusinessException("不允许审批自己提交的记录（借据号：" + existing.getLoanId() + "）");
            }
        }

        int count = 0;
        for (Long id : ids) {
            LoanLedger existing = this.getById(id);
            if (existing != null && existing.getStatus() == 1) {
                existing.setStatus(2);
                existing.setIsApproved(1);
                existing.setReviewBy(reviewBy);
                existing.setReviewTime(LocalDateTime.now());
                existing.setRejectReason(null);
                existing.setUpdatedBy(reviewBy);
                existing.setUpdatedTime(LocalDateTime.now());
                this.updateById(existing);
                count++;
            }
        }

        log.info("批量审批通过：count={}, reviewBy={}", count, reviewBy);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long id, String reviewBy, String rejectReason) {
        LoanLedger existing = validateReviewEligibility(id, reviewBy);

        // 拒绝原因必填 (BR-F02-02)
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            throw new BusinessException("请填写拒绝原因后再提交");
        }
        if (rejectReason.trim().length() < 5) {
            throw new BusinessException("拒绝原因不能少于5个字");
        }

        existing.setStatus(3); // 已拒绝
        existing.setIsApproved(0);
        existing.setReviewBy(reviewBy);
        existing.setReviewTime(LocalDateTime.now());
        existing.setRejectReason(rejectReason.trim());
        existing.setUpdatedBy(reviewBy);
        existing.setUpdatedTime(LocalDateTime.now());

        this.updateById(existing);
        log.info("审批拒绝：id={}, reviewBy={}, reason={}", id, reviewBy, rejectReason);
    }

    @Override
    public boolean isLoanIdUnique(String loanId, Long excludeId) {
        LambdaQueryWrapper<LoanLedger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoanLedger::getLoanId, loanId);
        if (excludeId != null) {
            wrapper.ne(LoanLedger::getId, excludeId);
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public List<LoanLedger> exportList(Map<String, Object> params, String userId, String roleType) {
        LambdaQueryWrapper<LoanLedger> wrapper = new LambdaQueryWrapper<>();

        String loanId = (String) params.get("loanId");
        if (loanId != null && !loanId.trim().isEmpty()) {
            wrapper.like(LoanLedger::getLoanId, loanId.trim());
        }
        String companyName = (String) params.get("companyName");
        if (companyName != null && !companyName.trim().isEmpty()) {
            wrapper.like(LoanLedger::getCompanyName, companyName.trim());
        }
        Integer status = (Integer) params.get("status");
        if (status != null) {
            wrapper.eq(LoanLedger::getStatus, status);
        }

        if ("applicant".equals(roleType)) {
            wrapper.eq(LoanLedger::getCreatedBy, userId);
        }

        wrapper.orderByDesc(LoanLedger::getCreatedTime);
        return this.list(wrapper);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 校验审批资格
     */
    private LoanLedger validateReviewEligibility(Long id, String reviewBy) {
        LoanLedger existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("借据记录不存在");
        }
        // 仅待复核状态可审批
        if (existing.getStatus() != 1) {
            throw new BusinessException("该记录不是待复核状态，无法审批");
        }
        // 不可审批自己提交的 (BR-F02-04)
        if (reviewBy.equals(existing.getSubmitBy())) {
            throw new BusinessException("不允许审批自己提交的记录");
        }
        return existing;
    }

    /**
     * 必填字段校验
     */
    private void validateRequired(LoanLedger loanLedger) {
        if (loanLedger.getLoanId() == null || loanLedger.getLoanId().trim().isEmpty()) {
            throw new BusinessException("借据号不能为空");
        }
        if (loanLedger.getCompanyName() == null || loanLedger.getCompanyName().trim().isEmpty()) {
            throw new BusinessException("公司名称不能为空");
        }
        if (loanLedger.getCreditCode() == null || loanLedger.getCreditCode().trim().isEmpty()) {
            throw new BusinessException("企业统一信用代码不能为空");
        }
        if (loanLedger.getLoanAmount() == null) {
            throw new BusinessException("借据金额不能为空");
        }
        if (loanLedger.getInterestRate() == null) {
            throw new BusinessException("利率不能为空");
        }
        if (loanLedger.getLoanDate() == null) {
            throw new BusinessException("放款日期不能为空");
        }
        if (loanLedger.getDueDate() == null) {
            throw new BusinessException("到期日期不能为空");
        }
        // 担保机构条件必填 (BR-F01-03)
        if (loanLedger.getIsGuarantee() != null && loanLedger.getIsGuarantee() == 1) {
            if (loanLedger.getGuaranteeOrg() == null || loanLedger.getGuaranteeOrg().trim().isEmpty()) {
                throw new BusinessException("选择\"有担保\"时，担保机构不能为空");
            }
        }
    }

    /**
     * 业务规则校验
     */
    private void validateBusinessRules(LoanLedger loanLedger, LoanLedger existing) {
        // 信用代码格式：18位字母数字 (BR-F01-02)
        String creditCode = loanLedger.getCreditCode();
        if (creditCode != null && !creditCode.trim().isEmpty()) {
            if (!creditCode.trim().matches("^[A-Za-z0-9]{18}$")) {
                throw new BusinessException("企业统一信用代码格式不正确");
            }
        }

        // 到期日期必须晚于放款日期 (BR-F01-04)
        LocalDate loanDate = loanLedger.getLoanDate();
        LocalDate dueDate = loanLedger.getDueDate();
        if (loanDate != null && dueDate != null) {
            if (!dueDate.isAfter(loanDate)) {
                throw new BusinessException("到期日期不能早于放款日期");
            }
        }

        // 利率范围：0.0001% ~ 100% (BR-F01-05)
        BigDecimal rate = loanLedger.getInterestRate();
        if (rate != null) {
            if (rate.compareTo(new BigDecimal("0.0001")) < 0 || rate.compareTo(new BigDecimal("100")) > 0) {
                throw new BusinessException("利率取值范围为 0.0001%～100%");
            }
        }

        // 借据金额最大值 (BR-F01-06)
        BigDecimal amount = loanLedger.getLoanAmount();
        if (amount != null) {
            if (amount.compareTo(new BigDecimal("9999999999.99")) > 0) {
                throw new BusinessException("借据金额不能超过 9,999,999,999.99 元");
            }
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("借据金额不能为负数");
            }
        }
    }
}
