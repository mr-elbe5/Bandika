package de.elbe5.user;

import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.html.Html;
import de.elbe5.request.RequestData;

import java.util.List;

public class UserAdminPage {

    public void appendGroupList(StringBuilder sb, RequestData rdata) {
        List<GroupData> groups = null;
        try {
            groups = GroupBean.getInstance().getAllGroups();
        } catch (Exception ignore) {
        }
        int groupId = rdata.getAttributes().getInt("groupId");
        sb.append(Html.format("""
            <li class="open">
                <span>{1}</span>
                <div class="icons">
                    <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/dlgpage/group/openCreateGroup');" title="{2}"> </a>
                </div>
                <ul>
            """,
                Html.localized("_groups"),
                Html.localized("_new")));
        if (groups != null) {
            for (GroupData group : groups) {
                sb.append(Html.format("""
                    <li class="{1}">
                        <span>{2}</span>
                        <div class="icons">
                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/dlgpage/group/openEditGroup/{3}');" title="{4}"></a>
                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/group/deleteGroup/{5}');" title="{6}"></a>
                        </div>
                    </li>
            """,
                        groupId == group.getId() ? "open" : "",
                        Html.html(group.getName()),
                        Integer.toString(group.getId()),
                        Html.localized("_edit"),
                        Integer.toString(group.getId()),
                        Html.localized("_delete")));
            }
        }
        sb.append("""
                </ul>
            </li>
            """);
    }

    public static void appendUserList(StringBuilder sb, RequestData rdata) {
        List<UserData> users = null;
        try {
            UserBean ts = UserBean.getInstance();
            users = ts.getAllUsers();
        } catch (Exception ignore) {
        }
        int userId = rdata.getAttributes().getInt("userId");
        sb.append(Html.format("""
                        <li class="open">
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/dlgpage/user/openCreateUser');" title="{2}"> </a>
                            </div>
                            <ul>
                            """,
                Html.localized("_users"),
                Html.localized("_new")));
        if (users != null) {
            for (UserData user : users) {
                sb.append(Html.format("""
                                <li class="{1}">
                                <span>{2}&nbsp;({3})</span>
                                <div class="icons">
                                    <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/dlgpage/user/openEditUser/{4}');" title="{5}"> </a>
                                    """,
                        userId == user.getId() ? "selected" : "",
                        Html.html(user.getName()),
                        Integer.toString(user.getId()),
                        Integer.toString(user.getId()),
                        Html.localized("_edit")));
                if (user.getId() != UserData.ID_ROOT) {
                    sb.append(Html.format("""
                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/user/deleteUser/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(user.getId()),
                            Html.localized("_delete")));
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
