package com.bank.beijing.template.core.iservice;

import com.bank.beijing.template.core.pojo.TemplateSinglePk;
import com.bank.common.pages.Pager;
import java.util.List;

public interface ITemplateSinglePkService {
    public int listTemplateSinglePkCount(TemplateSinglePk templateSinglePk);
    public List<TemplateSinglePk> listTemplateSinglePk(TemplateSinglePk templateSinglePk, Pager pager);
    public TemplateSinglePk saveTemplateSinglePk(TemplateSinglePk templateSinglePk);
    public TemplateSinglePk getTemplateSinglePk(String pk);
    public void deleteTemplateSinglePk(String[] pks);
}
