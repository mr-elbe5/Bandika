package de.elbe5.cms.page;

public enum PagePartFlexClass {
    COL1("col-md-1"),
    COL2("col-md-2"),
    COL3("col-md-3"),
    COL4("col-md-4"),
    COL6("col-md-6"),
    COL8("col-md-8"),
    COL9("col-md-9"),
    COL12("col-md-12");

    private String cssClass="";

    PagePartFlexClass(String css){
        cssClass=css;
    }

    public String getCssClass(){
        return cssClass;
    }
}
