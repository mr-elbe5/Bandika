package de.elbe5.template;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;
import java.util.Map;

public class BreadcrumbTag extends TemplateTag {

    public static final String TYPE = "breadcrumb";

    public BreadcrumbTag() {
        this.type = TYPE;
    }

    static final String startHtml = """
            <ol class="breadcrumb">
            """;
    static final String linkHtml = """
                <li class="breadcrumb-item">
                    <a href="{{url}}">{{name}}</a>
                </li>
            """;
    static final String endHtml = """
            </ol>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<Integer> parentIds = ContentCache.getInstance().getParentContentIds(contentData);
        sb.append(startHtml);
        for (int i = parentIds.size() - 1; i >= 0; i--) {
            ContentData content = ContentCache.getInstance().getContent(parentIds.get(i));
            if (content != null) {
                append(sb, linkHtml, Map.ofEntries(
                        Map.entry("url", content.getUrl()),
                        Map.entry("name", toHtml(content.getDisplayName()))));
            }
        }
        sb.append(endHtml);
    }

}
