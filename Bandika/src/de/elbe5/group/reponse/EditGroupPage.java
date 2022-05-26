package de.elbe5.group.reponse;

import de.elbe5.base.Strings;
import de.elbe5.group.GroupData;
import de.elbe5.response.FormHtml;
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
        FormHtml.appendFormStart(sb, url , "groupform");
        appendModalBodyStart(Strings.getHtml("_settings"));
        FormHtml.appendLineStart(sb, "", Strings.getHtml("_id"), true);
        sb.append(group.getId());
        FormHtml.appendLineEnd(sb);
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("name"),"name","_name", true, group.getName());
        FormHtml.appendTextareaLine(sb, "notes", Strings.getHtml("_notes"), Strings.toHtml(group.getNotes()), "5rem");
        FormHtml.appendLineStart(sb, "", Strings.getHtml("_globalRights"), true);
        for (SystemZone zone : SystemZone.values()) {
            String name="zoneright_"+zone.name();
            FormHtml.appendCheckbox(sb, name, zone.name(), "true", group.hasSystemRight(zone));
        }
        FormHtml.appendLineEnd(sb);
        FormHtml.appendLineStart(sb, "", Strings.getHtml("_users"), true);
        FormHtml.appendLineEnd(sb);
        for (UserData udata : users) {
            String label = Strings.toHtml(udata.getName());
            FormHtml.appendLineStart(sb, "", label, true);
            FormHtml.appendCheckbox(sb, "userIds", "", Integer.toString(udata.getId()), group.getUserIds().contains(udata.getId()));
            FormHtml.appendLineEnd(sb);
        }
        appendModalFooter(Strings.getHtml("_close"),Strings.getHtml("_save"));
        FormHtml.appendFormEnd(sb, url, "groupform", false, true, "");
        appendModalEnd();
    }
}
