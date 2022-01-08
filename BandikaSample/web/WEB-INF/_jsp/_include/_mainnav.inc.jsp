<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ page import="java.util.*" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    ContentData home = ContentCache.getContentRoot();
    ContentData currentContent = rdata.getCurrentContent();
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
    <a class="nav-link <%=activeIds.contains(contentData.getId())? "active" : ""%> dropdown-toggle" data-toggle="dropdown" href="<%=contentData.getUrl()%>" role="button" aria-haspopup="true" aria-expanded="false"><%=$H(contentData.getDisplayName())%>
    </a>
    <div class="dropdown-menu">
        <a class="dropdown-item <%=activeIds.contains(contentData.getId())? "active" : ""%>" href="<%=contentData.getUrl()%>"><%=$H(contentData.getDisplayName())%>
        </a>
        <% for (ContentData child : children){%>
        <a class="dropdown-item <%=activeIds.contains(contentData.getId())? "active" : ""%>" href="<%=child.getUrl()%>"><%=$H(child.getDisplayName())%></a>
        <%}%>
    </div>
</li>
<%
                } else {%>
<li class="nav-item <%=activeIds.contains(contentData.getId())? "active" : ""%>">
    <a class="nav-link <%=activeIds.contains(contentData.getId())? "active" : ""%>" href="<%=contentData.getUrl()%>"><%=$H(contentData.getDisplayName())%>
    </a>
</li>
<%
                }

            }

        }
    }%>
