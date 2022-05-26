package de.elbe5.user.response;

import de.elbe5.administration.response.AdminPage;
import de.elbe5.base.Strings;
import de.elbe5.company.CompanyBean;
import de.elbe5.company.CompanyData;
import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.response.MessageHtml;
import de.elbe5.request.RequestData;
import de.elbe5.rights.SystemZone;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import java.util.List;

public class UserAdminPage extends AdminPage {

    @Override
    public void appendHtml(RequestData rdata) {
        append("""
                <div id="pageContent">
                """);
        MessageHtml.appendMessageHtml(sb, rdata);
        append("""
                        <section class="treeSection">
                            <ul class="tree">
                                <li class="open">
                                    <a class="treeRoot">{1}
                                    </a>
                                    <ul>
                                    """,
                Strings.getHtml("_persons")
        );
        if (rdata.hasSystemRight(SystemZone.USER)) {
            appendCompanyList(rdata);
            appendGroupList(rdata);
            appendUserList(rdata);
        }
        append("""        
                                </ul>
                            </li>
                        </ul>
                    </section>
                </div>
                """
        );
    }

    public void appendCompanyList(RequestData rdata) {
        List<CompanyData> companies = null;
        try {
            companies = CompanyBean.getInstance().getAllCompanies();
        } catch (Exception ignore) {
        }
        int companyId = rdata.getAttributes().getInt("companyId");
        append("""
                        <li class="open">
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/company/openCreateCompany');" title="{2}"> </a>
                            </div>
                            <ul>
                        """,
                Strings.getHtml("_companies"),
                Strings.getHtml("_new")
        );
        if (companies != null) {
            for (CompanyData company : companies) {
                append("""
                                        <li class="{1}">
                                            <span>{2}</span>
                                            <div class="icons">
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/company/openEditCompany/{3}');" title="{4}"></a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/company/deleteCompany/{5}');" title="{6}"></a>
                                            </div>
                                        </li>
                                """,
                        companyId == company.getId() ? "open" : "",
                        Strings.toHtml(company.getName()),
                        Integer.toString(company.getId()),
                        Strings.getHtml("_edit"),
                        Integer.toString(company.getId()),
                        Strings.getHtml("_delete")
                );
            }
        }
        append("""
                    </ul>
                </li>
                """);
    }

    public void appendGroupList(RequestData rdata) {
        List<GroupData> groups = null;
        try {
            groups = GroupBean.getInstance().getAllGroups();
        } catch (Exception ignore) {
        }
        int groupId = rdata.getAttributes().getInt("groupId");
        append("""
                        <li class="open">
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/group/openCreateGroup');" title="{2}"> </a>
                            </div>
                            <ul>
                        """,
                Strings.getHtml("_groups"),
                Strings.getHtml("_new")
        );
        if (groups != null) {
            for (GroupData group : groups) {
                append("""
                                        <li class="{1}">
                                            <span>{2}</span>
                                            <div class="icons">
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/group/openEditGroup/{3}');" title="{4}"></a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/group/deleteGroup/{5}');" title="{6}"></a>
                                            </div>
                                        </li>
                                """,
                        groupId == group.getId() ? "open" : "",
                        Strings.toHtml(group.getName()),
                        Integer.toString(group.getId()),
                        Strings.getHtml("_edit"),
                        Integer.toString(group.getId()),
                        Strings.getHtml("_delete")
                );
            }
        }
        append("""
                    </ul>
                </li>
                """);
    }

    public void appendUserList(RequestData rdata) {
        List<UserData> users = null;
        try {
            UserBean ts = UserBean.getInstance();
            users = ts.getAllUsers();
        } catch (Exception ignore) {
        }
        int userId = rdata.getAttributes().getInt("userId");
        append("""
                        <li class="open">
                            <span>{1}</span>
                            <div class="icons">
                                <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/user/openCreateUser');" title="{2}"> </a>
                            </div>
                            <ul>
                            """,
                Strings.getHtml("_users"),
                Strings.getHtml("_new")
        );
        if (users != null) {
            for (UserData user : users) {
                append("""
                                <li class="{1}">
                                <span>{2}&nbsp;({3})</span>
                                <div class="icons">
                                    <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/user/openEditUser/{4}');" title="{5}"> </a>
                                    """,
                        userId == user.getId() ? "selected" : "",
                        Strings.toHtml(user.getName()),
                        Integer.toString(user.getId()),
                        Integer.toString(user.getId()),
                        Strings.getHtml("_edit")
                );
                if (user.getId() != UserData.ID_ROOT) {
                    append("""
                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/user/deleteUser/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(user.getId()),
                            Strings.getHtml("_delete")
                    );
                }
                append("""
                        </div>
                        </li>
                        """);
            }
        }
        append(""" 
                    </ul>
                </li>
                """);
    }

}
