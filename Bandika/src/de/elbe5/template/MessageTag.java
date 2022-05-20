package de.elbe5.template;

import de.elbe5.request.RequestData;
import de.elbe5.response.MessageHtml;
import de.elbe5.template.TemplateTag;

public class MessageTag extends TemplateTag {

    public static final String TYPE = "message";

    public MessageTag(){
        this.type = TYPE;
    }

    public void appendHtml(StringBuilder sb, RequestData rdata){
        MessageHtml.appendMessageHtml(sb, rdata);
    }

}
