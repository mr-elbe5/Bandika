package de.elbe5.file.response;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.file.MediaData;
import de.elbe5.response.FormHtml;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditMediaPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        MediaData mediaData = rdata.getSessionObject(ContentRequestKeys.KEY_MEDIA,MediaData.class);
        String url = "/ctrl/media/saveMedia/" + mediaData.getId();
        UserData creator = UserCache.getUser(mediaData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(mediaData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= mediaData.isNew();
        appendModalStart(Strings.getHtml("_editMediaSettings"));
        FormHtml.appendFormStart(sb, url , "mediaform", true);
        appendModalBodyStart();
        FormHtml.appendTextLine(sb, Strings.getHtml("_idAndUrl"), mediaData.getId() + " - " + Strings.toHtml(mediaData.getFileName()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_creation"), DateHelper.toHtmlDateTime(mediaData.getCreationDate()) + " - " + Strings.toHtml(creatorName));
        FormHtml.appendTextLine(sb, Strings.getHtml("_lastChange"), DateHelper.toHtmlDateTime(mediaData.getChangeDate()) + " - " + Strings.toHtml(changerName));
        FormHtml.appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", Strings.getHtml("_file"), fileRequired, false);
        FormHtml.appendLineEnd(sb);
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName","_displayName", true, Strings.toHtml(mediaData.getDisplayName()));
        FormHtml.appendTextareaLine(sb, "description", Strings.getHtml("_description"), Strings.toHtml(mediaData.getDescription()), "3rem");
        appendModalFooter(Strings.getHtml("_close"),Strings.getHtml("_save"));
        FormHtml.appendFormEnd(sb, url, "mediaform", true, true, "");
        appendModalEnd();
    }
}
