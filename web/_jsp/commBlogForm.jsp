<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.http.StdServlet" %>
<%@ page import="de.bandika.page.ParagraphData" %>
<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.page.fields.BlogField" %>
<%@ page import="de.bandika.page.fields.BlogEntry" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.communication.CommunicationController" %>
<%@ page import="de.bandika.base.UserStrings" %>
<%
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
  boolean editView = rdata.getParamBoolean("editView");
  boolean editMode = rdata.getParamBoolean("editMode");
  BlogField blogField = (BlogField) pdata.getFields().get("blog");
  SimpleDateFormat dateFormat = new SimpleDateFormat(UserStrings.datetimepattern);
  if (!editView) {
    PageData data = (PageData) sdata.getParam("pageData");
    BlogEntry newEntry = (BlogEntry) rdata.getParam("newEntry");
    for (BlogEntry entry : blogField.getEntries()) {
%>
<div>&nbsp;</div>
<tr>
  <td class="c_1column"><span class="c_subheader"><%=Formatter.toHtml(entry.getName())%></span><br>
    <%=dateFormat.format(entry.getTime())%><br>
    <a href="blogForm.jsp#newEntry"><%=UserStrings.answer%>
    </a>
  </td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><%=Formatter.toHtml(entry.getText())%>
  </td>
</tr>
<tr><td colspan="7" class="spacer10">&nbsp;</td></tr>
<%}%>
<tr><td colspan="7" class="spacer20">&nbsp;</td></tr>
<tr>
  <td class="c_1column"><a name="newEntry">&nbsp;</a></td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><span class="c_subheader"><%=UserStrings.answer%></span>
  </td>
</tr>
<form action="/index.jsp" method="post" name="blogform<%=pdata.getId()%>" accept-charset="<%=HttpHelper.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=CommunicationController.KEY_COMMUNICATION%>"/>
  <input type="hidden" name="method" value="addBlogEntry"/>
  <input type="hidden" name="pid" value="<%=pdata.getId()%>"/>
  <input type="hidden" name="fieldName" value="blog"/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>
  <tr>
    <td class="c_1column">&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><%=blogField.getName()%>
    </td>
  </tr>
  <tr>
    <td class="c_1column"><%=UserStrings.name%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="blogform_name"
                                             value="<%=newEntry==null ? "" : Formatter.toHtml(newEntry.getName())%>"/>
    </td>
  </tr>
  <tr>
    <td class="c_1column"><%=UserStrings.email%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="blogform_email"
                                             value="<%=newEntry==null ? "" : Formatter.toHtml(newEntry.getEmail())%>"/>
    </td>
  </tr>
  <tr>
    <td class="c_1column"><%=UserStrings.text%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><textarea name="blogform_text" rows="5"
                                                cols="60"><%=newEntry == null ? "" : Formatter.toHtmlInput(newEntry.getText())%>
    </textarea></td>
  </tr>
  <tr>
    <td class="c_1column">&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column">
      <div class="tableButtonArea">
        <button	onclick="document.blogform<%=pdata.getId()%>.submit();"><%=UserStrings.send%></button>
      </div>
    </td>
  </tr>
</form>
<%
} else {
  PageData data = (PageData) sdata.getParam("pageData");
  for (int i = 0; i < blogField.getEntries().size(); i++) {
    BlogEntry entry = blogField.getEntries().get(i);
%>
<tr>
  <td class="c_1column"><span class="c_subheader"><%=Formatter.toHtml(entry.getName())%></span><br>
    <%=dateFormat.format(entry.getTime())%>
    <%if (editMode) {%><br>
    <a href="/index.jsp?ctrl=<%=CommunicationController.KEY_COMMUNICATION%>&method=removeBlogEntry&pid=<%=pdata.getId()%>&fieldName=blog&entryIdx=<%=i%>&id=<%=data.getId()%>"><%=UserStrings.remove%>
    </a>
    <%}%>
  </td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><%=Formatter.toHtml(entry.getText())%>
  </td>
</tr>
<tr>
  <td colspan="7">&nbsp;</td>
</tr>
<%
    }
  }
%>
