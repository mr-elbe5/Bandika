<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.group.GroupData" %>
<%@ page import="de.bandika.webbase.rights.Right" %>
<%@ page import="de.bandika.webbase.rights.SystemZone" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    GroupData group = (GroupData) SessionReader.getSessionObject(request, "groupData");
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/group.ajx" method="post" id="groupform" name="groupform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="saveGroup"/>
    <fieldset>
        <table class="padded form">
            <tr>
                <td><label><%=StringUtil.getHtml("_id", locale)%>
                </label></td>
                <td>
          <span><%=Integer.toString(group.getId())%>
          </span>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="name"><%=StringUtil.getHtml("_name", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="name" name="name" value="<%=StringUtil.toHtml(group.getName())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="notes"><%=StringUtil.getHtml("_notes", locale)%>
                    </label></td>
                <td><textarea id="notes" name="notes" rows="5"><%=StringUtil.toHtmlInput(group.getNotes())%></textarea>
                </td>
            </tr>
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("_rights", locale)%>
                    </label></td>
                <td>&nbsp;
                </td>
            </tr>
            <tr class="formTableHeader">
                <th><%=StringUtil.getHtml("_zone", locale)%>
                </th>
                <th><%=StringUtil.getHtml("_rights", locale)%>
                </th>
            </tr>
            <%for (SystemZone zone : SystemZone.values()) {%>
            <tr>
                <td>
                    <label for="zoneright_<%=zone.name()%>"><%=StringUtil.toHtml(zone.name())%>
                    </label>
                </td>
                <td>
                    <select class="fullWidth" id="zoneright_<%=zone.name()%>" name="zoneright_<%=zone.name()%>">
                        <option value="" <%=!group.getRights().hasAnySystemRight(zone) ? "selected" : ""%>><%=StringUtil.getHtml("_rightnone", locale)%>
                        </option>
                        <option value="<%=Right.READ.name()%>" <%=group.getRights().isSystemRight(zone, Right.READ) ? "selected" : ""%>><%=StringUtil.getHtml("_rightread", locale)%>
                        </option>
                        <option value="<%=Right.EDIT.name()%>" <%=group.getRights().isSystemRight(zone, Right.EDIT) ? "selected" : ""%>><%=StringUtil.getHtml("_rightedit", locale)%>
                        </option>
                        <option value="<%=Right.APPROVE.name()%>" <%=group.getRights().isSystemRight(zone, Right.APPROVE) ? "selected" : ""%>><%=StringUtil.getHtml("_rightapprove", locale)%>
                        </option>
                    </select>
                </td>
            </tr>
            <%}%>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#groupform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/group.ajx', params);
    });
</script>

