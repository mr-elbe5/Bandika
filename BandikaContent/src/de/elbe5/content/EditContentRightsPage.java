package de.elbe5.content;

import de.elbe5.base.Strings;
import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.html.Form;
import de.elbe5.html.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.rights.Right;

import java.util.List;

public class EditContentRightsPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<GroupData> groups = GroupBean.getInstance().getAllGroups();
        String label, name;
        String url = "/ctrl/content/saveRights/" + contentData.getId();
        appendModalStart(sb, Strings.getHtml("_editGroupRights"));
        Form.appendFormStart(sb, url, "rightsform", true);
        appendModalBodyStart(sb, rdata, Strings.getHtml("_rights"));
        for (GroupData group : groups) {
            if (group.getId() <= GroupData.ID_MAX_FINAL)
                continue;
            label = Strings.toHtml(group.getName());
            name = "groupright_" + group.getId();
            Form.appendLineStart(sb, "", Strings.toHtml(group.getName()), true);
            Form.appendRadio(sb, name, Strings.getHtml("_rightnone"), "", !contentData.hasAnyGroupRight(group.getId()));
            Form.appendRadio(sb, name, Strings.getHtml("_rightread"), Right.READ.name(), contentData.isGroupRight(group.getId(), Right.READ));
            Form.appendRadio(sb, name, Strings.getHtml("_rightedit"), Right.EDIT.name(), contentData.isGroupRight(group.getId(), Right.EDIT));
            Form.appendRadio(sb, name, Strings.getHtml("_rightapprove"), Right.APPROVE.name(), contentData.isGroupRight(group.getId(), Right.APPROVE));
            Form.appendLineEnd(sb);
        }
        appendModalFooter(sb, Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "rightsform", false, true, "");
        appendModalEnd(sb);
    }
}
