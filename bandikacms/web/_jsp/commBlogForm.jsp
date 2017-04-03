<%@ page import="de.bandika.page.ParagraphData" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="de.bandika.page.fields.BlogField" %>
<%@ page import="de.bandika.page.fields.BlogEntry" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.RequestData" %>
<%@ page import="de.bandika.data.SessionData" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  ParagraphData pdata = (ParagraphData) rdata.getParam("pdata");
  boolean editView = rdata.getParamBoolean("editView");
  boolean editMode = rdata.getParamBoolean("editMode");
  BlogField blogField = (BlogField) pdata.getFields().get("blog");
  SimpleDateFormat dateFormat = new SimpleDateFormat(Strings.getHtml("datetimepattern"));
  if (!editView) {
    PageData data = (PageData) sdata.getParam("pageData");
    BlogEntry newEntry = (BlogEntry) rdata.getParam("newEntry");
    for (BlogEntry entry : blogField.getEntries()) {
%>
<div>&nbsp;</div>
<tr>
  <td class="c_1column"><span class="c_subheader"><%=FormatHelper.toHtml(entry.getName())%></span><br>
    <%=dateFormat.format(entry.getTime())%><br>
    <a href="#newEntry"><%=Strings.getHtml("answer")%>
    </a>
  </td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><%=FormatHelper.toHtml(entry.getText())%>
  </td>
</tr>
<tr><td colspan="7" class="spacer10">&nbsp;</td></tr>
<%}%>
<tr><td colspan="7" class="spacer20">&nbsp;</td></tr>
<tr>
  <td class="c_1column"><a name="newEntry">&nbsp;</a></td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><span class="c_subheader"><%=Strings.getHtml("answer")%></span>
  </td>
</tr>
<form action="/_comm" method="post" name="blogform<%=pdata.getId()%>" accept-charset="<%=RequestHelper.ISOCODE%>">
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
    <td class="c_1column"><%=Strings.getHtml("name")%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="blogform_name"
                                             value="<%=newEntry==null ? "" : FormatHelper.toHtml(newEntry.getName())%>"/>
    </td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("email")%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><input type="text" name="blogform_email"
                                             value="<%=newEntry==null ? "" : FormatHelper.toHtml(newEntry.getEmail())%>"/>
    </td>
  </tr>
  <tr>
    <td class="c_1column"><%=Strings.getHtml("text")%>*</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column"><textarea name="blogform_text" rows="5"
                                                cols="60"><%=newEntry == null ? "" : FormatHelper.toHtmlInput(newEntry.getText())%>
    </textarea></td>
  </tr>
  <tr>
    <td class="c_1column">&nbsp;</td>
    <td>&nbsp;</td>
    <td colspan="5" class="c_3column">
      <div class="tableButtonArea">
        <button	onclick="document.blogform<%=pdata.getId()%>.submit();"><%=Strings.getHtml("send")%></button>
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
  <td class="c_1column"><span class="c_subheader"><%=FormatHelper.toHtml(entry.getName())%></span><br>
    <%=dateFormat.format(entry.getTime())%>
    <%if (editMode) {%><br>
    <a href="/_comm?method=removeBlogEntry&pid=<%=pdata.getId()%>&fieldName=blog&entryIdx=<%=i%>&id=<%=data.getId()%>"><%=Strings.getHtml("remove")%>
    </a>
    <%}%>
  </td>
  <td>&nbsp;</td>
  <td colspan="5" class="c_3column"><%=FormatHelper.toHtml(entry.getText())%>
  </td>
</tr>
<tr>
  <td colspan="7">&nbsp;</td>
</tr>
<%
    }
  }
%>
