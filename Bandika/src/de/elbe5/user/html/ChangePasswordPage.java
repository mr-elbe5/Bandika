package de.elbe5.user.html;

import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

public class ChangePasswordPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        UserData user = UserBean.getInstance().getUser(rdata.getLoginUser().getId());
        String url = "/ctrl/user/changePassword/" + user.getId();
        appendModalStart(getString("_changePassword"));
        appendFormStart(sb, url, "changepasswordform", false);
        appendModalBodyStart();
        appendHiddenField(sb, "userId", Integer.toString(user.getId()));
        appendPasswordLine(sb, rdata.hasFormErrorField("oldPassword"), "oldPassword", getString("_oldPassword"), true, 30);
        appendPasswordLine(sb, rdata.hasFormErrorField("newPassword1"), "newPassword1", getString("_newPassword"), true, 30);
        appendPasswordLine(sb, rdata.hasFormErrorField("newPassword2"), "newPassword2", getString("_retypePassword"), true, 30);
        append(sb, """
                        <div><small>{{_passwordHint}}</small></div>
                        """, null);
        appendModalFooter(getString("_cancel"), getString("_save"));
        appendFormEnd(sb, url, "changepasswordform", false, true, "");
        appendModalEnd();
    }
}
