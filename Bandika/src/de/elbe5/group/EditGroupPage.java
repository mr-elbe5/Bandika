package de.elbe5.group;

import de.elbe5.base.Strings;
import de.elbe5.html.*;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;

public class EditGroupPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        GroupData group = rdata.getSessionObject("groupData", GroupData.class);
        String url = "/ctrl/group/saveGroup/" + group.getId();
        List<UserData> users = UserBean.getInstance().getAllUsers();
        appendModalStart(sb, Strings.getHtml("_editGroup"));
        Form.appendFormStart(sb, url , "groupform");
        appendModalBodyStart(sb, Strings.getHtml("_settings"));
        Form.appendLineStart(sb, "", Strings.getHtml("_id"), true);
        sb.append(group.getId());
        Form.appendLineEnd(sb);
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("name"),"name","_name", true, group.getName());
        Form.appendTextareaLine(sb, "notes", Strings.getHtml("_notes"), Strings.toHtml(group.getNotes()), "5rem");
        Form.appendLineStart(sb, "", Strings.getHtml("_globalRights"), true);
        for (SystemZone zone : SystemZone.values()) {
            String name="zoneright_"+zone.name();
            Form.appendCheckbox(sb, name, zone.name(), "true", group.hasSystemRight(zone));
        }
        Form.appendLineEnd(sb);
        Form.appendLineStart(sb, "", Strings.getHtml("_users"), true);
        Form.appendLineEnd(sb);
        for (UserData udata : users) {
            String label = Strings.toHtml(udata.getName());
            Form.appendLineStart(sb, "", label, true);
            Form.appendCheckbox(sb, "userIds", "", Integer.toString(udata.getId()), group.getUserIds().contains(udata.getId()));
            Form.appendLineEnd(sb);
        }
        appendModalFooter(sb,Strings.getHtml("_close"),Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "groupform", false, true, "");
        appendModalEnd(sb);
    }
}
