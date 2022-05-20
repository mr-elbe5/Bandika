package de.elbe5.template;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.template.TemplateTag;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class BreadcrumbTag extends TemplateTag {

    public static final String TYPE = "breadcrumb";

    public BreadcrumbTag(){
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
                sb.append(Strings.format("""
                                        <li class="breadcrumb-item">
                                            <a href="{1}">{2}</a>
                                        </li>
            """,
                        content.getUrl(),
                        Strings.toHtml(content.getDisplayName())));
            }
        }
        sb.append("""
                                    </ol>
                                </section>
            """);
    }

}
