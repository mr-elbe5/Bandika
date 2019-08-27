package de.elbe5.page;

import de.elbe5.base.cache.Strings;

public enum PageFlexClass {
    COL3("col-md-3", "_$widthOneForth"),
    COL4("col-md-4", "_$widthOneThird"),
    COL6("col-md-6", "_$widthHalf"),
    COL8("col-md-8", "_$widthTwoThirds"),
    COL9("col-md-9", "_$widthThreeFourth"),
    COL12("col-md-12", "_$widthFull");

    private String cssClass;
    private String key;

    PageFlexClass(String css, String key) {
        cssClass = css;
        this.key=key;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getKey() {
        return key;
    }
}
