package de.elbe5.serverpagetags;

import de.elbe5.page.PageData;
import de.elbe5.page.PagePartData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;
import de.elbe5.serverpage.ServerPage;

public class SPPartTag extends SPTag {

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
                sb.append(format(editStart,
                        partData.getPartWrapperId(),
                        toHtml(partData.getCssClass()),
                        toHtml(partData.getEditTitle())
                ));
                ServerPage.includePage(sb, "page/editPartHeader", rdata);
            }
            else{
                sb.append(format(viewStart,
                        partData.getPartWrapperId(),
                        toHtml(partData.getCssClass())
                ));
            }
        }
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata){
        sb.append(end);
    }

}
