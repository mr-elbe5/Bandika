package de.elbe5.user.html;

import de.elbe5.administration.html.AdminPage;
import de.elbe5.data.LocalizedStrings;
import de.elbe5.company.CompanyBean;
import de.elbe5.company.CompanyData;
import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;
import java.util.Map;

public class UserAdminPage extends AdminPage {

    public UserAdminPage() {
        super(LocalizedStrings.getString("_userAdministration"));
    }

    static final String htmlStart = """
            <div id="pageContent">
            """;
    static final String htmlSectionStart = """
                <section class="treeSection">
                    <ul class="tree">
                        <li class="open">
                            <a class="treeRoot">{{_persons}}
                            </a>
                            <ul>
            """;
    static final String companiesStart = """
                                <li class="open">
                                    <span>{{_companies}}</span>
                                    <div class="icons">
                                        <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/company/openCreateCompany');" title="{{_new}}"> </a>
                                    </div>
                                    <ul>
            """;
    static final String companyHtml = """
                                        <li class="{{open}}">
                                            <span>{{name}}</span>
                                            <div class="icons">
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/company/openEditCompany/{{id}}');" title="{{_edit}}"></a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/company/deleteCompany/{{id}}');" title="{{_delete}}"></a>
                                            </div>
                                        </li>
            """;
    static final String companiesEnd = """
                                    </ul>
                                </li>
            """;
    static final String groupsStart = """
                                <li class="open">
                                    <span>{{_groups}}</span>
                                    <div class="icons">
                                        <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/group/openCreateGroup');" title="{{_new}}"> </a>
                                    </div>
                                    <ul>
            """;
    static final String groupHtml = """
                                        <li class="{{open}}">
                                            <span>{{name}}</span>
                                            <div class="icons">
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/group/openEditGroup/{{id}}');" title="{{_edit}}"></a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/group/deleteGroup/{{id}}');" title="{{_delete}}"></a>
                                            </div>
                                        </li>
            """;
    static final String groupsEnd = """
                                    </ul>
                                </li>
            """;
    static final String usersStart = """
                                <li class="open">
                                    <span>{{_users}}</span>
                                    <div class="icons">
                                        <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/user/openCreateUser');" title="{{_new}}"> </a>
                                    </div>
                                    <ul>
            """;
    static final String userHtmlStart = """
                                        <li class="{{selected}}">
                                            <span>{{name}}&nbsp;({{id}})</span>
                                            <div class="icons">
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/user/openEditUser/{{id}}');" title="{{_edit}}"> </a>
            """;
    static final String userDelete = """
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/user/deleteUser/{{id}}');" title="{{_delete}}"> </a>
            """;
    static final String userHtmlEnd = """
                                            </div>
                                        </li>
            """;
    static final String usersEnd = """
                                    </ul>
                                </li>
            """;
    static final String htmlEnd = """
                            </ul>
                        </li>
                    </ul>
                </section>
            </div>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        append(sb, htmlStart);
        appendMessageHtml(sb, rdata);
        append(sb, htmlSectionStart, null);
        if (rdata.hasSystemRight(SystemZone.USER)) {
            appendCompanyList(sb, rdata);
            appendGroupList(sb, rdata);
            appendUserList(sb, rdata);
        }
        append(sb, htmlEnd);
    }

    public void appendCompanyList(StringBuilder sb, RequestData rdata) {
        List<CompanyData> companies = null;
        try {
            companies = CompanyBean.getInstance().getAllCompanies();
        } catch (Exception ignore) {
        }
        int companyId = rdata.getAttributes().getInt("companyId");
        append(sb, companiesStart, null);
        if (companies != null) {
            for (CompanyData company : companies) {
                append(sb, companyHtml,
                        Map.ofEntries(
                                Map.entry("open", companyId == company.getId() ? "open" : ""),
                                Map.entry("name", toHtml(company.getName())),
                                Map.entry("id", Integer.toString(company.getId()))));
            }
        }
        append(sb, companiesEnd);
    }

    public void appendGroupList(StringBuilder sb, RequestData rdata) {
        List<GroupData> groups = null;
        try {
            groups = GroupBean.getInstance().getAllGroups();
        } catch (Exception ignore) {
        }
        int groupId = rdata.getAttributes().getInt("groupId");
        append(sb, groupsStart, null);
        if (groups != null) {
            for (GroupData group : groups) {
                append(sb, groupHtml,
                        Map.ofEntries(
                                Map.entry("open", groupId == group.getId() ? "open" : ""),
                                Map.entry("name", toHtml(group.getName())),
                                Map.entry("id", Integer.toString(group.getId()))));
            }
        }
        append(sb, groupsEnd);
    }

    public void appendUserList(StringBuilder sb, RequestData rdata) {
        List<UserData> users = null;
        try {
            UserBean ts = UserBean.getInstance();
            users = ts.getAllUsers();
        } catch (Exception ignore) {
        }
        int userId = rdata.getAttributes().getInt("userId");
        append(sb, usersStart, null);
        if (users != null) {
            for (UserData user : users) {
                append(sb, userHtmlStart,
                        Map.ofEntries(
                                Map.entry("selected", userId == user.getId() ? "selected" : ""),
                                Map.entry("name", toHtml(user.getName())),
                                Map.entry("id", Integer.toString(user.getId()))));
                if (user.getId() != UserData.ID_ROOT) {
                    append(sb, userDelete,
                            Map.ofEntries(
                                    Map.entry("id", Integer.toString(user.getId()))));
                }
                append(sb, userHtmlEnd);
            }
        }
        append(sb, usersEnd);
    }

}
