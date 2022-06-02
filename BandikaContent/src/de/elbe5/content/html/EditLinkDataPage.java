package de.elbe5.content.html;

import de.elbe5.content.LinkData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

public class EditLinkDataPage extends EditContentDataPage {

    @Override
    public void appendHtml(RequestData rdata) {
        LinkData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, LinkData.class);
        String url = "/ctrl/content/saveContentData/" + contentData.getId();
        appendStartHtml(rdata, contentData, url);
        appendTextInputLine(sb, rdata.hasFormErrorField("linkIcon"), "linkIcon", getHtml("_linkIcon"), true, toHtml(contentData.getLinkIcon()));
        appendTextInputLine(sb, rdata.hasFormErrorField("linkUrl"), "linkUrl", getHtml("_linkUrl"), true, toHtml(contentData.getLinkUrl()));
        appendEndHtml(url);
    }
}
