package de.elbe5.serverpagetags;

import de.elbe5.application.Configuration;
import de.elbe5.base.StringHelper;
import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;

public class SPFormDateTag extends SPFormLineTag {

    public static final String TYPE = "date";

    String controlPreHtml =
            """
                <div class="input-group date">
                  <input type="text" id="{1}" name="{2}" class="form-control datepicker" value="{3}" />
                </div>
                <script type="text/javascript">$('#{4}').datepicker({language: '{5}'});</script>
                """;

    public SPFormDateTag(){
        this.type = TYPE;
    }

    @Override
    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        String value = getStringParam("value", rdata, "");
        sb.append(format(controlPreHtml, name, name, StringHelper.toHtml(value),name, Configuration.getLocale().getLanguage()));
    }

}
