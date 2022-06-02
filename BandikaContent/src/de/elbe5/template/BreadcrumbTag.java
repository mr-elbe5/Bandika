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

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<Integer> parentIds = ContentCache.getParentContentIds(contentData);
        sb.append("""
                  
                                    <section class="col-12">
                                        <ol class="breadcrumb">
                """);
        for (int i = parentIds.size() - 1; i >= 0; i--) {
            ContentData content = ContentCache.getContent(parentIds.get(i));
            if (content != null) {
                append(sb, """
                                                            <li class="breadcrumb-item">
                                                                <a href="$url$">$name$</a>
                                                            </li>
                                """,
                        Map.ofEntries(
                                param("url", content.getUrl()),
                                param("name", content.getDisplayName())
                        )
                );
            }
        }
        sb.append("""
                                        </ol>
                                    </section>
                """);
    }

}
