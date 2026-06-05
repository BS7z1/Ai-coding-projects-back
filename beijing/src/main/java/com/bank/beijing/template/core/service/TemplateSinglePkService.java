package com.bank.beijing.template.core.service;

import com.bank.beijing.template.common.pack.TemplateSinglePkPack;
import com.bank.beijing.template.core.dao.ITemplateSinglePkDao;
import com.bank.beijing.template.core.iservice.ITemplateSinglePkService;
import com.bank.beijing.template.core.pojo.TemplateSinglePk;
import com.bank.common.pages.Pager;
import com.bank.core.iservice.AbstractMysqlBUSIService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class TemplateSinglePkService extends AbstractMysqlBUSIService implements ITemplateSinglePkService {

    @Resource
    private ITemplateSinglePkDao templateSinglePkDao;

    public int listTemplateSinglePkCount(TemplateSinglePk templateSinglePk) {
        int count = templateSinglePkDao.findByPrepareHqlWhereCount(
                TemplateSinglePkPack.packTemplateSinglePkQuery(templateSinglePk),
                TemplateSinglePkPack.packTemplateSinglePkQueryParams(templateSinglePk)
        );
        return count;
    }

    public List<TemplateSinglePk> listTemplateSinglePk(TemplateSinglePk templateSinglePk, Pager pager) {
        List<TemplateSinglePk> list = templateSinglePkDao.findByPrepareHqlWherePage(
                TemplateSinglePkPack.packTemplateSinglePkQuery(templateSinglePk),
                TemplateSinglePkPack.packTemplateSinglePkQueryParams(templateSinglePk),
                pager
        );
        return list;
    }

    public TemplateSinglePk saveTemplateSinglePk(TemplateSinglePk templateSinglePk) {
        syncPrimaryKey(templateSinglePk);
        TemplateSinglePk temp = (TemplateSinglePk) templateSinglePkDao.save(templateSinglePk);
        return temp;
    }

    public TemplateSinglePk getTemplateSinglePkByPk(String pk) {
        TemplateSinglePk templateSinglePk = (TemplateSinglePk)templateSinglePkDao.getByPK(pk);
        return templateSinglePk;
    }

    public void deleteTemplateSinglePkByPks(String[] pks) {
        for(String pk: pks){
            TemplateSinglePk templateSinglePk = templateSinglePkDao.getByPK(pk);
            templateSinglePkDao.remove(templateSinglePk);
        }
    }

    private void syncPrimaryKey(TemplateSinglePk templateSinglePk) {
        if (templateSinglePk.getPrimaryKey() == null || templateSinglePk.getPrimaryKey().trim().isEmpty()) {
            String tskId = templateSinglePk.getTskId();
            if (tskId == null || tskId.trim().isEmpty()) {
                throw new IllegalArgumentException("TemplateSinglePk primary key tskId must not be empty");
            }
            templateSinglePk.setPrimaryKey(tskId.trim());
        }
    }
}
