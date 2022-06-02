package de.elbe5.user.html;

import de.elbe5.company.CompanyBean;
import de.elbe5.company.CompanyData;
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
        List<CompanyData> companies = CompanyBean.getInstance().getAllCompanies();
        List<GroupData> groups = GroupBean.getInstance().getAllGroups();
        String url = "/ctrl/user/saveUser/" + user.getId();
        appendModalStart(getHtml("_editUser"));
        appendFormStart(sb, url, "userform", true);
        appendModalBodyStart(getHtml("_settings"));
        appendHiddenField(sb, "userId", Integer.toString(user.getId()));
        appendTextLine(sb, getHtml("_id"), Integer.toString(user.getId()));
        appendTextInputLine(sb, rdata.hasFormErrorField("login"), "login", getHtml("_login"), true, toHtml(user.getLogin()));
        appendPasswordLine(sb, rdata.hasFormErrorField("password"), "password", getHtml("_password"), false, 30);
        appendTextInputLine(sb, "title", getHtml("_title"), toHtml(user.getTitle()));
        appendTextInputLine(sb, rdata.hasFormErrorField("firstName"), "firstName", getHtml("_firstName"), true, toHtml(user.getFirstName()));
        appendTextInputLine(sb, rdata.hasFormErrorField("lastName"), "lastName", getHtml("_lastName"), true, toHtml(user.getLastName()));
        appendTextareaLine(sb, "_notes", getHtml("_notes"), toHtml(user.getNotes()), "5rem");
        appendFileLineStart(sb, "portrait", getHtml("_portrait"), false);
        if (user.hasPortrait()) {
            append(sb, """
                            <img src="/ctrl/user/showPortrait/$id$" alt="$name$"/>
                            """,
                    Map.ofEntries(
                            param("id",user.getId()),
                            param("name",user.getName())
                    )
            );
        }
        appendLineEnd(sb);
        appendLineStart(sb, "", getHtml("_locked"), true);
        appendCheckbox(sb, "locked", "", "true", user.isLocked());
        appendLineEnd(sb);
        append(sb, """
                <h3>$address$
                </h3>
                """,
                Map.ofEntries(
                        param("address","_address")
                )
        );
        appendTextInputLine(sb, "street", getHtml("_street"), toHtml(user.getStreet()));
        appendTextInputLine(sb, "zipCode", getHtml("_zipCode"), toHtml(user.getZipCode()));
        appendTextInputLine(sb, "city", getHtml("_city"), toHtml(user.getCity()));
        appendTextInputLine(sb, "country", getHtml("_country"), toHtml(user.getCountry()));
        append(sb, """
                <h3>$contact$
                </h3>
                """,
                Map.ofEntries(
                        param("contact","_contact")
                )
        );
        appendTextInputLine(sb, "email", getHtml("_email"), toHtml(user.getEmail()));
        appendTextInputLine(sb, "phone", getHtml("_phone"), toHtml(user.getPhone()));
        appendTextInputLine(sb, "fax", getHtml("_fax"), toHtml(user.getFax()));
        appendTextInputLine(sb, "mobile", getHtml("_mobile"), toHtml(user.getMobile()));
        append(sb, """
                <h3>$groups$
                </h3>
                """,
                Map.ofEntries(
                        param("groups","_groups")
                )
        );
        appendTextLine(sb, getHtml("_group"), getHtml("_inGroup"));
        for (GroupData groupData : groups) {
            String label = toHtml(groupData.getName());
            appendLineStart(sb, "", label, true);
            appendCheckbox(sb, "groupIds", "", Integer.toString(groupData.getId()), user.getGroupIds().contains(groupData.getId()));
            appendLineEnd(sb);
        }
        appendModalFooter(getHtml("_cancel"), getHtml("_save"));
        appendFormEnd(sb, url, "userform", true, true, "");
        appendModalEnd();
    }

}
