<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%@ page import="de.bandika.cms.pagepart.PagePartData" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    PageData data = (PageData) request.getAttribute("pageData");
    PagePartData partData= (PagePartData) request.getAttribute("partData");
%>
<div id ="test">DOCCENTER</div>
DOCCENTER

<input type="button" onclick="sendDoccenterCall()" value="Click" />
<script type="text/javascript">
    function sendDoccenterCall(){
        $.ajax({
            type: 'POST',
            url: '/pagepart.ajx?act=executePagePartMethod&pageId=<%=data.getId()%>&sectionName=<%=partData.getSection()%>&partId=<%=partData.getId()%>&partMethod=test',
            processData: false,
            contentType: false,
            success: function (data) {
                $('#test').html(data);
            }
        });
    }
</script>