package de.elbe5.group.html;

import de.elbe5.group.GroupData;
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
        appendModalStart(getHtml("_editGroup"));
        appendFormStart(sb, url , "groupform");
        appendModalBodyStart(getHtml("_settings"));
        appendLineStart(sb, "", getHtml("_id"), true);
        sb.append(group.getId());
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("name"),"name",getHtml("_name"), true, group.getName());
        appendTextareaLine(sb, "notes", getHtml("_notes"), toHtml(group.getNotes()), "5rem");
        appendLineStart(sb, "", getHtml("_globalRights"), true);
        for (SystemZone zone : SystemZone.values()) {
            String name="zoneright_"+zone.name();
            appendCheckbox(sb, name, zone.name(), "true", group.hasSystemRight(zone));
        }
        appendLineEnd(sb);
        appendLineStart(sb, "", getHtml("_users"), true);
        appendLineEnd(sb);
        for (UserData udata : users) {
            String label = toHtml(udata.getName());
            appendLineStart(sb, "", label, true);
            appendCheckbox(sb, "userIds", "", Integer.toString(udata.getId()), group.getUserIds().contains(udata.getId()));
            appendLineEnd(sb);
        }
        appendModalFooter(getHtml("_close"),getHtml("_save"));
        appendFormEnd(sb, url, "groupform", false, true, "");
        appendModalEnd();
    }
}
