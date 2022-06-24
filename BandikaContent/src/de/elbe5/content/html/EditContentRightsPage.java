package de.elbe5.content.html;

import de.elbe5.content.ContentData;
import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.rights.Right;

import java.util.List;

public class EditContentRightsPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        ContentData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<GroupData> groups = GroupBean.getInstance().getAllGroups();
        String name;
        String url = "/ctrl/content/saveRights/" + contentData.getId();
        appendModalStart(getString("_editGroupRights"));
        appendFormStart(sb, url, "rightsform", true);
        appendModalBodyStart();
        for (GroupData group : groups) {
            if (group.getId() <= GroupData.ID_MAX_FINAL)
                continue;
            name = "groupright_" + group.getId();
            appendLineStart(sb, "", toHtml(group.getName()), true);
            appendRadio(sb, name, getString("_rightnone"), "", !contentData.hasAnyGroupRight(group.getId()));
            appendRadio(sb, name, getString("_rightread"), Right.READ.name(), contentData.isGroupRight(group.getId(), Right.READ));
            appendRadio(sb, name, getString("_rightedit"), Right.EDIT.name(), contentData.isGroupRight(group.getId(), Right.EDIT));
            appendRadio(sb, name, getString("_rightapprove"), Right.APPROVE.name(), contentData.isGroupRight(group.getId(), Right.APPROVE));
            appendLineEnd(sb);
        }
        appendModalFooter(getString("_cancel"), getString("_save"));
        appendFormEnd(sb, url, "rightsform", false, true, "");
        appendModalEnd();
    }
}
