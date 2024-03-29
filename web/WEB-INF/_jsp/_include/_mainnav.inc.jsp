<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="java.util.*" %>
<%@ page import="de.elbe5.request.ContentRequestKeys" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    ContentData home = ContentCache.getContentRoot();
    ContentData currentContent = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, ContentData.class);
    if (currentContent == null)
        currentContent = home;
    if (home != null) {
        Set<Integer> activeIds = new HashSet<>();
        currentContent.collectParentIds(activeIds);
        activeIds.add(currentContent.getId());
        for (ContentData contentData : home.getChildren()) {
            if (contentData.isInHeaderNav() && contentData.hasUserReadRight(rdata)) {
                List<ContentData> children = new ArrayList<>();
                for (ContentData child : contentData.getChildren()) {
                    if (child.isInHeaderNav() && child.hasUserReadRight(rdata))
                        children.add(child);
                }
                if (!children.isEmpty()) {%>
<li class="nav-item dropdown">
    <a class="nav-link <%=activeIds.contains(contentData.getId())? "active" : ""%> dropdown-toggle" data-toggle="dropdown" href="<%=contentData.getUrl()%>" role="button" aria-haspopup="true" aria-expanded="false"><%=contentData.getNavDisplay()%>
    </a>
    <div class="dropdown-menu">
        <a class="dropdown-item <%=activeIds.contains(contentData.getId())? "active" : ""%>" href="<%=contentData.getUrl()%>"><%=contentData.getNavDisplay()%>
        </a>
        <% for (ContentData child : children){%>
        <a class="dropdown-item <%=activeIds.contains(child.getId())? "active" : ""%>" href="<%=child.getUrl()%>"><%=child.getNavDisplay()%></a>
        <%}%>
    </div>
</li>
<%
                } else {%>
<li class="nav-item <%=activeIds.contains(contentData.getId())? "active" : ""%>">
    <a class="nav-link <%=activeIds.contains(contentData.getId())? "active" : ""%>" href="<%=contentData.getUrl()%>"><%=contentData.getNavDisplay()%>
    </a>
</li>
<%
                }

            }

        }
    }%>
