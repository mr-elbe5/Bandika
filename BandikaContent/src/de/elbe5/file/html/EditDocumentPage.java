package de.elbe5.file.html;

import de.elbe5.base.DateHelper;
import de.elbe5.file.DocumentData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditDocumentPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        DocumentData documentData = rdata.getSessionObject(ContentRequestKeys.KEY_DOCUMENT,DocumentData.class);
        String url = "/ctrl/document/saveDocument/" + documentData.getId();
        UserData creator = UserCache.getUser(documentData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(documentData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= documentData.isNew();
        appendModalStart(getHtml("_editDocumentSettings"));
        appendFormStart(sb, url , "documentform", true);
        appendModalBodyStart();
        appendTextLine(sb, getHtml("_idAndUrl"), documentData.getId() + " - " + toHtml(documentData.getFileName()));
        appendTextLine(sb, getHtml("_creation"), DateHelper.toHtmlDateTime(documentData.getCreationDate()) + " - " + toHtml(creatorName));
        appendTextLine(sb, getHtml("_lastChange"), DateHelper.toHtmlDateTime(documentData.getChangeDate()) + " - " + toHtml(changerName));
        appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", getHtml("_file"), fileRequired, false);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName",getHtml("_displayName"), true, toHtml(documentData.getDisplayName()));
        appendTextareaLine(sb, "description", getHtml("_description"), toHtml(documentData.getDescription()), "3rem");
        appendModalFooter(getHtml("_close"),getHtml("_save"));
        appendFormEnd(sb, url, "documentform", true, true, "");
        appendModalEnd();
    }
}
