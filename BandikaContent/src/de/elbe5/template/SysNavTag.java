package de.elbe5.template;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentData;
import de.elbe5.template.TemplateTag;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

public class SysNavTag extends TemplateTag {

    public static final String TYPE = "sysnav";

    public SysNavTag() {
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        appendAdminHtml(sb, rdata);
        appendContentHtml(sb, rdata);
        appendUserHtml(sb, rdata);
    }

    public void appendAdminHtml(StringBuilder sb, RequestData rdata) {
        sb.append("""
                                <ul class="nav justify-content-end">
            """);
        if (rdata.hasAnyElevatedSystemRight()) {
            sb.append(Strings.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-cog" href="/ctrl/admin/openAdministration" title="{1}"></a></li>
            """,
                    Strings.toHtml("_administration")));
        }
    }

    public void appendContentHtml(StringBuilder sb, RequestData rdata) {
        ContentData currentContent = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        if (currentContent!=null && !currentContent.isEditing() && currentContent.hasUserEditRight(rdata)) {
            sb.append(Strings.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-edit" href="/ctrl/content/openEditContentFrontend/{1}" title="{2}"></a></li>
            """,
                    Integer.toString(currentContent.getId()),
                    Strings.getHtml("_editPage")
            ));
            if (currentContent.hasUnpublishedDraft()) {
                if (currentContent.isPublished()) {
                    if (currentContent.getViewType().equals(ContentData.VIEW_TYPE_SHOWPUBLISHED)) {
                        sb.append(Strings.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-eye-slash" href="/ctrl/content/showDraft/{1}" title="{2}" ></a></li>
            """,
                                Integer.toString(currentContent.getId()),
                                Strings.getHtml("_showDraft")));
                    } else {
                        sb.append(Strings.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-eye" href="/ctrl/content/showPublished/{1}" title="{2}"></a></li>
            """,
                                Integer.toString(currentContent.getId()),
                                Strings.getHtml("_showPublished")));
                    }
                }
                if (currentContent.hasUserApproveRight(rdata)) {
                    sb.append(Strings.format("""
                                    <li class="nav-item"><a class="nav-link fa fa-thumbs-up" href="/ctrl/content/publishContent/{1}" title="{2}"></a></li>
            """,
                            Integer.toString(currentContent.getId()),
                            Strings.getHtml("_publish")));
                }
            }
        }
    }

    public void appendUserHtml(StringBuilder sb, RequestData rdata) {
        String userClass = rdata.isLoggedIn() ? "fa-user" : "fa-user-o";
        sb.append(Strings.format("""
                                    <li class="nav-item">
                                        <a class="nav-link fa {1}" data-toggle="dropdown" title="{2}"></a>
                                        <div class="dropdown-menu">
            """,
                userClass,
                Strings.getHtml("_user")));
        if (rdata.isLoggedIn()) {
            sb.append(Strings.format("""
                                            <a class="dropdown-item" href="/ctrl/user/openProfile">{1}</a>
                                            <a class="dropdown-item" href="/ctrl/user/logout">{2}</a>
            """,
                    Strings.getHtml("_profile"),
                    Strings.getHtml("_logout")));
        } else {
            sb.append(Strings.format("""
                                            <a class="dropdown-item" href="/ctrl/user/openLogin">{1}</a>
            """,
                    Strings.getHtml("_login")));
        }
        sb.append("""
                                        </div>
                                    </li>
                                </ul>
            """);
    }

}
