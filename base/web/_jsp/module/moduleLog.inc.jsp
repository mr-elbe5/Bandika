<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika.module.ModuleData" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  ModuleData data = (ModuleData) rdata.getParam("moduleData");
%>
<div class="layerContent">
  <%=FormatHelper.toHtml(data.getInstallLog())%>
</div>
