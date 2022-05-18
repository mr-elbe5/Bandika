package de.elbe5.page;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.response.HtmlResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class AddPagePart extends HtmlResponse {

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        StringBuilder sb = new StringBuilder();
        PagePartData partData = rdata.getAttributes().get(PagePartData.KEY_PART, PagePartData.class);
        partData.appendHtml(sb, rdata);
        sb.append(Strings.format("""
                <script type="text/javascript">
                    updatePartEditors($('#{1}'));
                </script>
                """,
                partData.getPartWrapperId()
        ));
        html = sb.toString();
        sendHtml(response);
    }

}
