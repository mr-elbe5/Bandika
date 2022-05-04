package de.elbe5.serverpagetags;

import de.elbe5.application.Configuration;
import de.elbe5.base.StringHelper;
import de.elbe5.request.RequestData;

public class SPFormDateTag extends SPFormLineTag {

    public static final String TYPE = "date";

    private String value = "";

    public void setValue(String value) {
        this.value = value;
    }

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
    public void collectParameters() {
        super.collectParameters();
        value = getParameters().getString("value", "");
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        sb.append(format(controlPreHtml, name, name, StringHelper.toHtml(value),name, Configuration.getLocale().getLanguage()));
    }

}
