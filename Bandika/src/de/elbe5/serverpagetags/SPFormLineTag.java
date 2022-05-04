package de.elbe5.serverpagetags;

import de.elbe5.base.LocalizedStrings;
import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPFormLineTag extends SPTag {

    public static final String TYPE = "line";

    protected String name = "";
    protected String label = "";
    protected boolean required = false;
    protected boolean padded = false;

    public SPFormLineTag(){
        this.type = TYPE;
    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){
        name = getStringParam("name", rdata,"");
        label = getStringParam("label", rdata,"");
        required = getBooleanParam("required", rdata,false);
        padded = getBooleanParam("padded", rdata,false);
        sb.append("<div class=\"form-group row");
        if (rdata.hasFormErrorField(name))
            sb.append(" error");
        sb.append("\">\n");
        if (label.isEmpty()) {
            sb.append("<div class=\"col-md-3\"></div>");
        } else {
            sb.append("<label class=\"col-md-3 col-form-label\"");
            if (!name.isEmpty()) {
                sb.append(" for=\"");
                sb.append(toHtml(name));
                sb.append("\"");
            }
            sb.append(">");
            sb.append(label.startsWith("_") ? LocalizedStrings.html(label) : label);
            if (required) {
                sb.append(" <sup>*</sup>");
            }
            sb.append("</label>\n");
        }
        sb.append("<div class=\"col-md-9");
        if (padded)
            sb.append(" padded");
        sb.append("\">\n");
        appendPreControlHtml(sb, rdata);
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata){
        appendPostControlHtml(sb, rdata);
        sb.append("</div></div>");
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
    }

    protected void appendPostControlHtml(StringBuilder sb, RequestData rdata) {
    }


}
