package de.elbe5.user.response;

import de.elbe5.base.Strings;
import de.elbe5.response.FormHtml;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

public class EditProfilePage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        String url = "/ctrl/user/changeProfile/" + user.getId();
        appendModalStart(sb, Strings.getHtml("_changeProfile"));
        FormHtml.appendFormStart(sb, url, "profileform", true);
        appendModalBodyStart(sb, Strings.getHtml("_profile"));
        sb.append(Strings.format("""
                        <input type="hidden" name="userId" value="{1}"/>
                        """,
                Integer.toString(user.getId())
        ));
        FormHtml.appendTextLine(sb, Strings.getHtml("_id"), Integer.toString(user.getId()));
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("login"), "login", Strings.getHtml("_login"), true, Strings.toHtml(user.getLogin()));
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
        appendModalFooter(sb, Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        FormHtml.appendFormEnd(sb, url, "profileform", true, true, "");
        appendModalEnd(sb);
    }
}
