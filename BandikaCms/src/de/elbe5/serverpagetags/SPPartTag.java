package de.elbe5.serverpagetags;

import de.elbe5.html.Html;
import de.elbe5.page.PageData;
import de.elbe5.page.PagePartData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateTag;
import de.elbe5.template.Template;

public class SPPartTag extends TemplateTag {

    static final String editStart="<div id=\"{1}\" class=\"partWrapper {2}\" title=\"{3}\">";
    static final String viewStart="<div id=\"{1}\" class=\"partWrapper {2}\">";
    static final String end="</div>";

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){
        String cssClass = getStringParam("cssClass", rdata, "");
        PageData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        PagePartData partData = rdata.getAttributes().get(PagePartData.KEY_PART, PagePartData.class);
        if (partData != null) {
            partData.setCssClass(cssClass);
            if (contentData.isEditing()) {
                sb.append(Html.format(editStart,
                        partData.getPartWrapperId(),
                        Html.html(partData.getCssClass()),
                        Html.html(partData.getEditTitle())
                ));
                Template.includePage(sb, "page/editPartHeader", rdata);
            }
            else{
                sb.append(Html.format(viewStart,
                        partData.getPartWrapperId(),
                        Html.html(partData.getCssClass())
                ));
            }
        }
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata){
        sb.append(end);
    }

}
