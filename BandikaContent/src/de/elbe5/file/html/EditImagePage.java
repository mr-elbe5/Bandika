package de.elbe5.file.html;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.file.ImageData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditImagePage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        ImageData imageData = rdata.getSessionObject(ContentRequestKeys.KEY_IMAGE,ImageData.class);
        String url = "/ctrl/image/saveImage/" + imageData.getId();
        UserData creator = UserCache.getUser(imageData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(imageData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= imageData.isNew();
        appendModalStart(Strings.getHtml("_editImageSettings"));
        appendFormStart(sb, url , "imageform", true);
        appendModalBodyStart();
        appendTextLine(sb, Strings.getHtml("_idAndUrl"), imageData.getId() + " - " + Strings.toHtml(imageData.getFileName()));
        appendTextLine(sb, Strings.getHtml("_creation"), DateHelper.toHtmlDateTime(imageData.getCreationDate()) + " - " + Strings.toHtml(creatorName));
        appendTextLine(sb, Strings.getHtml("_lastChange"), DateHelper.toHtmlDateTime(imageData.getChangeDate()) + " - " + Strings.toHtml(changerName));
        appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", Strings.getHtml("_file"), fileRequired, false);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName",Strings.getHtml("_displayName"), true, Strings.toHtml(imageData.getDisplayName()));
        appendTextareaLine(sb, "description", Strings.getHtml("_description"), Strings.toHtml(imageData.getDescription()), "3rem");
        appendModalFooter(Strings.getHtml("_close"),Strings.getHtml("_save"));
        appendFormEnd(sb, url, "imageform", true, true, "");
        appendModalEnd();
    }
}
