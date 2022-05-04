package de.elbe5.serverpagetags;

import de.elbe5.base.StringFormatter;
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
    protected boolean ajax;
    protected String target = IResponse.MODAL_DIALOG_JQID;

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
    public void collectParameters() {
        url = getParameters().getString("url", "");
        name = getParameters().getString("name", "");
        multi = getParameters().getBoolean("multi", false);
        ajax = getParameters().getBoolean("ajax", false);
        target = getParameters().getString("target", IResponse.MODAL_DIALOG_JQID);
    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata) {
        sb.append(StringFormatter.format(preHtml, url, name, name, multi ? " enctype=\"multipart/form-data\"" : ""));
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata) {
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
