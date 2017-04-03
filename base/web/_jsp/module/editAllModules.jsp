<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika.module.ModuleData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.module.ModuleCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  ArrayList<ModuleData> modules = ModuleCache.getInstance().getModules();
  ModuleData data;
%>
<script type="text/javascript">
  var moduleName = "";
</script>
<div class="well">
  <legend><%=StringCache.getHtml("modules")%>
  </legend>
  <bandika:dataTable id="moduleTable" headerKeys="name,dependencies,_,_">
    <%
      if (modules != null) {
        for (ModuleData module : modules) {
          data = module; %>
    <tr>
      <td><%=FormatHelper.toHtml(data.getName())%>
      </td>
      <td><%=FormatHelper.toHtml(data.getDependencies())%>
      </td>
      <td>
        <button onclick="moduleName='<%=data.getName()%>';$('#moduleLog').modal();return false;"><%=StringCache.getHtml("installLog")%>
        </button>
      </td>
      <td><% if (!data.getName().equals(ModuleData.BASE_MODULE_NAME)) {%>
        <button onclick="return linkTo('/_module?method=openUninstallModule&moduleName=<%=FormatHelper.toHtml(data.getName())%>');"><%=StringCache.getHtml("uninstall")%>
        </button>
        <%}%></td>
    </tr>
    <%
        }
      }
    %>
  </bandika:dataTable>
</div>
<div class="btn-toolbar">
  <button class="btn btn-primary" onclick="return linkTo('/_module?method=openUploadModule');"><%=StringCache.getHtml("uploadModule")%>
  </button>
  <button class="btn btn-primary" onclick="return linkTo('/_application?method=rewriteWebXml');"><%=StringCache.getHtml("rewriteWebXml")%>
  </button>
  <button class="btn btn-primary" onclick="return linkTo('/_application?method=restartApplication');"><%=StringCache.getHtml("restartApplication")%>
  </button>
</div>

<div id="moduleLog" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("moduleLog")%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    <legend><%=StringCache.getHtml("moduleLog")%>
    </legend>
  </div>
  <div class="modal-body">
  </div>
</div>

<script type="text/javascript">
  $(function () {
    $("#moduleLog").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#moduleLog').on('show', function () {
    $("#moduleLog").find($(".modal-body")).load("/_module?method=showInstallLog&moduleName=" + moduleName);
  });
</script>

