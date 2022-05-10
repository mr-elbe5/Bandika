package de.elbe5.layout;

import de.elbe5.content.ContentData;
import de.elbe5.html.Html;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateTag;

public class SysNavTag extends TemplateTag {

    public static final String TYPE = "sysnav";

    public SysNavTag() {
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
            sb.append(Html.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-cog" href="/page/admin/openAdministration" title="{1}"></a></li>
            """,
                    Html.localized("_administration")));
        }
        if (currentContent!=null && !currentContent.isEditing() && currentContent.hasUserEditRight(rdata)) {
            sb.append(Html.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-edit" href="/page/cmspage/openEditContentFrontend/{1}" title="{2}"></a></li>
            """,
                    Integer.toString(currentContent.getId()),
                    Html.localized("_editPage")
            ));
            if (currentContent.hasUnpublishedDraft()) {
                if (currentContent.isPublished()) {
                    if (currentContent.isPublishedView()) {
                        sb.append(Html.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-eye-slash" href="/page/cmspage/showDraft/{1}" title="{2}" ></a></li>
            """,
                                Integer.toString(currentContent.getId()),
                                Html.localized("_showDraft")));
                    } else {
                        sb.append(Html.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-eye" href="/page/cmspage/showPublished/{1}" title="{2}"></a></li>
            """,
                                Integer.toString(currentContent.getId()),
                                Html.localized("_showPublished")));
                    }
                }
                if (currentContent.hasUserApproveRight(rdata)) {
                    sb.append(Html.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-thumbs-up" href="/page/cmspage/publishPage/{1}" title="{2}"></a></li>
            """,
                            Integer.toString(currentContent.getId()),
                            Html.localized("_publish")));
                }
            }
        }
        sb.append(Html.format("""
                                    <li class="nav-item">
                                        <a class="nav-link fa {1}" data-toggle="dropdown" title="{2}"></a>
                                        <div class="dropdown-menu">
            """,
                userClass,
                Html.localized("_user")));
        if (rdata.isLoggedIn()) {
            sb.append(Html.format("""
                                            <a class="dropdown-item" href="/page/user/openProfile">{1}</a>
                                            <a class="dropdown-item" href="/page/user/logout">{2}</a>
            """,
                    Html.localized("_profile"),
                    Html.localized("_logout")));
        } else {
            sb.append(Html.format("""
                                            <a class="dropdown-item" href="" onclick="return openModalDialog('/dlgpage/user/openLogin');">{1}</a>
            """,
                    Html.localized("_login")));
        }
        sb.append("""
                                        </div>
                                    </li>
                                </ul>
            """);
    }

}
