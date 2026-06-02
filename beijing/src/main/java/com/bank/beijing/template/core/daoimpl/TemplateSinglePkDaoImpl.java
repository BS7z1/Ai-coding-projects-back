package com.bank.beijing.template.core.daoimpl;

import com.bank.beijing.template.core.dao.*;
import com.bank.beijing.template.core.pojo.*;
import com.bank.core.daoimpl.BaseHapiDaoimpl;
import org.springframework.stereotype.Repository;

@Repository
public class TemplateSinglePkDaoImpl extends BaseHapiDaoimpl<TemplateSinglePk, java.lang.String> implements ITemplateSinglePkDao {
    public TemplateSinglePkDaoImpl() { super(TemplateSinglePk.class);}
}
