<%--
	Bandika! - A Java based Content Management System
	Copyright (C) 2009-2011 Michael Roennau

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
	You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

	Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="de.bandika.admin.TemplateData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.admin.AdminController" %>
<%@ page import="de.bandika.base.Bean" %>
<%@ page import="de.bandika.admin.AdminBean" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  AdminBean tb = (AdminBean) Bean.getBean(AdminController.KEY_ADMIN);
  ArrayList<TemplateData> templates = tb.getTemplateList();
%>
			<div id="templateSelect" class="fullPageLayerContent">
				<div class="innerLayer">
          <div class="layerHeader">
            <div class="layerHeadertext">
              <%=AdminStrings.selectTemplate%>
            </div>
            <img src="/_statics/images/del.gif" alt="" class="layerCloseImg" onclick="return showLayer('templateSelect','none');" />
          </div>
          <div class="layerContent">
            <bnd:adminTable>
              <tr class="adminHeader">
                <td class="adminSmallCol"><%=AdminStrings.name%></td>
                <td class="adminMostCol"><%=AdminStrings.description%></td>
              </tr>
              <% boolean otherLine = false;
                for (int i = 0; i < templates.size(); i++) {
                  TemplateData tdata = templates.get(i);
                  otherLine = !otherLine;%>
                <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
                  <td><a href="#" onclick="addParagraph('<%=tdata.getName()%>');"><%=Formatter.toHtml(tdata.getName())%></a>
                  </td>
                  <td><%=Formatter.toHtml(tdata.getDescription())%>
                  </td>
                </tr>
              <%}%>
            </bnd:adminTable>
          </div>
        </div>
			</div>

