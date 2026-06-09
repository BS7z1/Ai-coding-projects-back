package com.bank.beijing.template.core.service;

import com.bank.beijing.template.common.pack.TemplateCompPkPack;
import com.bank.beijing.template.core.dao.ITemplateCompPkDao;
import com.bank.beijing.template.core.iservice.ITemplateCompPkService;
import com.bank.beijing.template.core.pojo.PKTemplateCompPk;
import com.bank.beijing.template.core.pojo.TemplateCompPk;
import com.bank.common.pages.Pager;
import com.bank.core.iservice.AbstractMysqlBUSIService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class TemplateCompPkService extends AbstractMysqlBUSIService implements ITemplateCompPkService {

    @Resource
    private ITemplateCompPkDao templateCompPkDao;

    public int listTemplateCompPkCount(TemplateCompPk templateCompPk) {
        int count = templateCompPkDao.findByPrepareHqlWhereCount(
                TemplateCompPkPack.packTemplateCompPkQuery(templateCompPk),
                TemplateCompPkPack.packTemplateCompPkQueryParams(templateCompPk)
        );
        return count;
    }

    public List<TemplateCompPk> listTemplateCompPk(TemplateCompPk templateCompPk, Pager pager) {
        List<TemplateCompPk> list = templateCompPkDao.findByPrepareHqlWherePage(
                TemplateCompPkPack.packTemplateCompPkQuery(templateCompPk),
                TemplateCompPkPack.packTemplateCompPkQueryParams(templateCompPk),
                pager
        );
        return list;
    }

    public TemplateCompPk saveTemplateCompPk(TemplateCompPk templateCompPk) {
        TemplateCompPk temp = (TemplateCompPk) templateCompPkDao.save(templateCompPk);
        return temp;
    }

    public TemplateCompPk getTemplateCompPkByPk(PKTemplateCompPk pk) {
        TemplateCompPk templateCompPk = (TemplateCompPk)templateCompPkDao.getByPK(pk);
        return templateCompPk;
    }

    public void deleteTemplateCompPkByPks(PKTemplateCompPk[] pks) {
        for(PKTemplateCompPk pk: pks){
            TemplateCompPk templateCompPk = templateCompPkDao.getByPK(pk);
            templateCompPkDao.remove(templateCompPk);
        }
    }
}
