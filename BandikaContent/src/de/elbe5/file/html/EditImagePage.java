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
        appendModalStart(getString("_editImageSettings"));
        appendFormStart(sb, url , "imageform", true);
        appendModalBodyStart();
        appendTextLine(sb, getString("_idAndUrl"), imageData.getId() + " - " + imageData.getFileName());
        appendTextLine(sb, getString("_creation"), toHtmlDateTime(imageData.getCreationDate()) + " - " + creatorName);
        appendTextLine(sb, getString("_lastChange"), toHtmlDateTime(imageData.getChangeDate()) + " - " +changerName);
        appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", getString("_file"), fileRequired, false);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName",getString("_displayName"), true, imageData.getDisplayName());
        appendTextareaLine(sb, "description", getString("_description"), imageData.getDescription(), "3rem");
        appendModalFooter(getString("_close"),getString("_save"));
        appendFormEnd(sb, url, "imageform", true, true, "");
        appendModalEnd();
    }
}
