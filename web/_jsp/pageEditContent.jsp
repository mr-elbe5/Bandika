<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.http.StdServlet" %>
<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.page.ParagraphData" %>
<%@ page import="de.bandika.admin.TemplateData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.base.Bean" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ page import="de.bandika.admin.AdminBean" %>
<%@ page import="de.bandika.admin.AdminController" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  RequestData rdata=HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  PageData data = (PageData) sdata.getParam("pageData");
  ParagraphData editParagraph = data.getEditParagraph();
  rdata.setParam("editView", "1");
%>
<form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=PageController.KEY_PAGE%>"/>
  <input type="hidden" name="method" value="save"/>
  <input type="hidden" name="editType" value="<%=PageController.EDIT_CONTENT%>"/>
  <input type="hidden" name="idx" value="0"/>
  <input type="hidden" name="template" value=""/>
	<input type="hidden" name="id" value="<%=data.getId()%>"/>

    <%
      int idx = 0;
      for (ParagraphData pdata : data.getParagraphs()) {
        if (pdata==null)
          continue;
        rdata.setParam("pdata", pdata);
        rdata.setParam("editMode", pdata == editParagraph ? "1" : "0");
    %>
    <div>
     <% if (pdata == editParagraph) {%>
      <div id="editToolsAccept" class="editToolsAccept">
        <% if (!pdata.getTemplateName().equals("")) {%>
        <a href="#" class="editBtn" onclick="saveParagraph(<%=idx%>);"><%=AdminStrings.ok%>
        </a>
        <%}%>
        <a href="#" class="editBtn" onclick="submitMethod('cancelEditParagraph');"><%=AdminStrings.cancel%>
        </a>
      </div>
      <div class="editline">&nbsp;</div>
      <%
      } else if (editParagraph == null) {%>
        <bnd:editTools idx="<%=idx%>" id="<%=data.getId()%>" />
      <%}%>
    </div>
    <jsp:include page="<%=pdata.getTemplateUrl()%>" flush="true"/>
    <%if (pdata == editParagraph) {%>
    <div>
      <div class="editline">&nbsp;</div>
    </div>
    <%}
      idx++;}%>
    <% if (editParagraph == null) {%>
    <div>
      <bnd:editTools idx="-1" id="<%=data.getId()%>" endTag="true" />
    </div>
    <%}%>
  <div class="adminTableButtonArea">
    <button	onclick="return linkTo('/index.jsp?id=<%=data.getId()%>');"><%=AdminStrings.back%></button>
    <button	onclick="return submitMethod('switchMetaData');"><%=AdminStrings.metadata%></button>
    <button	onclick="return submitMethod('switchParent');"><%=AdminStrings.parentmenu%></button>
    <button	onclick="return submitMethod('save');"><%=AdminStrings.save%></button>
  </div>
  <% if (editParagraph != null) {%>
  <script type="text/javascript">
    window.location.hash = 'editToolsAccept';
  </script>
  <%}else{%>
  <jsp:include page="/_jsp/templateSelect.jsp"/>
  <%}%>
</form>
</bnd:setMaster>
