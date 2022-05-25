package de.elbe5.file.response;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.file.DocumentData;
import de.elbe5.response.FormHtml;
import de.elbe5.response.ModalPage;
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
        FormHtml.appendFormStart(sb, url , "documentform", true);
        appendModalBodyStart(sb);
        FormHtml.appendTextLine(sb, Strings.getHtml("_idAndUrl"), documentData.getId() + " - " + Strings.toHtml(documentData.getFileName()));
        FormHtml.appendTextLine(sb, Strings.getHtml("_creation"), DateHelper.toHtmlDateTime(documentData.getCreationDate()) + " - " + Strings.toHtml(creatorName));
        FormHtml.appendTextLine(sb, Strings.getHtml("_lastChange"), DateHelper.toHtmlDateTime(documentData.getChangeDate()) + " - " + Strings.toHtml(changerName));
        FormHtml.appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", Strings.getHtml("_file"), fileRequired, false);
        FormHtml.appendLineEnd(sb);
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName","_displayName", true, Strings.toHtml(documentData.getDisplayName()));
        FormHtml.appendTextareaLine(sb, "description", Strings.getHtml("_description"), Strings.toHtml(documentData.getDescription()), "3rem");
        appendModalFooter(sb,Strings.getHtml("_close"),Strings.getHtml("_save"));
        FormHtml.appendFormEnd(sb, url, "documentform", true, true, "");
        appendModalEnd(sb);
    }
}
