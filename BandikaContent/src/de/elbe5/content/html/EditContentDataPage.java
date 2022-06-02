package de.elbe5.content.html;

import de.elbe5.base.DateHelper;
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
        UserData creator = UserCache.getUser(contentData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(contentData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        appendModalStart(getHtml("_editContentData"));
        appendFormStart(sb, url, "contentform", true);
        appendModalBodyStart(getHtml("_settings"));
        appendTextLine(sb, getHtml("_idAndUrl"), Integer.toString(contentData.getId()) + " - " + toHtml(contentData.getUrl()));
        appendTextLine(sb, getHtml("_creation"), DateHelper.toHtmlDateTime(contentData.getCreationDate()) + " - " + toHtml(creatorName));
        appendTextLine(sb, getHtml("_lastChange"), DateHelper.toHtmlDateTime(contentData.getChangeDate()) + " - " + toHtml(changerName));
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"), "displayName", getHtml("_name"), true, toHtml(contentData.getDisplayName()));
        appendTextareaLine(sb, "description", getHtml("_description"), toHtml(contentData.getDescription()), "5rem");
        appendSelectStart(sb, "accessType", getHtml("_accessType"));
        append(sb, """
                        <option value="$a1$" $a2$>$a3$</option>
                        <option value="$b1$" $b2$>$b3$</option>
                        <option value="$c1$" $c2$>$c3$</option>
                        """,
                Map.ofEntries(
                        param("a1",ContentData.ACCESS_TYPE_OPEN),
                        param("a2",contentData.getAccessType().equals(ContentData.ACCESS_TYPE_OPEN) ? "selected" : ""),
                        param("a3",getString("system.accessTypeOpen")),
                        param("b1",ContentData.ACCESS_TYPE_INHERITS),
                        param("b2",contentData.getAccessType().equals(ContentData.ACCESS_TYPE_INHERITS) ? "selected" : ""),
                        param("b3",getString("system.accessTypeInherits")),
                        param("c1",ContentData.ACCESS_TYPE_INDIVIDUAL),
                        param("c2",contentData.getAccessType().equals(ContentData.ACCESS_TYPE_INDIVIDUAL) ? "selected" : ""),
                        param("c3",getString("system.accessTypeIndividual"))
                )
        );
        appendSelectEnd(sb);
        appendSelectStart(sb, "navType", getHtml("_navType"));
        append(sb, """
                        <option value="$a1$" $a2$>$a3$</option>
                        <option value="$b1$" $b2$>$b3$</option>
                        <option value="$c1$" $c2$>$c3$</option>
                        """,
                Map.ofEntries(
                        param("a1",ContentData.NAV_TYPE_NONE),
                        param("a2",contentData.getNavType().equals(ContentData.NAV_TYPE_NONE) ? "selected" : ""),
                        param("a3",getString("system.navTypeNone")),
                        param("b1",ContentData.NAV_TYPE_HEADER),
                        param("b2",contentData.getNavType().equals(ContentData.NAV_TYPE_HEADER) ? "selected" : ""),
                        param("b3",getString("system.navTypeHeader")),
                        param("c1",ContentData.NAV_TYPE_FOOTER),
                        param("c2",contentData.getNavType().equals(ContentData.NAV_TYPE_FOOTER) ? "selected" : ""),
                        param("c3",getString("system.navTypeFooter"))
                )
        );
        appendSelectEnd(sb);
        appendLineStart(sb, "", getHtml("_active"));
        appendCheckbox(sb, "active", "", "true", contentData.isActive());
        appendLineEnd(sb);
    }

    public void appendEndHtml(String url) {
        appendModalFooter(getHtml("_cancel"), getHtml("_save"));
        appendFormEnd(sb, url, "contentform", false, true, "");
        appendModalEnd();
    }
}
