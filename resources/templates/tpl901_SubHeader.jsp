<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.content.ParagraphData" %>
<%@ page import="de.net25.http.SessionData" %>
<%
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
  boolean editMode = rdata.getParamBoolean("editMode");
  String[][] fieldDescriptions = {{"header", "textline"}};
  pdata.ensureFields(fieldDescriptions);
%>
<tr>
  <td colspan="7" class="c_subheader"><%=pdata.getFieldHtml("header", sdata.getLocale(), editMode)%>
  </td>
</tr>
<tr><td colspan="7" class="spacer10">&nbsp;</td></tr>
