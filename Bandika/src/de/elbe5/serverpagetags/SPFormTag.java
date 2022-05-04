package de.elbe5.serverpagetags;

import de.elbe5.base.StringFormatter;
import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;
import de.elbe5.response.IResponse;
import de.elbe5.serverpage.SPTag;

public class SPFormTag extends SPTag {

    public static final String TYPE = "form";

    public SPFormTag() {
        this.type = TYPE;
    }

    protected String url;
    protected String name;
    protected boolean multi;

    String preHtml = "<form action=\"{1}\" method=\"post\" id=\"{2}\" name=\"{3}\" accept-charset=\"UTF-8\"{4}>\n";
    String postHtml = "</form>\n";
    String ajaxHtml = """
            <script type="text/javascript">
            $('#{1}').submit(function (event) {
            var $this = $(this);
                event.preventDefault();
                var params = $this.{2}();
                {3}('{4}', params,'{5}');
              });
            </script>
            """;

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata) {
        url = getStringParam("url", rdata,"");
        name = getStringParam("name", rdata,"");
        multi = getBooleanParam("multi", rdata,false);
        sb.append(StringFormatter.format(preHtml, url, name, name, multi ? " enctype=\"multipart/form-data\"" : ""));
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata) {
        boolean ajax = getBooleanParam("ajax", rdata,false);
        String target = getStringParam("target", rdata, IResponse.MODAL_DIALOG_JQID);
        sb.append(postHtml);
        if (ajax) {
            sb.append(StringFormatter.format(ajaxHtml,
                    name,
                    multi ? "serializeFiles" : "serialize",
                    multi ? "postMultiByAjax" : "postByAjax",
                    url,
                    target));
        }
    }

}
