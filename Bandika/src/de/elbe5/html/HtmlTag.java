package de.elbe5.html;

import de.elbe5.request.RequestData;

public abstract class HtmlTag {

    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendTagStart(sb, rdata);
        appendInner(sb, rdata);
        appendTagEnd(sb, rdata);
    }

    public void appendTagStart(StringBuilder sb, RequestData rdata){
    }

    public void appendInner(StringBuilder sb, RequestData rdata){

    }

    public void appendTagEnd(StringBuilder sb, RequestData rdata){
    }

}
