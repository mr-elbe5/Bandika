package de.elbe5.content.html;

import de.elbe5.content.ContentData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

import java.util.Map;

public class EditContentDataPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        ContentData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        String url = "/ctrl/content/saveContentData/" + contentData.getId();
        appendStartHtml(rdata, contentData, url);
        appendEndHtml(url);
    }

    public void appendStartHtml(RequestData rdata, ContentData contentData, String url) {
        UserData creator = UserCache.getInstance().getUser(contentData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getInstance().getUser(contentData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        appendModalStart(getString("_editContentData"));
        appendFormStart(sb, url, "contentform", true);
        appendModalBodyStart(getString("_settings"));
        appendTextLine(sb, getString("_idAndUrl"), contentData.getId() + " - " + contentData.getUrl());
        appendTextLine(sb, getString("_creation"), toHtmlDateTime(contentData.getCreationDate()) + " - " + creatorName);
        appendTextLine(sb, getString("_lastChange"), toHtmlDateTime(contentData.getChangeDate()) + " - " + changerName);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"), "displayName", getString("_name"), true, contentData.getDisplayName());
        appendTextareaLine(sb, "description", getString("_description"), contentData.getDescription(), "5rem");
        appendSelectStart(sb, "accessType", getString("_accessType"));
        append(sb, """
                        <option value="{{a1}}" {{a2}}>{{a3}}</option>
                        <option value="{{b1}}" {{b2}}>{{b3}}</option>
                        <option value="{{c1}}" {{c2}}>{{c3}}</option>
                        """,
                Map.ofEntries(
                        Map.entry("a1",ContentData.ACCESS_TYPE_OPEN),
                        Map.entry("a2",contentData.getAccessType().equals(ContentData.ACCESS_TYPE_OPEN) ? "selected" : ""),
                        Map.entry("a3",getString("system.accessTypeOpen")),
                        Map.entry("b1",ContentData.ACCESS_TYPE_INHERITS),
                        Map.entry("b2",contentData.getAccessType().equals(ContentData.ACCESS_TYPE_INHERITS) ? "selected" : ""),
                        Map.entry("b3",getString("system.accessTypeInherits")),
                        Map.entry("c1",ContentData.ACCESS_TYPE_INDIVIDUAL),
                        Map.entry("c2",contentData.getAccessType().equals(ContentData.ACCESS_TYPE_INDIVIDUAL) ? "selected" : ""),
                        Map.entry("c3",getString("system.accessTypeIndividual"))
                )
        );
        appendSelectEnd(sb);
        appendSelectStart(sb, "navType", getString("_navType"));
        append(sb, """
                        <option value="{{a1}}" {{a2}}>{{a3}}</option>
                        <option value="{{b1}}" {{b2}}>{{b3}}</option>
                        <option value="{{c1}}" {{c2}}>{{c3}}</option>
                        """,
                Map.ofEntries(
                        Map.entry("a1",ContentData.NAV_TYPE_NONE),
                        Map.entry("a2",contentData.getNavType().equals(ContentData.NAV_TYPE_NONE) ? "selected" : ""),
                        Map.entry("a3",getString("system.navTypeNone")),
                        Map.entry("b1",ContentData.NAV_TYPE_HEADER),
                        Map.entry("b2",contentData.getNavType().equals(ContentData.NAV_TYPE_HEADER) ? "selected" : ""),
                        Map.entry("b3",getString("system.navTypeHeader")),
                        Map.entry("c1",ContentData.NAV_TYPE_FOOTER),
                        Map.entry("c2",contentData.getNavType().equals(ContentData.NAV_TYPE_FOOTER) ? "selected" : ""),
                        Map.entry("c3",getString("system.navTypeFooter"))
                )
        );
        appendSelectEnd(sb);
        appendLineStart(sb, "", getString("_active"));
        appendCheckbox(sb, "active", "", "true", contentData.isActive());
        appendLineEnd(sb);
    }

    public void appendEndHtml(String url) {
        appendModalFooter(getString("_cancel"), getString("_save"));
        appendFormEnd(sb, url, "contentform", false, true, "");
        appendModalEnd();
    }
}
