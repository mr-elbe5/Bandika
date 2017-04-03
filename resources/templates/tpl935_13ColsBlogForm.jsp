<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.content.ParagraphData" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.content.ContentData" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.content.fields.BlogEntry" %>
<%@ page import="de.net25.content.fields.BlogField" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
  boolean editView = rdata.getParamBoolean("editView");
  boolean editMode = rdata.getParamBoolean("editMode");
  String[][] fieldDescriptions = {{"blog", "blog"}};
  pdata.ensureFields(fieldDescriptions);
  BlogField blogField = (BlogField) pdata.getFields().get("blog");
  SimpleDateFormat dateFormat = new SimpleDateFormat(Strings.getString("dateTimePattern", sdata.getLocale()));
  if (!editView) {
    ContentData data = (ContentData) rdata.getParam("contentData");
    BlogEntry newEntry = (BlogEntry) rdata.getParam("newEntry");
    for (BlogEntry entry : blogField.getEntries()) {
%>
<div>&nbsp;</div>
<tr>
  <td class="c_1column"><span class="c_subheader"><%=Formatter.toHtml(entry.getName())%></span><br>
    <%=dateFormat.format(entry.getTime())%><br>
    <a href="#newEntry"><%=Strings.getHtml("answer", sdata.getLocale())%>
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
  <td colspan="5" class="c_3column"><span class="c_subheader"><%=Strings.getHtml("answer", sdata.getLocale())%></span>
  </td>
</tr>
<form action="srv25" method="post" name="blogform<%=pdata.getId()%>" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_COMMUNICATION%>"/>
  <input type="hidden" name="method" value="addBlogEntry"/>
  <input type="hidden" name="pid" value="<%=pdata.getId()%>"/>
  <input type="hidden" name="fieldName" value="blog"/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>
  <tr>
    <td class="c_1column">&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><%=pdata.getFieldHtml("blog", sdata.getLocale(), editMode)%>
    </td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("name", sdata.getLocale())%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="blogform_name"
                                             value="<%=newEntry==null ? "" : Formatter.toHtml(newEntry.getName())%>"/>
    </td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("email", sdata.getLocale())%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="blogform_email"
                                             value="<%=newEntry==null ? "" : Formatter.toHtml(newEntry.getEmail())%>"/>
    </td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("text", sdata.getLocale())%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><textarea name="blogform_text" rows="5"
                                                cols="60"><%=newEntry == null ? "" : Formatter.toHtmlInput(newEntry.getText())%>
    </textarea></td>
  </tr>
  <tr>
    <td class="c_1column">&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column">
      <ul class="pageFormButtonList">
        <li class="userFormButton"><a href="#"
                                      onClick="document.blogform<%=pdata.getId()%>.submit();"><%=Strings.getHtml("send", sdata.getLocale())%>
        </a></li>
      </ul>
    </td>
  </tr>
</form>
<%
} else {
  ContentData data = (ContentData) sdata.getParam("contentData");
  for (int i = 0; i < blogField.getEntries().size(); i++) {
    BlogEntry entry = blogField.getEntries().get(i);
%>
<tr>
  <td class="c_1column"><span class="c_subheader"><%=Formatter.toHtml(entry.getName())%></span><br>
    <%=dateFormat.format(entry.getTime())%>
    <%if (editMode) {%><br>
    <a href="srv25?ctrl=<%=Statics.KEY_COMMUNICATION%>&method=removeBlogEntry&id=<%=data.getId()%>&pid=<%=pdata.getId()%>&fieldName=blog&entryIdx=<%=i%>"><%=Strings.getHtml("remove", sdata.getLocale())%>
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
