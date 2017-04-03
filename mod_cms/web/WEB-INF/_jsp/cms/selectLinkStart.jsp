<%@ page import="de.bandika.cms.CmsPartData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.cms.LinkField" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  CmsPartData pdata = (CmsPartData) rdata.get("pagePartData");
  String fieldName=rdata.getString("fieldName");
  String className=rdata.getString("className");
  LinkField field = (LinkField) pdata.getField(fieldName);
%>
<div>
  <input type="hidden" id="<%=field.getIdentifier()%>Link" name="<%=field.getIdentifier()%>Link" value="<%=StringFormat.toHtml(field.getLink())%>" />
  <input type="hidden" id="<%=field.getIdentifier()%>Target" name="<%=field.getIdentifier()%>Target" value="<%=StringFormat.toHtml(field.getTarget())%>" />
  <a href="#" class="editField <%=StringFormat.isNullOrEmtpy(className) ? "" : className%>" onclick="$('#selectLink<%=field.getIdentifier()%>').modal();return false;" id="<%=field.getIdentifier()%>">

