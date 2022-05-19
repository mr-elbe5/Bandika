package de.elbe5.file;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.html.Form;
import de.elbe5.html.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditImagePage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ImageData imageData = rdata.getSessionObject(ContentRequestKeys.KEY_IMAGE,ImageData.class);
        String url = "/ctrl/image/saveImage/" + imageData.getId();
        UserData creator = UserCache.getUser(imageData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(imageData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= imageData.isNew();
        appendModalStart(sb, Strings.getHtml("_editImageSettings"));
        Form.appendFormStart(sb, url , "imageform");
        appendModalBodyStart(sb);
        Form.appendTextLine(sb, Strings.getHtml("_idAndUrl"), imageData.getId() + " - " + Strings.toHtml(imageData.getFileName()));
        Form.appendTextLine(sb, Strings.getHtml("_creation"), DateHelper.toHtmlDateTime(imageData.getCreationDate()) + " - " + Strings.toHtml(creatorName));
        Form.appendTextLine(sb, Strings.getHtml("_lastChange"), DateHelper.toHtmlDateTime(imageData.getChangeDate()) + " - " + Strings.toHtml(changerName));
        Form.appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", Strings.getHtml("_file"), fileRequired, false);
        Form.appendLineEnd(sb);
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName","_displayName", true, Strings.toHtml(imageData.getDisplayName()));
        Form.appendTextareaLine(sb, "description", Strings.getHtml("_description"), Strings.toHtml(imageData.getDescription()), "3rem");
        appendModalFooter(sb,Strings.getHtml("_close"),Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "imageform", false, true, "");
        appendModalEnd(sb);
    }
}
