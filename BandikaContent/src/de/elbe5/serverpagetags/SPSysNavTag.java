package de.elbe5.serverpagetags;

import de.elbe5.content.ContentData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPSysNavTag extends SPTag {

    public static final String TYPE = "sysnav";

    public SPSysNavTag() {
        this.type = TYPE;
    }

    static final String ulStart = """
                                
                                <ul class="nav justify-content-end">
            """;
    static final String adminLink = """
                                    <li class="nav-item"><a class="nav-link fa fa-cog" href="/page/admin/openAdministration" title="{1}"></a></li>
            """;
    static final String editContentLink = """
                                    <li class="nav-item"><a class="nav-link fa fa-edit" href="/page/cmspage/openEditContentFrontend/{1}" title="{2}"></a></li>
            """;
    static final String showDraftLink = """
                                    <li class="nav-item"><a class="nav-link fa fa-eye-slash" href="/page/cmspage/showDraft/{1}" title="{2}" ></a></li>
            """;
    static final String showPublishedlink = """
                                    <li class="nav-item"><a class="nav-link fa fa-eye" href="/page/cmspage/showPublished/{1}" title="{2}"></a></li>
            """;
    static final String approveLink = """
                                    <li class="nav-item"><a class="nav-link fa fa-thumbs-up" href="/page/cmspage/publishPage/{1}" title="{2}"></a></li>
            """;
    static final String userDropdownStart = """
                                    <li class="nav-item">
                                        <a class="nav-link fa {1}" data-toggle="dropdown" title="{2}"></a>
                                        <div class="dropdown-menu">
            """;
    static final String userDropdownLinks = """
                                            <a class="dropdown-item" href="/page/user/openProfile">{1}</a>
                                            <a class="dropdown-item" href="/page/user/logout">{2}</a>
            """;
    static final String loginLink = """
                                            <a class="dropdown-item" href="" onclick="return openModalDialog('/dlgpage/user/openLogin');">{1}</a>
            """;
    static final String ulEnd = """
                                        </div>
                                    </li>
                                </ul>
            """;
    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData currentContent = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        String userClass = rdata.isLoggedIn() ? "fa-user" : "fa-user-o";
        sb.append(ulStart);
        if (rdata.hasAnyElevatedSystemRight()) {
            sb.append(format(adminLink,
                    localizedString("_administration")));
        }
        if (currentContent!=null && !currentContent.isEditing() && currentContent.hasUserEditRight(rdata)) {
            sb.append(format(editContentLink,
                    Integer.toString(currentContent.getId()),
                    localizedString("_editPage")
            ));
            if (currentContent.hasUnpublishedDraft()) {
                if (currentContent.isPublished()) {
                    if (currentContent.isPublishedView()) {
                        sb.append(format(showDraftLink,
                                Integer.toString(currentContent.getId()),
                                localizedString("_showDraft")));
                    } else {
                        sb.append(format(showPublishedlink,
                                Integer.toString(currentContent.getId()),
                                localizedString("_showPublished")));
                    }
                }
                if (currentContent.hasUserApproveRight(rdata)) {
                    sb.append(format(approveLink,
                            Integer.toString(currentContent.getId()),
                            localizedString("_publish")));
                }
            }
        }
        sb.append(format(userDropdownStart,
                userClass,
                localizedString("_user")));
        if (rdata.isLoggedIn()) {
            sb.append(format(userDropdownLinks,
                    localizedString("_profile"),
                    localizedString("_logout")));
        } else {
            sb.append(format(loginLink,
                    localizedString("_login")));
        }
        sb.append(ulEnd);
    }

}
