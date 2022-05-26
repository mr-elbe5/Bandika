/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class CloseDialogResponse extends HtmlResponse {

    String url;
    String msg = "";
    String msgType = "";
    private String targetId = "";

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
            append(Strings.format("""
            <div id="pageContent">
                <form action="{1}" method="POST" id="forwardform" accept-charset="UTF-8">
                """,
                    url));
            if (!msg.isEmpty()) {
                append(Strings.format("""
                    <input type="hidden" name="message" value="{msg}"/>
                    <input type="hidden" name="messageType" value="{msgType}"/>
                    """,
                        Strings.toHtml(msg),
                        msgType));
            }
            append("""
                </form>
            </div>
            <script type="text/javascript">
                $('#forwardform').submit();
            </script>
            """);
        }
        else{
            StringBuilder ssb = new StringBuilder("{");
            if (!msg.isEmpty()) {
                ssb.append(RequestKeys.KEY_MESSAGE).append(" : '").append(Strings.toJs(msg)).append("',");
                ssb.append(RequestKeys.KEY_MESSAGETYPE).append(" : '").append(Strings.toJs(msgType)).append("'");
            }
            ssb.append("}");
            append("""
                <div id="pageContent"></div>
                <script type="text/javascript">
                        closeModalDialog();
                        postByAjax('{1}', {2}, '{3}');
                </script>
            """,
                    url,
                    ssb.toString(),
                    Strings.toJs(targetId));
        }
        super.sendHtml(response);
    }
}
