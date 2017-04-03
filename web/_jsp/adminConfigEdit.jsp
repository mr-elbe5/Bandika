<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="de.bandika.base.BaseConfig" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.admin.AdminController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
	<form action="/_jsp/adminConfigEdit.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>" enctype="multipart/form-data">
		<input type="hidden" name="ctrl" value="<%=AdminController.KEY_ADMIN%>"/>
		<input type="hidden" name="method" value="saveConfig"/>
    <div class="adminTopHeader"><%=AdminStrings.configuration%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminTextInput label="<%=AdminStrings.basepath%>" name="basePath" mandatory="true" text="<%=Formatter.toHtml(BaseConfig.getBasePath())%>" maxlength="255" />
      <bnd:adminFileInput label="<%=AdminStrings.zipfile%>" name="zipFile" />
    </bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=AdminStrings.back%></button>
      <button	onclick="return submitMethod('saveConfig');"><%=AdminStrings.save%></button>
    </div>
    <div class="adminTableButtonArea">
      <button	onclick="return submitMethod('replaceStylePack');"><%=AdminStrings.replaceStylePack%></button>
      <button	onclick="return submitMethod('updateTemplates');"><%=AdminStrings.updateTemplates%></button>
      <button	onclick="return submitMethod('replaceTemplates');"><%=AdminStrings.replaceTemplates%></button>
		</div>
	</form>
</bnd:setMaster>
