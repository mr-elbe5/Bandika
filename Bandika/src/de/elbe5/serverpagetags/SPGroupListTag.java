package de.elbe5.serverpagetags;

import de.elbe5.group.GroupBean;
import de.elbe5.group.GroupData;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

import java.util.List;

public class SPGroupListTag extends SPTag {

    public static final String TYPE = "grouplist";

    public SPGroupListTag(){
        this.type = TYPE;
    }

    static final String htmlStart = """
            <li class="open">
                <span>{1}</span>
                <div class="icons">
                    <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/group/openCreateGroup');" title="{2}"> </a>
                </div>
                <ul>
            """;

    static final String groupHtml = """
                    <li class="{1}">
                        <span>{2}</span>
                        <div class="icons">
                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/group/openEditGroup/{3}');" title="{4}"></a>
                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/group/deleteGroup/{5}');" title="{6}"></a>
                        </div>
                    </li>
            """;

    static final String htmlEnd = """
                </ul>
            </li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        List<GroupData> groups = null;
        try {
            groups = GroupBean.getInstance().getAllGroups();
        } catch (Exception ignore) {
        }
        int groupId = rdata.getAttributes().getInt("groupId");
        sb.append(format(htmlStart,
                localizedString("_groups"),
                localizedString("_new")));
        if (groups != null) {
            for (GroupData group : groups) {
                sb.append(format(groupHtml,
                        groupId == group.getId() ? "open" : "",
                        toHtml(group.getName()),
                        Integer.toString(group.getId()),
                        localizedString("_edit"),
                        Integer.toString(group.getId()),
                        localizedString("_delete")));
            }
        }
        sb.append(htmlEnd);
    }

}
