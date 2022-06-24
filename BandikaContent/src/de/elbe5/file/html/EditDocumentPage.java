package de.elbe5.file.html;

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
        appendModalStart(getString("_editDocumentSettings"));
        appendFormStart(sb, url , "documentform", true);
        appendModalBodyStart();
        appendTextLine(sb, getString("_idAndUrl"), documentData.getId() + " - " + documentData.getFileName());
        appendTextLine(sb, getString("_creation"), toHtmlDateTime(documentData.getCreationDate()) + " - " + creatorName);
        appendTextLine(sb, getString("_lastChange"), toHtmlDateTime(documentData.getChangeDate()) + " - " + changerName);
        appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", getString("_file"), fileRequired, false);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName",getString("_displayName"), true, documentData.getDisplayName());
        appendTextareaLine(sb, "description", getString("_description"), documentData.getDescription(), "3rem");
        appendModalFooter(getString("_close"),getString("_save"));
        appendFormEnd(sb, url, "documentform", true, true, "");
        appendModalEnd();
    }
}
