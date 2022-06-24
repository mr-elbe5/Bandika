package de.elbe5.response;

import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;

import java.util.Map;

public interface MessageHtml extends IHtmlBuilder {

    String html = """
            <div class="alert alert-{{msgType}} alert-dismissible fade show" role="alert"> {{msg}}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            """;

    default void appendMessageHtml(StringBuilder sb, RequestData rdata) {
        if (rdata.hasMessage()) {
            String msg = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGE);
            String msgType = rdata.getAttributes().getString(RequestKeys.KEY_MESSAGETYPE);
            append(sb, html, Map.ofEntries(
                    Map.entry("msgType", msgType),
                    Map.entry("msg", toHtml(msg))));
        }
    }
}
