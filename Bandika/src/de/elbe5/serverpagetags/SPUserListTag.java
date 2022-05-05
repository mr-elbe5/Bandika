package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;

public class SPUserListTag extends SPTag {

    public static final String TYPE = "userlist";

    public SPUserListTag() {
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        List<UserData> users = null;
        try {
            UserBean ts = UserBean.getInstance();
            users = ts.getAllUsers();
        } catch (Exception ignore) {
        }
        int userId = rdata.getAttributes().getInt("userId");
        sb.append(format("""
                        <li class="open">
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/dlgpage/user/openCreateUser');" title="{2}"> </a>
                            </div>
                            <ul>
                            """,
                localizedString("_users"),
                localizedString("_new")));
        if (users != null) {
            for (UserData user : users) {
                sb.append(format("""
                                <li class="{1}">
                                <span>{2}&nbsp;({3})</span>
                                <div class="icons">
                                    <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/dlgpage/user/openEditUser/{4}');" title="{5}"> </a>
                                    """,
                        userId == user.getId() ? "selected" : "",
                        toHtml(user.getName()),
                        Integer.toString(user.getId()),
                        Integer.toString(user.getId()),
                        localizedString("_edit")));
                if (user.getId() != UserData.ID_ROOT) {
                    sb.append(format("""
                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/user/deleteUser/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(user.getId()),
                            localizedString("_delete")));
                }
                sb.append("""
                        </div>
                        </li>
                        """);
            }
        }
        sb.append(""" 
                    </ul>
                </li>
                """);
    }

}
