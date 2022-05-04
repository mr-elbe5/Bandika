package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;

public class SPFormTextTag extends SPFormLineTag {

    public static final String TYPE = "text";

    String controlPreHtml = """
                    <input type="text" id="{1}" name="{2}" class="form-control" value="{3}" {4}/>
                    """;

    public SPFormTextTag(){
        this.type = TYPE;
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        String value = getStringParam("value", rdata,"");
        int maxLength = getIntParam("maxLength", rdata,0);
        sb.append(format(controlPreHtml, name, name, value,maxLength > 0 ? "maxlength=\""+maxLength+"\"" : ""));
    }

}
