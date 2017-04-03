<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.http.StdServlet" %>
<%@ page import="de.bandika.page.ParagraphData" %>
<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.communication.CommunicationController" %>
<%@ page import="de.bandika.page.fields.MailField" %>
<%@ page import="de.bandika.base.*" %>
<%
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
	MailField mailField = (MailField) pdata.getFields().get("mail");
  boolean editView = rdata.getParamBoolean("editView");
%>
<% if (!editView) {
  PageData data = (PageData) sdata.getParam("pageData");
%>
<form action="/index.jsp" method="post" name="mailform<%=pdata.getId()%>" accept-charset="<%=HttpHelper.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=CommunicationController.KEY_COMMUNICATION%>"/>
  <input type="hidden" name="method" value="sendMail"/>
  <input type="hidden" name="mailform_receiver" value="<%=Formatter.toHtml(BaseConfig.getConfig(CommunicationController.MAIL_RECEIVER))%>"/>
	<input type="hidden" name="id" value="<%=data.getId()%>"/>
  <tr>
    <td class="c_1column"><%=UserStrings.youremail%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="mailform_email"/></td>
  </tr>
  <tr>
    <td class="c_1column"><%=UserStrings.subject%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="mailform_subject"/></td>
  </tr>
  <tr>
    <td class="c_1column"><%=UserStrings.yourtext%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><textarea name="mailform_text" rows="5" cols="60"></textarea></td>
  </tr>
  <tr>
    <td class="c_1column">&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column">
      <div class="tableButtonArea">
        <button	onclick="document.mailform<%=pdata.getId()%>.submit();"><%=UserStrings.send%></button>
      </div>
    </td>
  </tr>
</form>
<%} else {%>
<tr>
  <td class="c_1column">&nbsp;</td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column">[<%=UserStrings.mailform%>]</td>
</tr>
<tr>
  <td class="c_1column"><%=UserStrings.receiver%>
  </td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><%=Formatter.toHtml(mailField.getReceiver())%>
  </td>
</tr>
<%}%>
