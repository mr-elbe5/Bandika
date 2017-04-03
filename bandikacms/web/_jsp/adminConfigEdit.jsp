<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
	<form action="/_admin" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>" enctype="multipart/form-data">
		<input type="hidden" name="method" value=""/>
    <div class="adminTopHeader"><%=Strings.getHtml("configuration")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <bnd:adminFileInput label="<%=Strings.getHtml(\"zipfile\")%>" name="zipFile" />
    </bnd:adminTable>
    <div class="adminTableButtonArea">
      <button	onclick="return submitMethod('replaceStylePack');"><%=Strings.getHtml("replaceStylePack")%></button>
      <button	onclick="return submitMethod('updateTemplates');"><%=Strings.getHtml("updateTemplates")%></button>
      <button	onclick="return submitMethod('replaceTemplates');"><%=Strings.getHtml("replaceTemplates")%></button>
		</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=Strings.getHtml("back")%></button>
    </div>
	</form>
