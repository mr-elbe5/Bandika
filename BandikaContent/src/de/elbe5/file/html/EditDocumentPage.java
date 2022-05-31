package de.elbe5.file.html;

import de.elbe5.base.DateHelper;
import de.elbe5.base.Strings;
import de.elbe5.file.DocumentData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserCache;
import de.elbe5.user.UserData;

public class EditDocumentPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        DocumentData documentData = rdata.getSessionObject(ContentRequestKeys.KEY_DOCUMENT,DocumentData.class);
        String url = "/ctrl/document/saveDocument/" + documentData.getId();
        UserData creator = UserCache.getUser(documentData.getCreatorId());
        String creatorName = creator == null ? "" : creator.getName();
        UserData changer = UserCache.getUser(documentData.getChangerId());
        String changerName = changer == null ? "" : changer.getName();
        boolean fileRequired= documentData.isNew();
        appendModalStart(Strings.getHtml("_editDocumentSettings"));
        appendFormStart(sb, url , "documentform", true);
        appendModalBodyStart();
        appendTextLine(sb, Strings.getHtml("_idAndUrl"), documentData.getId() + " - " + Strings.toHtml(documentData.getFileName()));
        appendTextLine(sb, Strings.getHtml("_creation"), DateHelper.toHtmlDateTime(documentData.getCreationDate()) + " - " + Strings.toHtml(creatorName));
        appendTextLine(sb, Strings.getHtml("_lastChange"), DateHelper.toHtmlDateTime(documentData.getChangeDate()) + " - " + Strings.toHtml(changerName));
        appendFileLineStart(sb, rdata.hasFormErrorField("file"), "file", Strings.getHtml("_file"), fileRequired, false);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("displayName"),"displayName",Strings.getHtml("_displayName"), true, Strings.toHtml(documentData.getDisplayName()));
        appendTextareaLine(sb, "description", Strings.getHtml("_description"), Strings.toHtml(documentData.getDescription()), "3rem");
        appendModalFooter(Strings.getHtml("_close"),Strings.getHtml("_save"));
        appendFormEnd(sb, url, "documentform", true, true, "");
        appendModalEnd();
    }
}
