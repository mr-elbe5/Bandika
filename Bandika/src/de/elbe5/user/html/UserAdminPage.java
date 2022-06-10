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

    @Override
    public void appendPageHtml(RequestData rdata) {
        append(sb, """
                <div id="pageContent">
                """);
        appendMessageHtml(sb, rdata);
        append(sb, """
                        <section class="treeSection">
                            <ul class="tree">
                                <li class="open">
                                    <a class="treeRoot">$persons$
                                    </a>
                                    <ul>
                                    """,
                Map.ofEntries(
                        param("persons","_persons")
                )
        );
        if (rdata.hasSystemRight(SystemZone.USER)) {
            appendCompanyList(rdata);
            appendGroupList(rdata);
            appendUserList(rdata);
        }
        append(sb,"""        
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
        append(sb, """
                        <li class="open">
                            <span>$companies$</span>
                            <div class="icons">
                                <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/company/openCreateCompany');" title="$new$"> </a>
                            </div>
                            <ul>
                        """,
                Map.ofEntries(
                        param("companies","_companies"),
                        param("new","_new")
                )
        );
        if (companies != null) {
            for (CompanyData company : companies) {
                append(sb, """
                                        <li class="$open$">
                                            <span>$name$</span>
                                            <div class="icons">
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/company/openEditCompany/$id$');" title="$edit$"></a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/company/deleteCompany/$id$');" title="$delete$"></a>
                                            </div>
                                        </li>
                                """,
                        Map.ofEntries(
                                param("open",companyId == company.getId() ? "open" : ""),
                                param("name",company.getName()),
                                param("id",company.getId()),
                                param("edit","_edit"),
                                param("delete","_delete")
                        )
                );
            }
        }
        append(sb, """
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
        append(sb, """
                        <li class="open">
                            <span>$groups$</span>
                            <div class="icons">
                                <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/group/openCreateGroup');" title="$new$"> </a>
                            </div>
                            <ul>
                        """,
                Map.ofEntries(
                        param("groups","_groups"),
                        param("new","_new")
                )
        );
        if (groups != null) {
            for (GroupData group : groups) {
                append(sb, """
                                        <li class="$open$">
                                            <span>$name$</span>
                                            <div class="icons">
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/group/openEditGroup/$id$');" title="$edit$"></a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/group/deleteGroup/$id$');" title="$delete$"></a>
                                            </div>
                                        </li>
                                """,
                        Map.ofEntries(
                                param("open",groupId == group.getId() ? "open" : ""),
                                param("name",group.getName()),
                                param("id",group.getId()),
                                param("edit","_edit"),
                                param("delete","_delete")
                        )
                );
            }
        }
        append(sb, """
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
        append(sb, """
                        <li class="open">
                            <span>$users$</span>
                            <div class="icons">
                                <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/user/openCreateUser');" title="$new$"> </a>
                            </div>
                            <ul>
                            """,
                Map.ofEntries(
                        param("users","_users"),
                        param("new","_new")
                )
        );
        if (users != null) {
            for (UserData user : users) {
                append(sb, """
                                <li class="$selected$">
                                <span>$name$&nbsp;($id$)</span>
                                <div class="icons">
                                    <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/user/openEditUser/$id$');" title="$edit$"> </a>
                                    """,
                        Map.ofEntries(
                                param("selected",userId == user.getId() ? "selected" : ""),
                                param("name",user.getName()),
                                param("id",user.getId()),
                                param("edit","_edit")
                        )
                );
                if (user.getId() != UserData.ID_ROOT) {
                    append(sb, """
                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/user/deleteUser/$id$');" title="$delete$"> </a>
                                    """,
                            Map.ofEntries(
                                    param("id",user.getId()),
                                    param("delete","_delete")
                            )
                    );
                }
                append(sb, """
                        </div>
                        </li>
                        """);
            }
        }
        append(sb, """ 
                    </ul>
                </li>
                """);
    }

}
