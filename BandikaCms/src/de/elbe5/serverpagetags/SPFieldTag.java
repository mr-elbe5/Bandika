package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPFieldTag extends SPTag {

    protected String name = "";
    protected String placeholder = "";

    public void setName(String name) {
        this.name = name;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public void collectVariables(RequestData rdata) {
        name = rdata.getPageAttributes().getString("name", "");
        placeholder = rdata.getPageAttributes().getString("placeholder", "");
    }
}
