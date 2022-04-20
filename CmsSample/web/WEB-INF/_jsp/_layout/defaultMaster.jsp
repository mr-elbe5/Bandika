<%--
  Bandika CMS - A Java based modular Content Management System
  Copyright (C) 2009-2021 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.application.Configuration" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.request.RequestKeys" %>
<%@ page import="de.elbe5.request.ContentRequestKeys" %>
<%@ page import="de.elbe5.response.IMasterInclude" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    IMasterInclude masterInclude = rdata.getRequestObject(RequestKeys.KEY_MASTERINCLUDE, IMasterInclude.class);
    ContentData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
    List<Integer> parentIds = ContentCache.getParentContentIds(contentData);
    String title = rdata.getString(RequestKeys.KEY_TITLE, Configuration.getAppTitle()) + (contentData!=null ? " | " + contentData.getDisplayName() : "");
    String keywords=contentData!=null ? contentData.getKeywords() : title;
    String description=contentData!=null ? contentData.getDescription() : "";
%>
<!DOCTYPE html>
<html lang="<%=Configuration.getLocale().getLanguage()%>">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <title><%=$H(title)%></title>
    <meta name="keywords" content="<%=$H(keywords)%>">
    <meta name="description" content="<%=$H(description)%>">
    <link rel="shortcut icon" href="/favicon.ico"/>
    <link rel="stylesheet" href="/static-content/css/bootstrap.css"/>
    <link rel="stylesheet" href="/static-content/css/bandika.css"/>
    <link rel="stylesheet" href="/static-content/css/layout.css"/>
    <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.tree.js"></script>
    <script type="text/javascript" src="/static-content/ckeditor/ckeditor.js"></script>
    <script>
        CKEDITOR.config.language = '<%=Configuration.getLocale().getLanguage()%>';
    </script>
    <script type="text/javascript" src="/static-content/ckeditor/adapters/jquery.js"></script>
    <script type="text/javascript" src="/static-content/js/bandika-webbase.js"></script>
</head>
<body>
    <div class="container">
        <header>
            <section class="sysnav">
                <jsp:include page="/WEB-INF/_jsp/_include/_sysnav.inc.jsp" flush="true"/>
            </section>
            <div class="menu row">
                <section class="col-12 menu">
                    <nav class="navbar navbar-expand-lg navbar-light">
                        <a class="navbar-brand" href="/"><img src="/static-content/img/logo-light.png" alt=""/></a>
                        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                            <span class="fa fa-bars"></span>
                        </button>
                        <div class="collapse navbar-collapse" id="navbarSupportedContent">
                            <ul class="navbar-nav mr-auto">
                                <jsp:include page="/WEB-INF/_jsp/_include/_mainnav.inc.jsp" flush="true"/>
                            </ul>
                        </div>
                    </nav>
                </section>
            </div>
            <div class="bc row">
                <section class="col-12">
                    <ol class="breadcrumb">
                            <%for (int i = parentIds.size() - 1; i >= 0; i--) {
                            ContentData content = ContentCache.getContent(parentIds.get(i));
                            if (content != null) {%>
                        <li class="breadcrumb-item">
                            <a href="<%=content.getUrl()%>"><%=$H(content.getDisplayName())%>
                            </a>
                        </li>
                            <%}}%>
                    </ol>
                </section>
            </div>
        </header>
        <main id="main" role="main">
            <div id="pageContainer">
                <% if (masterInclude!=null){
                    try {
                        masterInclude.displayContent(pageContext, rdata);
                    } catch (Exception ignore) {
                    }
                }%>
            </div>
        </main>
    </div>
    <div class="container fixed-bottom">
        <footer class="footer">
            <ul class="nav">
                <li class="nav-item">
                    <a class="nav-link">&copy; <%=$SH("layout.copyright")%>
                    </a>
                </li>
                <% for (ContentData data : ContentCache.getFooterList()) {
                    if (data.hasUserReadRight(rdata)) {%>
                <li class="nav-item">
                    <a class="nav-link" href="<%=data.getUrl()%>"><%=$H(data.getDisplayName())%>
                    </a>
                </li>
                <%}
                }%>
            </ul>
        </footer>
    </div>
<div class="modal" id="modalDialog" tabindex="-1" role="dialog"></div>
</body>
</html>
