package de.elbe5.html;

import de.elbe5.base.StringFormatter;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;

public class MessageTag {

    public static void appendHtml(StringBuilder sb, RequestData rdata){
        if (rdata.hasMessage()) {
            String msg = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGE);
            String msgType = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGETYPE);
            sb.append(StringFormatter.format("""
                  <div class="alert alert-{1} alert-dismissible fade show" role="alert"> {2}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                      <span aria-hidden="true">&times;</span>
                    </button>
                  </div>
        """,
                    msgType,
                    Html.html(msg)));
        }
    }

}
