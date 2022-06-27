package de.elbe5.user.html;

import de.elbe5.user.GroupData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;

public class EditGroupPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        GroupData group = rdata.getSessionObject("groupData", GroupData.class);
        String url = "/ctrl/group/saveGroup/" + group.getId();
        List<UserData> users = UserBean.getInstance().getAllUsers();
        appendModalStart(getString("_editGroup"));
        appendFormStart(sb, url , "groupform");
        appendModalBodyStart(getString("_settings"));
        appendLineStart(sb, "", getString("_id"), true);
        sb.append(group.getId());
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("name"),"name",getString("_name"), true, group.getName());
        appendTextareaLine(sb, "notes", getString("_notes"), group.getNotes(), "5rem");
        appendLineStart(sb, "", getString("_globalRights"), true);
        for (SystemZone zone : SystemZone.values()) {
            String name="zoneright_"+zone.name();
            appendCheckbox(sb, name, zone.name(), "true", group.hasSystemRight(zone));
        }
        appendLineEnd(sb);
        appendLineStart(sb, "", getString("_users"), true);
        appendLineEnd(sb);
        for (UserData udata : users) {
            String label = udata.getName();
            appendLineStart(sb, "", label, true);
            appendCheckbox(sb, "userIds", "", Integer.toString(udata.getId()), group.getUserIds().contains(udata.getId()));
            appendLineEnd(sb);
        }
        appendModalFooter(getString("_close"),getHtml("_save"));
        appendFormEnd(sb, url, "groupform", false, true, "");
        appendModalEnd();
    }
}
