package de.elbe5.serverpagetags;

import de.elbe5.base.StringFormatter;
import de.elbe5.base.StringHelper;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;
import de.elbe5.serverpage.SPTag;

public class SPMessageTag extends SPTag {

    public static final String TYPE = "message";

    public SPMessageTag(){
        this.type = TYPE;
    }

    String controlHtml = """
          <div class="alert alert-{1} alert-dismissible fade show" role="alert"> {2}
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
        """;

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){
        if (rdata.hasMessage()) {
            String msg = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGE);
            String msgType = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGETYPE);
            sb.append(StringFormatter.format(controlHtml,
                    msgType,
                    toHtml(msg)));
        }
    }

}
