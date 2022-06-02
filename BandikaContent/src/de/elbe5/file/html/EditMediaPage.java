package de.elbe5.file.html;

import de.elbe5.base.DateHelper;
import de.elbe5.file.MediaData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditMediaPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        MediaData mediaData = rdata.getSessionObject(ContentRequestKeys.KEY_MEDIA,MediaData.class);
        String url = "/ctrl/media/saveMedia/" + mediaData.getId();
        UserData creator = UserCache.getUser(mediaData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(mediaData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= mediaData.isNew();
        appendModalStart(getHtml("_editMediaSettings"));
        appendFormStart(sb, url , "mediaform", true);
        appendModalBodyStart();
        appendTextLine(sb, getHtml("_idAndUrl"), mediaData.getId() + " - " + toHtml(mediaData.getFileName()));
        appendTextLine(sb, getHtml("_creation"), DateHelper.toHtmlDateTime(mediaData.getCreationDate()) + " - " + toHtml(creatorName));
        appendTextLine(sb, getHtml("_lastChange"), DateHelper.toHtmlDateTime(mediaData.getChangeDate()) + " - " + toHtml(changerName));
        appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", getHtml("_file"), fileRequired, false);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName",getHtml("_displayName"), true, toHtml(mediaData.getDisplayName()));
        appendTextareaLine(sb, "description", getHtml("_description"), toHtml(mediaData.getDescription()), "3rem");
        appendModalFooter(getHtml("_close"),getHtml("_save"));
        appendFormEnd(sb, url, "mediaform", true, true, "");
        appendModalEnd();
    }
}
