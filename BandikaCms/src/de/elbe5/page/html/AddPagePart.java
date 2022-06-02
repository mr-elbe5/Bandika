package de.elbe5.page.html;

import de.elbe5.page.PagePartData;
import de.elbe5.request.RequestData;
import de.elbe5.response.HtmlResponse;
import de.elbe5.response.IHtmlBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class AddPagePart extends HtmlResponse implements IHtmlBuilder {

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        PagePartData partData = rdata.getAttributes().get(PagePartData.KEY_PART, PagePartData.class);
        partData.appendHtml(sb, rdata);
        append(sb,"""
                <script type="text/javascript">
                    updatePartEditors($('#$id$'));
                </script>
                """,
                Map.ofEntries(
                        param("id",partData.getPartWrapperId())
                )
        );
        sendHtml(response);
    }

}
