<%@ page import="de.bandika.page.ParagraphData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.communication.CommunicationController" %>
<%@ page import="de.bandika.page.fields.MailField" %>
<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.RequestData" %>
<%@ page import="de.bandika.data.SessionData" %>
<%
  RequestData rdata= RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
	MailField mailField = (MailField) pdata.getFields().get("mail");
  boolean editView = rdata.getParamBoolean("editView");
%>
<% if (!editView) {
  PageData data = (PageData) sdata.getParam("pageData");
%>
<form action="/_comm" method="post" name="mailform<%=pdata.getId()%>" accept-charset="<%=RequestHelper.ISOCODE%>">
  <input type="hidden" name="method" value="sendMail"/>
  <input type="hidden" name="mailform_receiver" value="<%=FormatHelper.toHtml(AppConfig.getInstance().getConfig(CommunicationController.MAIL_RECEIVER))%>"/>
	<input type="hidden" name="id" value="<%=data.getId()%>"/>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("youremail")%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="mailform_email"/></td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("subject")%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="mailform_subject"/></td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("yourtext")%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><textarea name="mailform_text" rows="5" cols="60"></textarea></td>
  </tr>
  <tr>
    <td class="c_1column">&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column">
      <div class="tableButtonArea">
        <button	onclick="document.mailform<%=pdata.getId()%>.submit();"><%=Strings.getHtml("send")%></button>
      </div>
    </td>
  </tr>
</form>
<%} else {%>
<tr>
  <td class="c_1column">&nbsp;</td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column">[<%=Strings.getHtml("mailform")%>]</td>
</tr>
<tr>
  <td class="c_1column"><%=Strings.getHtml("receiver")%>
  </td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><%=FormatHelper.toHtml(mailField.getReceiver())%>
  </td>
</tr>
<%}%>
