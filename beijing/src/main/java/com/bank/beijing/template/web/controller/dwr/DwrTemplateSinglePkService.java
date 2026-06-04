package com.bank.beijing.template.web.controller.dwr;


import com.bank.beijing.template.common.pack.TemplateSinglePkRequestPack;
import com.bank.beijing.template.core.iservice.ITemplateSinglePkService;
import com.bank.beijing.template.core.pojo.TemplateSinglePk;
import com.bank.common.module.ResultBean;
import com.bank.common.pages.Pager;
import com.bank.common.pages.PagerHelper;
import com.bank.common.util.AssoUtil;
import com.bank.common.util.WebUtilWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class DwrTemplateSinglePkService {
    private final static Logger logger = LoggerFactory.getLogger(DwrTemplateSinglePkService.class);

    @Resource
    private ITemplateSinglePkService templateSinglePkService;

    public ResultBean listTemplateSinglePk(TemplateSinglePkRequestPack pack) {
        TemplateSinglePk templateSinglePk = pack.getTemplateSinglePk() == null ? new TemplateSinglePk() : pack.getTemplateSinglePk();
        Pager pager = pack.getPager() == null ? new Pager() : pack.getPager();
        return listTemplateSinglePk(null, null, templateSinglePk, pager);
    }

    public ResultBean saveTemplateSinglePk(TemplateSinglePkRequestPack pack) {
        TemplateSinglePk templateSinglePk = pack.getTemplateSinglePk() == null ? new TemplateSinglePk() : pack.getTemplateSinglePk();
        return saveTemplateSinglePk(null, null, templateSinglePk);
    }

    public ResultBean updateTemplateSinglePk(TemplateSinglePkRequestPack pack) {
        TemplateSinglePk templateSinglePk = pack.getTemplateSinglePk() == null ? new TemplateSinglePk() : pack.getTemplateSinglePk();
        return updateTemplateSinglePk(null, null, templateSinglePk);
    }

    public ResultBean getTemplateSinglePkByPk(TemplateSinglePkRequestPack pack) {
        return getTemplateSinglePkByPk(null, null, pack.getPk());
    }

    public ResultBean deleteTemplateSinglePkByPks(TemplateSinglePkRequestPack pack) {
        String[] pks = pack.getPks() == null ? new String[0] : pack.getPks();
        return deleteTemplateSinglePkByPks(null, null, pks);
    }

    /**
     * 查询TemplateSinglePk分页列表
     */
    public ResultBean listTemplateSinglePk(ServletContext context, HttpServletRequest request, TemplateSinglePk templateSinglePk, Pager pager){
        List<TemplateSinglePk> list =null;
        pager = PagerHelper.getPager(pager, templateSinglePkService.listTemplateSinglePkCount(templateSinglePk));
        list = templateSinglePkService.listTemplateSinglePk(templateSinglePk, pager);
        logger.info("查询 TemplateSinglePk 分页列表...");
        return WebUtilWork.WebResultPack(list, pager);
    }

    /**
     * 保存TemplateSinglePk
     */
    public ResultBean saveTemplateSinglePk(ServletContext context, HttpServletRequest request, TemplateSinglePk templateSinglePk){
        String empNo = AssoUtil.getEmpNo();
        templateSinglePk.initSave(empNo);
        templateSinglePkService.saveTemplateSinglePk(templateSinglePk);
        logger.info("保存 TemplateSinglePk ...");
        return WebUtilWork.WebResultPack(null);
    }

    /**
     * 更新TemplateSinglePk
     */
    public ResultBean updateTemplateSinglePk(ServletContext context, HttpServletRequest request, TemplateSinglePk templateSinglePk){
        templateSinglePkService.saveTemplateSinglePk(templateSinglePk);
        logger.info("更新 TemplateSinglePk ...");
        return WebUtilWork.WebResultPack(null);
    }

    /**
     * 根据主键获得TemplateSinglePk
     */
    public ResultBean getTemplateSinglePkByPk(ServletContext context, HttpServletRequest request, String pk){
        TemplateSinglePk templateSinglePk = templateSinglePkService.getTemplateSinglePkByPk(pk);
        logger.info("根据主键获得 TemplateSinglePk ...");
        return WebUtilWork.WebResultPack(templateSinglePk);
    }

    /**
     * 删除 TemplateSinglePk
     */
    public ResultBean deleteTemplateSinglePkByPks(ServletContext context, HttpServletRequest request, String[] pks){
        templateSinglePkService.deleteTemplateSinglePkByPks(pks);
        for (String pk: pks){
            logger.info("删除 TemplateSinglePk...{}", pk);
        }
        return WebUtilWork.WebResultPack(null);
    }
}
