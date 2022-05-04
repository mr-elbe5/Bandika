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

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData currentContent = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        String userClass = rdata.isLoggedIn() ? "fa-user" : "fa-user-o";
        sb.append("""
                <ul class="nav justify-content-end">
                """);

        if (rdata.hasAnyElevatedSystemRight()) {
            sb.append(format("""
                            <li class="nav-item"><a class="nav-link fa fa-cog" href="/ctrl/admin/openAdministration" title="{1}"></a></li>
                            """,
                    localizedString("_administration")));
        }
        if (currentContent!=null && !currentContent.isEditing() && currentContent.hasUserEditRight(rdata)) {
            sb.append(format("""
                            <li class="nav-item"><a class="nav-link fa fa-edit" href="/ctrl/page/openEditContentFrontend/{1}" title="{2}"></a></li>
                            """,
                    Integer.toString(currentContent.getId()),
                    localizedString("_editPage")
            ));
            if (currentContent.hasUnpublishedDraft()) {
                if (currentContent.isPublished()) {
                    if (currentContent.isPublishedView()) {
                        sb.append(format("""
                                        <li class="nav-item"><a class="nav-link fa fa-eye-slash" href="/ctrl/page/showDraft/{1}" title="{2}" ></a></li>
                                        """,
                                Integer.toString(currentContent.getId()),
                                localizedString("_showDraft")));
                    } else {
                        sb.append(format("""
                                        <li class="nav-item"><a class="nav-link fa fa-eye" href="/ctrl/page/showPublished/{1}" title="{2}"></a></li>
                                        """,
                                Integer.toString(currentContent.getId()),
                                localizedString("_showPublished")));
                    }
                }
                if (currentContent.hasUserApproveRight(rdata)) {
                    sb.append(format("""
                                    <li class="nav-item"><a class="nav-link fa fa-thumbs-up" href="/ctrl/page/publishPage/{1}" title="{2}"></a></li>
                                    """,
                            Integer.toString(currentContent.getId()),
                            localizedString("_publish")));
                }
            }
        }
        sb.append(format("""
                        <li class="nav-item">
                        <a class="nav-link fa {1}" data-toggle="dropdown" title="{2}"></a>
                        <div class="dropdown-menu">
                        """,
                userClass,
                localizedString("_user")));
        if (rdata.isLoggedIn()) {
            sb.append(format("""
                            <a class="dropdown-item" href="/ctrl/user/openProfile">{1}
                            </a>
                            <a class="dropdown-item" href="/ctrl/user/logout">{2}
                            </a>
                            """,
                    localizedString("_profile"),
                    localizedString("_logout")));
        } else {
            sb.append(format("""
                            <a class="dropdown-item" href="" onclick="return openModalDialog('/ctrl/user/openLogin');">{1}
                            </a>
                            """,
                    localizedString("_login")));
        }
        sb.append("""
                </div>
                </li>
                </ul>
                """);
    }

}
