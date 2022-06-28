package de.elbe5.file.html;

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
        UserData creator = UserCache.getInstance().getUser(mediaData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getInstance().getUser(mediaData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= mediaData.isNew();
        appendModalStart(getString("_editMediaSettings"));
        appendFormStart(sb, url , "mediaform", true);
        appendModalBodyStart();
        appendTextLine(sb, getString("_idAndUrl"), mediaData.getId() + " - " + mediaData.getFileName());
        appendTextLine(sb, getString("_creation"), toHtmlDateTime(mediaData.getCreationDate()) + " - " + creatorName);
        appendTextLine(sb, getString("_lastChange"), toHtmlDateTime(mediaData.getChangeDate()) + " - " + changerName);
        appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", getString("_file"), fileRequired, false);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName",getString("_displayName"), true, mediaData.getDisplayName());
        appendTextareaLine(sb, "description", getString("_description"), mediaData.getDescription(), "3rem");
        appendModalFooter(getString("_close"),getString("_save"));
        appendFormEnd(sb, url, "mediaform", true, true, "");
        appendModalEnd();
    }
}
