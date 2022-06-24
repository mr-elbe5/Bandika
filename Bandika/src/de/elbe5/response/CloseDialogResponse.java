/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class CloseDialogResponse extends HtmlResponse implements IFormBuilder {

    String url;
    String msg = "";
    String msgType = "";
    private String targetId = "";

    public CloseDialogResponse(String url) {
        this.url = url;
    }

    public CloseDialogResponse(String url, String msg, String msgType) {
        this.url = url;
        this.msg = msg;
        this.msgType = msgType;
    }

    public CloseDialogResponse(String url, String msg, String msgType, String targetId) {
        this.url = url;
        this.msg = msg;
        this.msgType = msgType;
        this.targetId = targetId;
    }

    static final String startHtml = """
            <div id="pageContent">
                <form action="{{url}}" method="POST" id="forwardform" accept-charset="UTF-8">
            """;
    static final String endHtml = """
                </form>
            </div>
            <script type="text/javascript">
                $('#forwardform').submit();
            </script>
            """;
    static final String emptyHtml = """
            <div id="pageContent"></div>
            <script type="text/javascript">
                    closeModalDialog();
                    postByAjax('{{url}}', {{msg}}, '{{target}}');
            </script>
            """;

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response) {
        if (targetId.isEmpty()) {
            append(sb, startHtml, Map.ofEntries(
                    Map.entry("url", url)));
            if (!msg.isEmpty()) {
                appendHiddenField(sb, RequestKeys.KEY_MESSAGE, msg);
                appendHiddenField(sb, RequestKeys.KEY_MESSAGETYPE, msgType);
            }
            append(sb, endHtml);
        } else {
            StringBuilder ssb = new StringBuilder("{");
            if (!msg.isEmpty()) {
                ssb.append(RequestKeys.KEY_MESSAGE).append(" : '").append(toJs(msg)).append("',");
                ssb.append(RequestKeys.KEY_MESSAGETYPE).append(" : '").append(toJs(msgType)).append("'");
            }
            ssb.append("}");
            append(sb, emptyHtml,
                    Map.ofEntries(
                            Map.entry("url", url),
                            Map.entry("msg", toHtml(ssb.toString())),
                            Map.entry("target", toJs(targetId))
                    ));
        }
        super.sendHtml(response);
    }
}
