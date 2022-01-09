<%@ page import="de.elbe5.request.RequestData" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    String url = request.getRequestURL().toString();
    String uri = request.getRequestURI();
    int idx = url.lastIndexOf(uri);
    url = url.substring(0, idx);
    if (rdata!= null && rdata.isLoggedIn())
        url +="/ctrl/admin/openSystemAdministration";
    else
        url +="/ctrl/user/openLogin";
%>
<html>
<head>
    <title>Title</title>
    <meta http-equiv="Refresh" content="0; url='<%=url%>'" />
</head>
<body>
</body>
</html>
