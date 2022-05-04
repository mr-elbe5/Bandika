package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPFormCheckTag extends SPTag {

    public static final String TYPE = "check";

    protected String name = "";
    protected String value = "";
    protected boolean checked = false;

    public SPFormCheckTag(){
        this.type = TYPE;;
    }

    @Override
    public void collectParameters() {
        name = getParameters().getString("name", "");
        value = getParameters().getString("value", "");
        checked = getParameters().getBoolean("checked", false);
    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){
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
