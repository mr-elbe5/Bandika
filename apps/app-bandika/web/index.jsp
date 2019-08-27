<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.application.Statics" %>
<%@ page import="de.elbe5.configuration.Configuration" %>
<%@ page import="de.elbe5.page.PageCache" %>
<%@ page import="de.elbe5.page.PageData" %>
<%
    Locale locale=null;
    if (session!=null)
        locale= (Locale) session.getAttribute(Statics.KEY_LOCALE);
    if (locale==null){
        Locale requestLocale=request.getLocale();
        if (requestLocale!=null)
            locale=new Locale(requestLocale.getLanguage().substring(0,2));
    }
    if (locale==null || !Configuration.getInstance().hasLocale(locale))
        locale= Configuration.getInstance().getDefaultLocale();
    PageData pageData = PageCache.getInstance().getHomePage(locale);
    String url;
    if (pageData!=null)
        url=pageData.getUrl();
    else if (session!=null && session.getAttribute(Statics.KEY_LOGIN)!=null)
        url="/ctrl/admin/openSystemAdministration";
    else
        url="/ctrl/user/openLogin";
    RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher(url);
    rd.forward(request, response);
%>