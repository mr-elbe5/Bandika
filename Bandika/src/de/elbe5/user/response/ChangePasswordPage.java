package de.elbe5.user.response;

import de.elbe5.base.Strings;
import de.elbe5.response.FormHtml;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

public class ChangePasswordPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        String url = "/ctrl/user/changePassword/" + user.getId();
        appendModalStart(Strings.getHtml("_changePassword"));
        FormHtml.appendFormStart(sb, url, "changepasswordform", false);
        appendModalBodyStart();
        append("""
                        <input type="hidden" name="userId" value="<%=rdata.getUserId()%>"/>
                        """,
                Integer.toString(user.getId())
        );
        FormHtml.appendPasswordLine(sb, rdata.hasFormErrorField("oldPassword"), "oldPassword", Strings.getHtml("_oldPassword"), true, 30);
        FormHtml.appendPasswordLine(sb, rdata.hasFormErrorField("newPassword1"), "newPassword1", Strings.getHtml("_newPassword"), true, 30);
        FormHtml.appendPasswordLine(sb, rdata.hasFormErrorField("newPassword2"), "newPassword2", Strings.getHtml("_retypePassword"), true, 30);
        append("""
                        <div><small>{1}</small></div>
                        """,
                Strings.getHtml("_passwordHint")
        );
        appendModalFooter(Strings.getHtml("_cancel"), Strings.getHtml("_save"));
        FormHtml.appendFormEnd(sb, url, "changepasswordform", false, true, "");
        appendModalEnd();
    }
}
