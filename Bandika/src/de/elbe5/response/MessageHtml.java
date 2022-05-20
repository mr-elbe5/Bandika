package de.elbe5.response;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;

public class MessageHtml {
    public static void appendMessageHtml(StringBuilder sb, RequestData rdata){
        if (rdata.hasMessage()) {
            String msg = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGE);
            String msgType = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGETYPE);
            sb.append(Strings.format("""
                  <div class="alert alert-{1} alert-dismissible fade show" role="alert"> {2}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                      <span aria-hidden="true">&times;</span>
                    </button>
                  </div>
        """,
                    msgType,
                    Strings.toHtml(msg)));
        }
    }
}
