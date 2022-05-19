package de.elbe5.user;

import de.elbe5.base.Strings;
import de.elbe5.html.Form;
import de.elbe5.html.ModalPage;
import de.elbe5.request.RequestData;

public class EditProfilePage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        String url = "/ctrl/user/changeProfile/" + user.getId();
        appendModalStart(sb, Strings.getHtml("_changeProfile"));
        Form.appendFormStart(sb, url, "profileform", true);
        appendModalBodyStart(sb, Strings.getHtml("_profile"));
        sb.append(Strings.format("""
                        <input type="hidden" name="userId" value="{1}"/>
                        """,
                Integer.toString(user.getId())
        ));
        Form.appendTextLine(sb, Strings.getHtml("_id"), Integer.toString(user.getId()));
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("login"), "login", Strings.getHtml("_login"), true, Strings.toHtml(user.getLogin()));
        Form.appendTextInputLine(sb, "title", Strings.getHtml("_title"), Strings.toHtml(user.getTitle()));
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("firstName"), "firstName", Strings.getHtml("_firstName"), true, Strings.toHtml(user.getFirstName()));
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("lastName"), "lastName", Strings.getHtml("_lastName"), true, Strings.toHtml(user.getLastName()));
        Form.appendTextareaLine(sb, "_notes", Strings.getHtml("_notes"), Strings.toHtml(user.getNotes()), "5rem");
        Form.appendFileLineStart(sb, "portrait", Strings.getHtml("_portrait"), false);
        if (user.hasPortrait()) {
            sb.append(Strings.format("""
                            <img src="/ctrl/user/showPortrait/{1}" alt="{2}"/>
                            """,
                    Integer.toString(user.getId()),
                    Strings.toHtml(user.getName())
            ));
        }
        Form.appendLineEnd(sb);
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_address")));
        Form.appendTextInputLine(sb, "street", Strings.getHtml("_street"), Strings.toHtml(user.getStreet()));
        Form.appendTextInputLine(sb, "zipCode", Strings.getHtml("_zipCode"), Strings.toHtml(user.getZipCode()));
        Form.appendTextInputLine(sb, "city", Strings.getHtml("_city"), Strings.toHtml(user.getCity()));
        Form.appendTextInputLine(sb, "country", Strings.getHtml("_country"), Strings.toHtml(user.getCountry()));
        sb.append(Strings.format("""
                <h3>{1}
                </h3>
                """,
                Strings.getHtml("_contact")));
        Form.appendTextInputLine(sb, "email", Strings.getHtml("_email"), Strings.toHtml(user.getEmail()));
        Form.appendTextInputLine(sb, "phone", Strings.getHtml("_phone"), Strings.toHtml(user.getPhone()));
        Form.appendTextInputLine(sb, "fax", Strings.getHtml("_fax"), Strings.toHtml(user.getFax()));
        Form.appendTextInputLine(sb, "mobile", Strings.getHtml("_mobile"), Strings.toHtml(user.getMobile()));
        appendModalFooter(sb, Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "profileform", true, true, "");
        appendModalEnd(sb);
    }
}
