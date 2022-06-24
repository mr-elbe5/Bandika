package de.elbe5.template;

import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;

public interface ITemplateNode extends IHtmlBuilder {

    void appendHtml(StringBuilder sb, RequestData rdata);

}
