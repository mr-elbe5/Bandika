package de.elbe5.user.html;

import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.Map;

public class EditProfilePage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        String url = "/ctrl/user/changeProfile/" + user.getId();
        appendModalStart(getString("_changeProfile"));
        appendFormStart(sb, url, "profileform", true);
        appendModalBodyStart(getString("_profile"));
        appendHiddenField(sb, "userId", Integer.toString(user.getId()));
        appendTextLine(sb, getString("_id"), Integer.toString(user.getId()));
        appendTextInputLine(sb, rdata.hasFormErrorField("login"), "login", getString("_login"), true, toHtml(user.getLogin()));
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
        append(sb, """
                <h3>{{_address}}</h3>
                """, null);
        appendTextInputLine(sb, "street", getString("_street"), user.getStreet());
        appendTextInputLine(sb, "zipCode", getString("_zipCode"), user.getZipCode());
        appendTextInputLine(sb, "city", getString("_city"), user.getCity());
        appendTextInputLine(sb, "country", getString("_country"), user.getCountry());
        append(sb, """
                <h3>{{_contact}}</h3>
                """, null);
        appendTextInputLine(sb, "email", getString("_email"), user.getEmail());
        appendTextInputLine(sb, "phone", getString("_phone"), user.getPhone());
        appendTextInputLine(sb, "fax", getString("_fax"), user.getFax());
        appendTextInputLine(sb, "mobile", getString("_mobile"), user.getMobile());
        appendModalFooter(getString("_cancel"), getString("_save"));
        appendFormEnd(sb, url, "profileform", true, true, "");
        appendModalEnd();
    }
}
