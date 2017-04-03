<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.admin.AdminController" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ page import="de.bandika.admin.CacheData" %>
<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.document.DocumentController" %>
<%@ page import="de.bandika.base.DataCache" %>
<%@ page import="de.bandika.image.ImageController" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
  SessionData sdata= HttpHelper.getSessionData(request);
  CacheData data = (CacheData) sdata.getParam("cacheData");
%>
	<form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=AdminController.KEY_ADMIN%>"/>
		<input type="hidden" name="method" value=""/>

    <div class="adminTopHeader"><%=AdminStrings.caches%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminGreyLine">
        <td class="adminLeft"><%=AdminStrings.documentcachesize%>, <%=AdminStrings.used%></td>
        <td class="adminRight"><%=DataCache.getCache(DocumentController.KEY_DOCUMENT).getCacheCount()%></td>
      </tr>
      <bnd:adminTextInput label="<%=AdminStrings.documentcachesize%>" name="documentCacheSize" text="<%=Integer.toString(data.getDocumentCacheSize())%>" maxlength="20" />
      <tr class="adminGreyLine">
        <td class="adminLeft"><%=AdminStrings.imagecachesize%>, <%=AdminStrings.used%></td>
        <td class="adminRight"><%=DataCache.getCache(ImageController.KEY_IMAGE).getCacheCount()%></td>
      </tr>
      <bnd:adminTextInput label="<%=AdminStrings.imagecachesize%>" name="imageCacheSize" text="<%=Integer.toString(data.getImageCacheSize())%>" maxlength="20" />
    </bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=AdminStrings.back%></button>
      <button	onclick="return submitMethod('saveCaches');"><%=AdminStrings.accept%></button>
		</div>
	</form>
</bnd:setMaster>
