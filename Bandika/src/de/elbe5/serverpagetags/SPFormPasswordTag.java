package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;

public class SPFormPasswordTag extends SPFormLineTag {

    public static final String TYPE = "password";

    String controlPreHtml = """
        <input type="password" id="{1}" name="{2}" class="form-control" />
        """;

    public SPFormPasswordTag(){
        this.type = TYPE;
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        sb.append(format(controlPreHtml, name, name));
    }

}
