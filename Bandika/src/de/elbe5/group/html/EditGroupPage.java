package de.elbe5.group.html;

import de.elbe5.base.Strings;
import de.elbe5.group.GroupData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;

public class EditGroupPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        GroupData group = rdata.getSessionObject("groupData", GroupData.class);
        String url = "/ctrl/group/saveGroup/" + group.getId();
        List<UserData> users = UserBean.getInstance().getAllUsers();
        appendModalStart(Strings.getHtml("_editGroup"));
        appendFormStart(sb, url , "groupform");
        appendModalBodyStart(Strings.getHtml("_settings"));
        appendLineStart(sb, "", Strings.getHtml("_id"), true);
        sb.append(group.getId());
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("name"),"name","_name", true, group.getName());
        appendTextareaLine(sb, "notes", Strings.getHtml("_notes"), Strings.toHtml(group.getNotes()), "5rem");
        appendLineStart(sb, "", Strings.getHtml("_globalRights"), true);
        for (SystemZone zone : SystemZone.values()) {
            String name="zoneright_"+zone.name();
            appendCheckbox(sb, name, zone.name(), "true", group.hasSystemRight(zone));
        }
        appendLineEnd(sb);
        appendLineStart(sb, "", Strings.getHtml("_users"), true);
        appendLineEnd(sb);
        for (UserData udata : users) {
            String label = Strings.toHtml(udata.getName());
            appendLineStart(sb, "", label, true);
            appendCheckbox(sb, "userIds", "", Integer.toString(udata.getId()), group.getUserIds().contains(udata.getId()));
            appendLineEnd(sb);
        }
        appendModalFooter(Strings.getHtml("_close"),Strings.getHtml("_save"));
        appendFormEnd(sb, url, "groupform", false, true, "");
        appendModalEnd();
    }
}
