package com.bank.beijing.template.web.controller.dwr;


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
    public ResultBean getTemplateSinglePk(ServletContext context, HttpServletRequest request, String pk){
        TemplateSinglePk templateSinglePk = templateSinglePkService.getTemplateSinglePk(pk);
        logger.info("根据主键获得 TemplateSinglePk ...");
        return WebUtilWork.WebResultPack(templateSinglePk);
    }

    /**
     * 删除 TemplateSinglePk
     */
    public ResultBean deleteTemplateSinglePk(ServletContext context, HttpServletRequest request, String[] pks){
        templateSinglePkService.deleteTemplateSinglePk(pks);
        for (String pk: pks){
            logger.info("删除 TemplateSinglePk...{}", pk);
        }
        return WebUtilWork.WebResultPack(null);
    }
}
