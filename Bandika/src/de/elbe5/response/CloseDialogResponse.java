/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.base.StringFormatter;
import de.elbe5.base.StringHelper;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class CloseDialogResponse extends HtmlResponse {

    String url;
    String msg = "";
    String msgType = "";
    private String targetId = "";

    static final String dialogHtmlStart = """
            <div id="pageContent">
                <form action="{1}" method="POST" id="forwardform" accept-charset="UTF-8">
                """;
    static final String dialogMsg = """
                    <input type="hidden" name="message" value="{msg}"/>
                    <input type="hidden" name="messageType" value="{msgType}"/>
                    """;
    static final String dialogHtmlEnd = """
                </form>
            </div>
            <script type="text/javascript">
                $('#forwardform').submit();
            </script>
            """;

    static final String targetedHtml = """
            <div id="pageContent"></div>
            <script type="text/javascript">
                    let $dlg = $(MODAL_DLG_JQID);
                    $dlg.html('');
                    $dlg.modal('hide');
                    $('.modal-backdrop').remove();
                    postByAjax('{1}', {2}, '{3}');
            </script>
            """;

    public CloseDialogResponse(String url){
        this.url = url;
    }

    public CloseDialogResponse(String url, String msg, String msgType){
        this.url = url;
        this.msg = msg;
        this.msgType = msgType;
    }
    public CloseDialogResponse(String url, String msg, String msgType, String targetId){
        this.url = url;
        this.msg = msg;
        this.msgType = msgType;
        this.targetId = targetId;
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        if (targetId.isEmpty()) {
            html = StringFormatter.format(dialogHtmlStart,
                    url);
            if (!msg.isEmpty()) {
                html += StringFormatter.format(dialogMsg,
                        StringHelper.toHtml(msg),
                        msgType);
            }
            html += dialogHtmlEnd;
        }
        else{
            StringBuilder sb = new StringBuilder("{");
            if (!msg.isEmpty()) {
                sb.append(RequestKeys.KEY_MESSAGE).append(" : '").append(StringHelper.toJs(msg)).append("',");
                sb.append(RequestKeys.KEY_MESSAGETYPE).append(" : '").append(StringHelper.toJs(msgType)).append("'");
            }
            sb.append("}");
            html = StringFormatter.format(targetedHtml,
                    url,
                    sb.toString(),
                    StringHelper.toJs(targetId));
        }
        super.sendHtml(response);
    }
}
