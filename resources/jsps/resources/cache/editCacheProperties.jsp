<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%@ page import="de.net25.base.DataCache" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
  long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
  DataCache docCache = Statics.getCache(Statics.KEY_DOCUMENT);
  DataCache imgCache = Statics.getCache(Statics.KEY_IMAGE);
  DataCache pageCache = Statics.getCache(Statics.KEY_CONTENT);
%>
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_CACHE%>"/>
  <input type="hidden" name="method" value=""/>

  <div class="hline">&nbsp;</div>
  <div class="admin">
    <table class="adminTable">
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("maxMemory", sdata.getLocale())%>
        </td>
        <td class="adminRight"><%=maxMemory%>&nbsp;kB
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("freeMemory", sdata.getLocale())%>
        </td>
        <td class="adminRight"><%=freeMemory%>&nbsp;kB
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("docCacheSize", sdata.getLocale())%>
        </td>
        <td  class="adminRight"><input class="adminInput" type="text" name="documentCacheSize" maxlength="20"
                                          value="<%=docCache.getMaxSize()/1024%>">&nbsp;kB&nbsp;(<%=Strings.getHtml("used", sdata.getLocale())%>
          :<%=docCache.getCacheSize() / 1024%>&nbsp;kB)
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("imgCacheSize", sdata.getLocale())%>
        </td>
        <td  class="adminRight"><input class="adminInput" type="text" name="imageCacheSize" maxlength="20"
                                          value="<%=imgCache.getMaxSize()/1024%>">&nbsp;kB&nbsp;(<%=Strings.getHtml("used", sdata.getLocale())%>
          :<%=imgCache.getCacheSize() / 1024%>&nbsp;kB)
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("pageCacheSize", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input class="adminInput" type="text" name="pageCacheSize" maxlength="20"
                                          value="<%=pageCache.getMaxSize()/1024%>">&nbsp;kB&nbsp;(<%=Strings.getHtml("used", sdata.getLocale())%>
          :<%=pageCache.getCacheSize() / 1024%>&nbsp;kB)
        </td>
      </tr>
    </table>
  </div>
  <div class="hline">&nbsp;</div>
  <ul class="adminButtonList">
    <li class="adminButton"><a href="#"
                               onClick="submitMethod('setCacheSizes');"><%=Strings.getHtml("accept", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_CACHE%>&method=resetCacheSizes"><%=Strings.getHtml("reset", sdata.getLocale())%>
    </a></li>
  </ul>
</form>
