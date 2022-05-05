package de.elbe5.serverpagetags;

import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPFormCheckTag extends SPTag {

    public static final String TYPE = "check";

    protected String name = "";
    protected String value = "";
    protected boolean checked = false;

    public SPFormCheckTag(){
        this.type = TYPE;
    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){
        name = getStringParam("name", rdata, "");
        value = getStringParam("value", rdata,"");
        checked = getBooleanParam("checked", rdata, false);
        sb.append(format("""
                       <span>
                            <input type="checkbox" name="{1}" value="{2}" {3}/>
                                <label class="form-check-label">""
                        """,
                toHtml(name),
                toHtml(value),
                checked ? "checked" : ""));
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata){
        sb.append("""
                    </label>
                </span>
               """);
    }

}
