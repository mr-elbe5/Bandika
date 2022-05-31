package de.elbe5.content.html;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentData;
import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.rights.Right;

import java.util.List;

public class EditContentRightsPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        ContentData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<GroupData> groups = GroupBean.getInstance().getAllGroups();
        String label, name;
        String url = "/ctrl/content/saveRights/" + contentData.getId();
        appendModalStart(Strings.getHtml("_editGroupRights"));
        appendFormStart(sb, url, "rightsform", true);
        appendModalBodyStart();
        for (GroupData group : groups) {
            if (group.getId() <= GroupData.ID_MAX_FINAL)
                continue;
            label = Strings.toHtml(group.getName());
            name = "groupright_" + group.getId();
            appendLineStart(sb, "", Strings.toHtml(group.getName()), true);
            appendRadio(sb, name, Strings.getHtml("_rightnone"), "", !contentData.hasAnyGroupRight(group.getId()));
            appendRadio(sb, name, Strings.getHtml("_rightread"), Right.READ.name(), contentData.isGroupRight(group.getId(), Right.READ));
            appendRadio(sb, name, Strings.getHtml("_rightedit"), Right.EDIT.name(), contentData.isGroupRight(group.getId(), Right.EDIT));
            appendRadio(sb, name, Strings.getHtml("_rightapprove"), Right.APPROVE.name(), contentData.isGroupRight(group.getId(), Right.APPROVE));
            appendLineEnd(sb);
        }
        appendModalFooter(Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        appendFormEnd(sb, url, "rightsform", false, true, "");
        appendModalEnd();
    }
}
