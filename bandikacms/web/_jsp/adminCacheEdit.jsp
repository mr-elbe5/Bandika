<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.admin.CacheData" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.document.DocumentBean" %>
<%@ page import="de.bandika.image.ImageBean" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  SessionData sdata= RequestHelper.getSessionData(request);
  CacheData data = (CacheData) sdata.getParam("cacheData");
%>
	<form action="/_admin" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=Strings.getHtml("caches")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminGreyLine">
        <td class="adminLeft"><%=Strings.getHtml("documentcachesize")%>, <%=Strings.getHtml("used")%></td>
        <td class="adminRight"><%=DocumentBean.getInstance().getCache().getCacheCount()%></td>
      </tr>
      <bnd:adminTextInput label="<%=Strings.getHtml(\"documentcachesize\")%>" name="documentCacheSize" text="<%=Integer.toString(data.getDocumentCacheSize())%>" maxlength="20" />
      <tr class="adminGreyLine">
        <td class="adminLeft"><%=Strings.getHtml("imagecachesize")%>, <%=Strings.getHtml("used")%></td>
        <td class="adminRight"><%=ImageBean.getInstance().getCache().getCacheCount()%></td>
      </tr>
      <bnd:adminTextInput label="<%=Strings.getHtml(\"imagecachesize\")%>" name="imageCacheSize" text="<%=Integer.toString(data.getImageCacheSize())%>" maxlength="20" />
    </bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('saveCaches');"><%=Strings.getHtml("accept")%></button>
		</div>
	</form>
