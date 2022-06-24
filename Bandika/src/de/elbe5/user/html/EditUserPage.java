package de.elbe5.user.html;

import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserData;

import java.util.List;
import java.util.Map;

public class EditUserPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        UserData user = rdata.getSessionObject("userData", UserData.class);
        List<GroupData> groups = GroupBean.getInstance().getAllGroups();
        String url = "/ctrl/user/saveUser/" + user.getId();
        appendModalStart(getString("_editUser"));
        appendFormStart(sb, url, "userform", true);
        appendModalBodyStart(getString("_settings"));
        appendHiddenField(sb, "userId", Integer.toString(user.getId()));
        appendTextLine(sb, getString("_id"), Integer.toString(user.getId()));
        appendTextInputLine(sb, rdata.hasFormErrorField("login"), "login", getString("_login"), true, user.getLogin());
        appendPasswordLine(sb, rdata.hasFormErrorField("password"), "password", getString("_password"), false, 30);
        appendTextInputLine(sb, "title", getString("_title"), user.getTitle());
        appendTextInputLine(sb, rdata.hasFormErrorField("firstName"), "firstName", getString("_firstName"), true, user.getFirstName());
        appendTextInputLine(sb, rdata.hasFormErrorField("lastName"), "lastName", getString("_lastName"), true, user.getLastName());
        appendTextareaLine(sb, "_notes", getString("_notes"), user.getNotes(), "5rem");
        appendFileLineStart(sb, "portrait", getString("_portrait"), false);
        if (user.hasPortrait()) {
            append(sb, """
                            <img src="/ctrl/user/showPortrait/{{id}}" alt="{{name}}"/>
                            """,
                    Map.ofEntries(
                            Map.entry("id", Integer.toString(user.getId())),
                            Map.entry("name", toHtml(user.getName()))
                    )
            );
        }
        appendLineEnd(sb);
        appendLineStart(sb, "", getString("_locked"), true);
        appendCheckbox(sb, "locked", "", "true", user.isLocked());
        appendLineEnd(sb);
        append(sb, """
                <h3>{{_address}}</h3>
                """, null);
        appendTextInputLine(sb, "street", getString("_street"), user.getStreet());
        appendTextInputLine(sb, "zipCode", getString("_zipCode"), user.getZipCode());
        appendTextInputLine(sb, "city", getString("_city"), user.getCity());
        appendTextInputLine(sb, "country", getString("_country"), user.getCountry());
        append(sb, """
                <h3>{{_contact}}
                </h3>
                """, null);
        appendTextInputLine(sb, "email", getString("_email"), user.getEmail());
        appendTextInputLine(sb, "phone", getString("_phone"), user.getPhone());
        appendTextInputLine(sb, "fax", getString("_fax"), user.getFax());
        appendTextInputLine(sb, "mobile", getString("_mobile"), user.getMobile());
        append(sb, """
                <h3>{{_groups}}</h3>
                """, null);
        appendTextLine(sb, getString("_group"), getHtml("_inGroup"));
        for (GroupData groupData : groups) {
            String label = groupData.getName();
            appendLineStart(sb, "", label, true);
            appendCheckbox(sb, "groupIds", "", Integer.toString(groupData.getId()), user.getGroupIds().contains(groupData.getId()));
            appendLineEnd(sb);
        }
        appendModalFooter(getString("_cancel"), getHtml("_save"));
        appendFormEnd(sb, url, "userform", true, true, "");
        appendModalEnd();
    }

}
