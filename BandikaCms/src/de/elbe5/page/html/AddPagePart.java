package de.elbe5.page.html;

import de.elbe5.base.Strings;
import de.elbe5.page.PagePartData;
import de.elbe5.request.RequestData;
import de.elbe5.response.HtmlResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class AddPagePart extends HtmlResponse {

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        PagePartData partData = rdata.getAttributes().get(PagePartData.KEY_PART, PagePartData.class);
        partData.appendHtml(sb, rdata);
        append(Strings.format("""
                <script type="text/javascript">
                    updatePartEditors($('#{1}'));
                </script>
                """,
                partData.getPartWrapperId()
        ));
        sendHtml(response);
    }

}
