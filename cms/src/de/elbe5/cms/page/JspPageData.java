package de.elbe5.cms.page;

public class JspPageData extends PageData{

    protected String jsp="";

    public String getJsp() {
        return jsp;
    }

    public void setJsp(String jsp) {
        this.jsp = jsp;
    }

    public String getMasterName(){
        return "defaultMaster";
    }

    public String getInclude(){
        return getJsp();
    }

    public boolean isDynamic(){
        return true;
    }

}
