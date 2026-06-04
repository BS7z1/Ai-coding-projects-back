package com.bank.beijing.template.common.pack;

import com.bank.beijing.template.core.pojo.TemplateSinglePk;
import com.bank.common.pages.Pager;

import java.io.Serializable;

public class TemplateSinglePkRequestPack implements Serializable {

    private static final long serialVersionUID = 1L;

    private TemplateSinglePk templateSinglePk;

    private Pager pager;

    private String pk;

    private String[] pks;

    public TemplateSinglePk getTemplateSinglePk() {
        return templateSinglePk;
    }

    public void setTemplateSinglePk(TemplateSinglePk templateSinglePk) {
        this.templateSinglePk = templateSinglePk;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String[] getPks() {
        return pks;
    }

    public void setPks(String[] pks) {
        this.pks = pks;
    }
}
