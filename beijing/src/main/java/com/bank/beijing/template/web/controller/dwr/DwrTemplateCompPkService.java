package com.bank.beijing.template.web.controller.dwr;


import com.bank.beijing.template.core.iservice.ITemplateCompPkService;
import com.bank.beijing.template.core.pojo.PKTemplateCompPk;
import com.bank.beijing.template.core.pojo.TemplateCompPk;
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
public class DwrTemplateCompPkService {
    private final static Logger logger = LoggerFactory.getLogger(DwrTemplateCompPkService.class);

    @Resource
    private ITemplateCompPkService templateCompPkService;

    /**
     * 查询TemplateCompPk分页列表
     */
    public ResultBean listTemplateCompPk(ServletContext context, HttpServletRequest request, TemplateCompPk templateCompPk, Pager pager){
        List<TemplateCompPk> list =null;
        pager = PagerHelper.getPager(pager, templateCompPkService.listTemplateCompPkCount(templateCompPk));
        list = templateCompPkService.listTemplateCompPk(templateCompPk, pager);
        logger.info("查询 TemplateCompPk 分页列表...");
        return WebUtilWork.WebResultPack(list, pager);
    }

    /**
     * 保存TemplateCompPk
     */
    public ResultBean saveTemplateCompPk(ServletContext context, HttpServletRequest request, TemplateCompPk templateCompPk){
        String empNo = AssoUtil.getEmpNo();
        templateCompPk.initSave(empNo);
        templateCompPkService.saveTemplateCompPk(templateCompPk);
        logger.info("保存 TemplateCompPk ...");
        return WebUtilWork.WebResultPack(null);
    }

    /**
     * 更新TemplateCompPk
     */
    public ResultBean updateTemplateCompPk(ServletContext context, HttpServletRequest request, TemplateCompPk templateCompPk){
        templateCompPkService.saveTemplateCompPk(templateCompPk);
        logger.info("更新 TemplateCompPk ...");
        return WebUtilWork.WebResultPack(null);
    }

    /**
     * 根据主键获得TemplateCompPk
     */
    public ResultBean getTemplateCompPkByPk(ServletContext context, HttpServletRequest request, PKTemplateCompPk pk){
        TemplateCompPk templateCompPk = templateCompPkService.getTemplateCompPkByPk(pk);
        logger.info("根据主键获得 TemplateCompPk ...");
        return WebUtilWork.WebResultPack(templateCompPk);
    }

    /**
     * 删除 TemplateCompPk
     */
    public ResultBean deleteTemplateCompPkByPks(ServletContext context, HttpServletRequest request, PKTemplateCompPk[] pks){
        templateCompPkService.deleteTemplateCompPkByPks(pks);
        for (PKTemplateCompPk pk: pks){
            logger.info("删除 TemplateCompPk...{}", pk);
        }
        return WebUtilWork.WebResultPack(null);
    }
}
