package de.elbe5.template;

import de.elbe5.content.ContentData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.Map;

public class TopContentNavTag extends TemplateTag {

    public static final String TYPE = "topContentNav";

    public TopContentNavTag() {
        this.type = TYPE;
    }

    static final String editHtml = """
            <li class="nav-item"><a class="nav-link fa fa-edit" href="/ctrl/content/openEditContentFrontend/{{id}}" title="{{_edit}}"></a></li>
            """;
    static final String showDraftHtml = """
            <li class="nav-item"><a class="nav-link fa fa-eye-slash" href="/ctrl/content/showDraft/{{id}}" title="{{_showDraft}}" ></a></li>
            """;
    static final String showPublishedHtml = """
            <li class="nav-item"><a class="nav-link fa fa-eye" href="/ctrl/content/showPublished/{{id}}" title="{{_showPublished}}"></a></li>
            """;
    static final String publishHtml = """
            <li class="nav-item"><a class="nav-link fa fa-thumbs-up" href="/ctrl/content/publishContent/{{id}}" title="{{_publish}}"></a></li>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData currentContent = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        if (currentContent != null && !currentContent.isEditing() && currentContent.hasUserEditRight(rdata)) {
            append(sb, editHtml, Map.ofEntries(
                    Map.entry("id", Integer.toString(currentContent.getId()))));
            if (currentContent.hasUnpublishedDraft()) {
                if (currentContent.isPublished()) {
                    if (currentContent.getViewType().equals(ContentData.VIEW_TYPE_SHOWPUBLISHED)) {
                        append(sb, showDraftHtml, Map.ofEntries(
                                Map.entry("id", Integer.toString(currentContent.getId()))));
                    } else {
                        append(sb, showPublishedHtml, Map.ofEntries(
                                Map.entry("id", Integer.toString(currentContent.getId()))));
                    }
                }
                if (currentContent.hasUserApproveRight(rdata)) {
                    append(sb, publishHtml, Map.ofEntries(
                            Map.entry("id", Integer.toString(currentContent.getId()))));
                }
            }
        }
    }

}
