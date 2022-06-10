package de.elbe5.file.html;

import de.elbe5.file.ImageData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditImagePage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        ImageData imageData = rdata.getSessionObject(ContentRequestKeys.KEY_IMAGE,ImageData.class);
        String url = "/ctrl/image/saveImage/" + imageData.getId();
        UserData creator = UserCache.getUser(imageData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(imageData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= imageData.isNew();
        appendModalStart(getHtml("_editImageSettings"));
        appendFormStart(sb, url , "imageform", true);
        appendModalBodyStart();
        appendTextLine(sb, getHtml("_idAndUrl"), imageData.getId() + " - " + toHtml(imageData.getFileName()));
        appendTextLine(sb, getHtml("_creation"), toHtmlDateTime(imageData.getCreationDate()) + " - " + toHtml(creatorName));
        appendTextLine(sb, getHtml("_lastChange"), toHtmlDateTime(imageData.getChangeDate()) + " - " + toHtml(changerName));
        appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", getHtml("_file"), fileRequired, false);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName",getHtml("_displayName"), true, toHtml(imageData.getDisplayName()));
        appendTextareaLine(sb, "description", getHtml("_description"), toHtml(imageData.getDescription()), "3rem");
        appendModalFooter(getHtml("_close"),getHtml("_save"));
        appendFormEnd(sb, url, "imageform", true, true, "");
        appendModalEnd();
    }
}
