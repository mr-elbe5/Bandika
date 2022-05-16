package de.elbe5.user;

import de.elbe5.base.Strings;
import de.elbe5.html.Form;
import de.elbe5.html.ModalPage;
import de.elbe5.request.RequestData;

public class ChangePasswordPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        String url = "/ctrl/user/changePassword/" + user.getId();
        appendModalStart(sb, Strings.getHtml("_changePassword"));
        Form.appendFormStart(sb, url, "changepasswordform", false);
        appendModalBodyStart(sb, rdata, Strings.getHtml("_changePassword"));
        sb.append(Strings.format("""
                        <input type="hidden" name="userId" value="<%=rdata.getUserId()%>"/>
                        """,
                Integer.toString(user.getId())
        ));
        Form.appendPasswordLine(sb, rdata.hasFormErrorField("oldPassword"), "oldPassword", Strings.getHtml("_oldPassword"), true, 30);
        Form.appendPasswordLine(sb, rdata.hasFormErrorField("newPassword1"), "newPassword1", Strings.getHtml("_newPassword"), true, 30);
        Form.appendPasswordLine(sb, rdata.hasFormErrorField("newPassword2"), "newPassword2", Strings.getHtml("_retypePassword"), true, 30);
        sb.append(Strings.format("""
                        <div><small>{1}</small></div>
                        """,
                Strings.getHtml("_passwordHint")
        ));
        appendModalFooter(sb, Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "changepasswordform", false, true, "");
        appendModalEnd(sb);
    }
}
