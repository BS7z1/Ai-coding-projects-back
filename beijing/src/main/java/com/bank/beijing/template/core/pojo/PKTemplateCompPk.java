package com.bank.beijing.template.core.pojo;

public class PKTemplateCompPk implements java.io.Serializable {

    private String tskId;

    private String loanId;

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getTskId() {
        return tskId;
    }

    public void setTskId(String tskId) {
        this.tskId = tskId;
    }

    @Override
    public int hashCode(){
        return 1*tskId.hashCode()+2*loanId.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(null == obj) return false;
        if(getClass() != obj.getClass()) return false;
        PKTemplateCompPk other = (PKTemplateCompPk) obj;
        return tskId.equals(other.tskId) && loanId.equals(other.loanId);
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("tskId:");
        sb.append(tskId);
        sb.append(",");
        sb.append("loanId:");
        sb.append(loanId);
        return sb.toString();
    }






}
