package de.elbe5.response;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;

import java.util.Map;

public interface MessageHtml extends IHtmlBuilder{
    default void appendMessageHtml(StringBuilder sb, RequestData rdata){
        if (rdata.hasMessage()) {
            String msg = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGE);
            String msgType = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGETYPE);
            append(sb,"""
                  <div class="alert alert-$msgType$ alert-dismissible fade show" role="alert"> $msg$
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                      <span aria-hidden="true">&times;</span>
                    </button>
                  </div>
        """,
                    Map.ofEntries(
                            param("msgType",msgType),
                            param("msg",msg)
                    )
            );
        }
    }
}
