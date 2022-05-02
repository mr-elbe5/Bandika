package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;

public class SPFormRadioTag extends SPFormCheckTag {

    public static final String TYPE = "radio";

    public SPFormRadioTag(){
        this.type = TYPE;
    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){
        sb.append(format("""
                       <span>
                            <input type="radio" name="{1}" value="{2}" {3}/>
                                <label class="form-check-label">""
                        """,
                toHtml(name),
                toHtml(value),
                checked ? "checked" : ""));
    }

}
