package de.elbe5.content.html;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.content.ContentData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditContentDataPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        ContentData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        String url = "/ctrl/content/saveContentData/" + contentData.getId();
        appendStartHtml(rdata, contentData, url);
        appendEndHtml(url);
    }

    public void appendStartHtml(RequestData rdata, ContentData contentData, String url) {
        UserData creator = UserCache.getUser(contentData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(contentData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        appendModalStart(Strings.getHtml("_editContentData"));
        appendFormStart(sb, url, "contentform", true);
        appendModalBodyStart(Strings.getHtml("_settings"));
        appendTextLine(sb, Strings.getHtml("_idAndUrl"), Integer.toString(contentData.getId()) + " - " + Strings.toHtml(contentData.getUrl()));
        appendTextLine(sb, Strings.getHtml("_creation"), DateHelper.toHtmlDateTime(contentData.getCreationDate()) + " - " + Strings.toHtml(creatorName));
        appendTextLine(sb, Strings.getHtml("_lastChange"), DateHelper.toHtmlDateTime(contentData.getChangeDate()) + " - " + Strings.toHtml(changerName));
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"), "displayName", Strings.getHtml("_name"), true, Strings.toHtml(contentData.getDisplayName()));
        appendTextareaLine(sb, "description", Strings.getHtml("_description"), Strings.toHtml(contentData.getDescription()), "5rem");
        appendSelectStart(sb, "accessType", Strings.getHtml("_accessType"));
        append("""
                        <option value="{1}" {2}>{3}</option>
                        <option value="{4}" {5}>{6}</option>
                        <option value="{7}" {8}>{9}</option>
                        """,
                ContentData.ACCESS_TYPE_OPEN,
                contentData.getAccessType().equals(ContentData.ACCESS_TYPE_OPEN) ? "selected" : "",
                Strings.getHtml("system.accessTypeOpen"),
                ContentData.ACCESS_TYPE_INHERITS,
                contentData.getAccessType().equals(ContentData.ACCESS_TYPE_INHERITS) ? "selected" : "",
                Strings.getHtml("system.accessTypeInherits"),
                ContentData.ACCESS_TYPE_INDIVIDUAL,
                contentData.getAccessType().equals(ContentData.ACCESS_TYPE_INDIVIDUAL) ? "selected" : "",
                Strings.getHtml("system.accessTypeIndividual")
        );
        appendSelectEnd(sb);
        appendSelectStart(sb, "navType", Strings.getHtml("_navType"));
        append("""
                        <option value="{1}" {2}>{3}</option>
                        <option value="{4}" {5}>{6}</option>
                        <option value="{7}" {8}>{9}</option>
                        """,
                ContentData.NAV_TYPE_NONE,
                contentData.getNavType().equals(ContentData.NAV_TYPE_NONE) ? "selected" : "",
                Strings.getHtml("system.navTypeNone"),
                ContentData.NAV_TYPE_HEADER,
                contentData.getNavType().equals(ContentData.NAV_TYPE_HEADER) ? "selected" : "",
                Strings.getHtml("system.navTypeHeader"),
                ContentData.NAV_TYPE_FOOTER,
                contentData.getNavType().equals(ContentData.NAV_TYPE_FOOTER) ? "selected" : "",
                Strings.getHtml("system.navTypeFooter")
        );
        appendSelectEnd(sb);
        appendLineStart(sb, "", Strings.getHtml("_active"));
        appendCheckbox(sb, "active", "", "true", contentData.isActive());
        appendLineEnd(sb);
    }

    public void appendEndHtml(String url) {
        appendModalFooter(Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        appendFormEnd(sb, url, "contentform", false, true, "");
        appendModalEnd();
    }
}
