package de.elbe5.template;

import de.elbe5.request.RequestData;
import de.elbe5.response.MessageHtml;

public class MessageTag extends TemplateTag implements MessageHtml {

    public static final String TYPE = "message";

    public MessageTag(){
        this.type = TYPE;
    }

    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendMessageHtml(sb, rdata);
    }

}
