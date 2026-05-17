package com.bank.beijing.loanledger.core.dao;

import com.bank.beijing.loanledger.core.pojo.LoanLedger;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 借据台账 DAO 接口
 *
 * @author hermes-agent
 * @since 2026-05-17
 */
@Mapper
public interface ILoanLedgerDao extends BaseMapper<LoanLedger> {

    /**
     * 根据借据号查询借据（含已删除）
     */
    @Select("SELECT * FROM t_loan_ledger WHERE loan_id = #{loanId}")
    LoanLedger findByLoanIdIncludeDeleted(@Param("loanId") String loanId);

    /**
     * 根据借据号查询未删除的借据
     */
    @Select("SELECT * FROM t_loan_ledger WHERE loan_id = #{loanId} AND is_deleted = 0")
    LoanLedger findByLoanId(@Param("loanId") String loanId);
}
