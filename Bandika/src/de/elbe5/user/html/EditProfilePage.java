package de.elbe5.user.html;

import de.elbe5.base.Strings;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

public class EditProfilePage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        String url = "/ctrl/user/changeProfile/" + user.getId();
        appendModalStart(Strings.getHtml("_changeProfile"));
        appendFormStart(sb, url, "profileform", true);
        appendModalBodyStart(Strings.getHtml("_profile"));
        append("""
                        <input type="hidden" name="userId" value="{1}"/>
                        """,
                Integer.toString(user.getId())
        );
        appendTextLine(sb, Strings.getHtml("_id"), Integer.toString(user.getId()));
        appendTextInputLine(sb, rdata.hasFormErrorField("login"), "login", Strings.getHtml("_login"), true, Strings.toHtml(user.getLogin()));
        appendTextInputLine(sb, "title", Strings.getHtml("_title"), Strings.toHtml(user.getTitle()));
        appendTextInputLine(sb, rdata.hasFormErrorField("firstName"), "firstName", Strings.getHtml("_firstName"), true, Strings.toHtml(user.getFirstName()));
        appendTextInputLine(sb, rdata.hasFormErrorField("lastName"), "lastName", Strings.getHtml("_lastName"), true, Strings.toHtml(user.getLastName()));
        appendTextareaLine(sb, "_notes", Strings.getHtml("_notes"), Strings.toHtml(user.getNotes()), "5rem");
        appendFileLineStart(sb, "portrait", Strings.getHtml("_portrait"), false);
        if (user.hasPortrait()) {
            append("""
                            <img src="/ctrl/user/showPortrait/{1}" alt="{2}"/>
                            """,
                    Integer.toString(user.getId()),
                    Strings.toHtml(user.getName())
            );
        }
        appendLineEnd(sb);
        append("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_address")
        );
        appendTextInputLine(sb, "street", Strings.getHtml("_street"), Strings.toHtml(user.getStreet()));
        appendTextInputLine(sb, "zipCode", Strings.getHtml("_zipCode"), Strings.toHtml(user.getZipCode()));
        appendTextInputLine(sb, "city", Strings.getHtml("_city"), Strings.toHtml(user.getCity()));
        appendTextInputLine(sb, "country", Strings.getHtml("_country"), Strings.toHtml(user.getCountry()));
        append("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_contact")
        );
        appendTextInputLine(sb, "email", Strings.getHtml("_email"), Strings.toHtml(user.getEmail()));
        appendTextInputLine(sb, "phone", Strings.getHtml("_phone"), Strings.toHtml(user.getPhone()));
        appendTextInputLine(sb, "fax", Strings.getHtml("_fax"), Strings.toHtml(user.getFax()));
        appendTextInputLine(sb, "mobile", Strings.getHtml("_mobile"), Strings.toHtml(user.getMobile()));
        appendModalFooter(Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        appendFormEnd(sb, url, "profileform", true, true, "");
        appendModalEnd();
    }
}
