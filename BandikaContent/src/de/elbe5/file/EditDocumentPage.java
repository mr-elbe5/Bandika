package de.elbe5.file;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.html.Form;
import de.elbe5.html.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditDocumentPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        DocumentData documentData = rdata.getSessionObject(ContentRequestKeys.KEY_DOCUMENT,DocumentData.class);
        String url = "/ctrl/document/saveDocument/" + documentData.getId();
        UserData creator = UserCache.getUser(documentData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(documentData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= documentData.isNew();
        appendModalStart(sb, Strings.getHtml("_editDocumentSettings"));
        Form.appendFormStart(sb, url , "documentform");
        appendModalBodyStart(sb);
        Form.appendTextLine(sb, Strings.getHtml("_idAndUrl"), documentData.getId() + " - " + Strings.toHtml(documentData.getFileName()));
        Form.appendTextLine(sb, Strings.getHtml("_creation"), DateHelper.toHtmlDateTime(documentData.getCreationDate()) + " - " + Strings.toHtml(creatorName));
        Form.appendTextLine(sb, Strings.getHtml("_lastChange"), DateHelper.toHtmlDateTime(documentData.getChangeDate()) + " - " + Strings.toHtml(changerName));
        Form.appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", Strings.getHtml("_file"), fileRequired, false);
        Form.appendLineEnd(sb);
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName","_displayName", true, Strings.toHtml(documentData.getDisplayName()));
        Form.appendTextareaLine(sb, "description", Strings.getHtml("_description"), Strings.toHtml(documentData.getDescription()), "3rem");
        appendModalFooter(sb,Strings.getHtml("_close"),Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "documentform", false, true, "");
        appendModalEnd(sb);
    }
}
