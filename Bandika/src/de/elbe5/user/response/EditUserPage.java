package de.elbe5.user.response;

import de.elbe5.base.Strings;
import de.elbe5.company.CompanyBean;
import de.elbe5.company.CompanyData;
import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.response.FormHtml;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserData;

import java.util.List;

public class EditUserPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserData user = rdata.getSessionObject("userData", UserData.class);
        List<CompanyData> companies = CompanyBean.getInstance().getAllCompanies();
        List<GroupData> groups = GroupBean.getInstance().getAllGroups();
        String url = "/ctrl/user/saveUser/" + user.getId();
        appendModalStart(sb, Strings.getHtml("_editUser"));
        FormHtml.appendFormStart(sb, url, "userform", true);
        appendModalBodyStart(sb, Strings.getHtml("_settings"));
        sb.append(Strings.format("""
                        <input type="hidden" name="userId" value="{1}"/>
                        """,
                Integer.toString(user.getId())
        ));
        FormHtml.appendTextLine(sb, Strings.getHtml("_id"), Integer.toString(user.getId()));
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("login"), "login", Strings.getHtml("_login"), true, Strings.toHtml(user.getLogin()));
        FormHtml.appendPasswordLine(sb, rdata.hasFormErrorField("password"), "password", Strings.getHtml("_password"), false, 30);
        FormHtml.appendTextInputLine(sb, "title", Strings.getHtml("_title"), Strings.toHtml(user.getTitle()));
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("firstName"), "firstName", Strings.getHtml("_firstName"), true, Strings.toHtml(user.getFirstName()));
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("lastName"), "lastName", Strings.getHtml("_lastName"), true, Strings.toHtml(user.getLastName()));
        FormHtml.appendTextareaLine(sb, "_notes", Strings.getHtml("_notes"), Strings.toHtml(user.getNotes()), "5rem");
        FormHtml.appendFileLineStart(sb, "portrait", Strings.getHtml("_portrait"), false);
        if (user.hasPortrait()) {
            sb.append(Strings.format("""
                            <img src="/ctrl/user/showPortrait/{1}" alt="{2}"/>
                            """,
                    Integer.toString(user.getId()),
                    Strings.toHtml(user.getName())
            ));
        }
        FormHtml.appendLineEnd(sb);
        FormHtml.appendLineStart(sb, "", Strings.getHtml("_locked"), true);
        FormHtml.appendCheckbox(sb, "locked", "", "true", user.isLocked());
        FormHtml.appendLineEnd(sb);
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_address")));
        FormHtml.appendTextInputLine(sb, "street", Strings.getHtml("_street"), Strings.toHtml(user.getStreet()));
        FormHtml.appendTextInputLine(sb, "zipCode", Strings.getHtml("_zipCode"), Strings.toHtml(user.getZipCode()));
        FormHtml.appendTextInputLine(sb, "city", Strings.getHtml("_city"), Strings.toHtml(user.getCity()));
        FormHtml.appendTextInputLine(sb, "country", Strings.getHtml("_country"), Strings.toHtml(user.getCountry()));
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_contact")));
        FormHtml.appendTextInputLine(sb, "email", Strings.getHtml("_email"), Strings.toHtml(user.getEmail()));
        FormHtml.appendTextInputLine(sb, "phone", Strings.getHtml("_phone"), Strings.toHtml(user.getPhone()));
        FormHtml.appendTextInputLine(sb, "fax", Strings.getHtml("_fax"), Strings.toHtml(user.getFax()));
        FormHtml.appendTextInputLine(sb, "mobile", Strings.getHtml("_mobile"), Strings.toHtml(user.getMobile()));
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_groups")));
        FormHtml.appendTextLine(sb, Strings.getHtml("_group"), Strings.getHtml("_inGroup"));
        for (GroupData groupData : groups) {
            String label = Strings.toHtml(groupData.getName());
            FormHtml.appendLineStart(sb, "", label, true);
            FormHtml.appendCheckbox(sb, "groupIds", "", Integer.toString(groupData.getId()), user.getGroupIds().contains(groupData.getId()));
            FormHtml.appendLineEnd(sb);
        }
        appendModalFooter(sb, Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        FormHtml.appendFormEnd(sb, url, "userform", true, true, "");
        appendModalEnd(sb);
    }

}
