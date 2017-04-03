<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.content.ParagraphData" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.content.ContentData" %>
<%@ page import="de.net25.base.Formatter" %>
<%
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
  boolean editView = rdata.getParamBoolean("editView");
  boolean editMode = rdata.getParamBoolean("editMode");
  String[][] fieldDescriptions = {{"receiver", "textline"}};
  pdata.ensureFields(fieldDescriptions);
%>
<% if (!editView) {
  ContentData data = (ContentData) rdata.getParam("contentData");
%>
<form action="srv25" method="post" name="mailform<%=pdata.getId()%>" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_COMMUNICATION%>"/>
  <input type="hidden" name="method" value="sendMail"/>
  <input type="hidden" name="mailform_receiver" value="<%=Formatter.toHtml(Statics.MAIL_ADDRESS)%>"/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("youremail", sdata.getLocale())%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="mailform_email"/></td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("subject", sdata.getLocale())%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="mailform_subject"/></td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("yourtext", sdata.getLocale())%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><textarea name="mailform_text" rows="5" cols="60"></textarea></td>
  </tr>
  <tr>
    <td class="c_1column">&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column">
      <ul class="pageFormButtonList">
        <li class="userFormButton"><a href="#"
                                      onClick="document.mailform<%=pdata.getId()%>.submit();"><%=Strings.getHtml("send", sdata.getLocale())%>
        </a></li>
      </ul>
    </td>
  </tr>
</form>
<%} else {%>
<tr>
  <td class="c_1column">&nbsp;</td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column">[<%=Strings.getHtml("mailForm", sdata.getLocale())%>]</td>
</tr>
<tr>
  <td class="c_1column"><%=Strings.getHtml("receiver", sdata.getLocale())%>
  </td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><%=pdata.getFieldHtml("receiver", sdata.getLocale(), editMode)%>
  </td>
</tr>
<%}%>
