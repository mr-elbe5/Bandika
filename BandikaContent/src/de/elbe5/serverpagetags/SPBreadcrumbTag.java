package de.elbe5.serverpagetags;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

import java.util.List;

public class SPBreadcrumbTag extends SPTag {

    public static final String TYPE = "breadcrumb";

    public SPBreadcrumbTag(){
        this.type = TYPE;
    }

    static final String bcStart = """
              
                                <section class="col-12">
                                    <ol class="breadcrumb">
            """;
    static final String bcLink = """
                                        <li class="breadcrumb-item">
                                            <a href="{1}">{2}</a>
                                        </li>
            """;
    static final String bcEnd = """
                                    </ol>
                                </section>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<Integer> parentIds = ContentCache.getParentContentIds(contentData);
        sb.append(bcStart);
        for (int i = parentIds.size() - 1; i >= 0; i--) {
            ContentData content = ContentCache.getContent(parentIds.get(i));
            if (content != null) {
                sb.append(format(bcLink,
                        content.getUrl(),
                        toHtml(content.getDisplayName())));
            }
        }
        sb.append(bcEnd);
    }

}
