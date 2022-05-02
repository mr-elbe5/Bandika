package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;

public class SPFormTextTag extends SPFormLineTag {

    public static final String TYPE = "text";

    protected String value = "";
    protected int maxLength = 0;

    String controlPreHtml = """
                    <input type="text" id="{1}" name="{2}" class="form-control" value="{3}" {4}/>
                    """;

    public SPFormTextTag(){
        this.type = TYPE;
    }

    @Override
    public void collectVariables(RequestData rdata) {
        super.collectVariables(rdata);
        value = rdata.getPageAttributes().getString("value", "");
        maxLength = rdata.getPageAttributes().getInt("maxLength", 0);
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        sb.append(format(controlPreHtml, name, name, value,maxLength > 0 ? "maxlength=\""+maxLength+"\"" : ""));
    }

}
