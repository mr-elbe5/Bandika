package de.elbe5.template;

import de.elbe5.content.ContentData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.Map;

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
            append(sb,"""
                                    <li class="nav-item"><a class="nav-link fa fa-cog" href="/ctrl/admin/openAdministration" title="$administration$"></a></li>
            """,
                    Map.ofEntries(
                            param("administration","_administration")
                    )
            );
        }
    }

    public void appendContentHtml(StringBuilder sb, RequestData rdata) {
        ContentData currentContent = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        if (currentContent!=null && !currentContent.isEditing() && currentContent.hasUserEditRight(rdata)) {
            append(sb,"""
                                    <li class="nav-item"><a class="nav-link fa fa-edit" href="/ctrl/content/openEditContentFrontend/$id$" title="$edit$"></a></li>
            """,
                    Map.ofEntries(
                            param("id",currentContent.getId()),
                            param("edit","_editPage")
                    )
            );
            if (currentContent.hasUnpublishedDraft()) {
                if (currentContent.isPublished()) {
                    if (currentContent.getViewType().equals(ContentData.VIEW_TYPE_SHOWPUBLISHED)) {
                        append(sb,"""
                                    <li class="nav-item"><a class="nav-link fa fa-eye-slash" href="/ctrl/content/showDraft/$id$" title="$showDraft$" ></a></li>
            """,
                                Map.ofEntries(
                                        param("id",currentContent.getId()),
                                        param("showDraft","_showDraft")
                                )
                        );
                    } else {
                        append(sb,"""
                                    <li class="nav-item"><a class="nav-link fa fa-eye" href="/ctrl/content/showPublished/$id$" title="$v$"></a></li>
            """,
                                Map.ofEntries(
                                        param("id",currentContent.getId()),
                                        param("showPublished","_showPublished")
                                )
                        );
                    }
                }
                if (currentContent.hasUserApproveRight(rdata)) {
                    append(sb,"""
                                    <li class="nav-item"><a class="nav-link fa fa-thumbs-up" href="/ctrl/content/publishContent/$id$" title="$publish$"></a></li>
            """,
                            Map.ofEntries(
                                    param("id",currentContent.getId()),
                                    param("publish","_publish")
                            )
                    );
                }
            }
        }
    }

    public void appendUserHtml(StringBuilder sb, RequestData rdata) {
        String userClass = rdata.isLoggedIn() ? "fa-user" : "fa-user-o";
        append(sb,"""
                                    <li class="nav-item">
                                        <a class="nav-link fa $userClass$" data-toggle="dropdown" title="$user$"></a>
                                        <div class="dropdown-menu">
            """,
                Map.ofEntries(
                        param("userClass",userClass),
                        param("user","_user")
                )
        );
        if (rdata.isLoggedIn()) {
            append(sb,"""
                                            <a class="dropdown-item" href="/ctrl/user/openProfile">$profile$</a>
                                            <a class="dropdown-item" href="/ctrl/user/logout">$logout$</a>
            """,
                    Map.ofEntries(
                            param("profile","_profile"),
                            param("logout","_logout")
                    )
            );
        } else {
            append(sb,"""
                                            <a class="dropdown-item" href="/ctrl/user/openLogin">$login$</a>
            """,
                    Map.ofEntries(
                            param("login","_login")
                    )
            );
        }
        sb.append("""
                                        </div>
                                    </li>
                                </ul>
            """);
    }

}
