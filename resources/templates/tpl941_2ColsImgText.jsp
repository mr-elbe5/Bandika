<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.content.ParagraphData" %>
<%@ page import="de.net25.http.SessionData" %>
<%
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
  boolean editMode = rdata.getParamBoolean("editMode");
  String[][] fieldDescriptions = {{"image", "image"}, {"text", "textarea"}};
  pdata.ensureFields(fieldDescriptions);
%>
<tr>
  <td colspan="3" class="c_2column"><%=pdata.getFieldHtml("image", sdata.getLocale(), editMode)%>
  </td>
  <td>&nbsp;</td>
  <td colspan="3" class="c_2column"><%=pdata.getFieldHtml("text", sdata.getLocale(), editMode)%>
  </td>
</tr>
