package de.elbe5.content;

import de.elbe5.base.Strings;
import de.elbe5.html.Form;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

public class EditLinkDataPage extends EditContentDataPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        LinkData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, LinkData.class);
        String url = "/ctrl/content/saveContentData/" + contentData.getId();
        appendStartHtml(sb, rdata, contentData, url);
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("linkIcon"), "linkIcon", Strings.getHtml("_linkIcon"), true, Strings.toHtml(contentData.getLinkIcon()));
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("linkUrl"), "linkUrl", Strings.getHtml("_linkUrl"), true, Strings.toHtml(contentData.getLinkUrl()));
        appendEndHtml(sb, url);
    }
}
