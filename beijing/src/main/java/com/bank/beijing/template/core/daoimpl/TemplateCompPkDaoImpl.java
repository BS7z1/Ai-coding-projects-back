package com.bank.beijing.template.core.daoimpl;

import com.bank.beijing.template.core.dao.ITemplateCompPkDao;
import com.bank.beijing.template.core.pojo.PKTemplateCompPk;
import com.bank.beijing.template.core.pojo.TemplateCompPk;
import com.bank.core.daoimpl.BaseHapiDaoimpl;
import org.springframework.stereotype.Repository;

@Repository
public class TemplateCompPkDaoImpl extends BaseHapiDaoimpl<TemplateCompPk, PKTemplateCompPk> implements ITemplateCompPkDao {
    public TemplateCompPkDaoImpl() { super(TemplateCompPk.class);}
}
