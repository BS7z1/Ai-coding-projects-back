package com.bank.beijing.template.core.iservice;

import com.bank.beijing.template.core.pojo.PKTemplateCompPk;
import com.bank.beijing.template.core.pojo.TemplateCompPk;
import com.bank.common.pages.Pager;
import java.util.List;

public interface ITemplateCompPkService {
    public int listTemplateCompPkCount(TemplateCompPk templateCompPk);
    public List<TemplateCompPk> listTemplateCompPk(TemplateCompPk templateCompPk, Pager pager);
    public TemplateCompPk saveTemplateCompPk(TemplateCompPk templateCompPk);
    public TemplateCompPk getTemplateCompPkByPk(PKTemplateCompPk pk);
    public void deleteTemplateCompPkByPks(PKTemplateCompPk[] pks);
}
