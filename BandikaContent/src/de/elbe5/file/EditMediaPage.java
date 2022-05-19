package de.elbe5.file;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.html.Form;
import de.elbe5.html.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditMediaPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        MediaData mediaData = rdata.getSessionObject(ContentRequestKeys.KEY_MEDIA,MediaData.class);
        String url = "/ctrl/media/saveMedia/" + mediaData.getId();
        UserData creator = UserCache.getUser(mediaData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(mediaData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= mediaData.isNew();
        appendModalStart(sb, Strings.getHtml("_editMediaSettings"));
        Form.appendFormStart(sb, url , "mediaform");
        appendModalBodyStart(sb);
        Form.appendTextLine(sb, Strings.getHtml("_idAndUrl"), mediaData.getId() + " - " + Strings.toHtml(mediaData.getFileName()));
        Form.appendTextLine(sb, Strings.getHtml("_creation"), DateHelper.toHtmlDateTime(mediaData.getCreationDate()) + " - " + Strings.toHtml(creatorName));
        Form.appendTextLine(sb, Strings.getHtml("_lastChange"), DateHelper.toHtmlDateTime(mediaData.getChangeDate()) + " - " + Strings.toHtml(changerName));
        Form.appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", Strings.getHtml("_file"), fileRequired, false);
        Form.appendLineEnd(sb);
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName","_displayName", true, Strings.toHtml(mediaData.getDisplayName()));
        Form.appendTextareaLine(sb, "description", Strings.getHtml("_description"), Strings.toHtml(mediaData.getDescription()), "3rem");
        appendModalFooter(sb,Strings.getHtml("_close"),Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "mediaform", false, true, "");
        appendModalEnd(sb);
    }
}
