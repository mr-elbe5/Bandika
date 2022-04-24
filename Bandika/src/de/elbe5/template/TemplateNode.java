package de.elbe5.template;

import de.elbe5.request.RequestData;

public interface TemplateNode {

    void appendHtml(StringBuilder sb, RequestData rdata);
}
