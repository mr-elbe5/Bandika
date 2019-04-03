<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ page import="de.elbe5.base.data.Locales" %>
<%
    Locale locale=null;
    if (session!=null)
        locale= (Locale) session.getAttribute(Statics.KEY_LOCALE);
    if (locale==null)
        locale=request.getLocale();
    if (locale==null)
        locale= Locales.getInstance().getDefaultLocale();
    int id=Locales.getInstance().getLocaleRoot(locale);
    String url;
    if (id!=0)
        url="/page/show/"+id;
    else if (session!=null && session.getAttribute(Statics.KEY_LOGIN)!=null)
        url="/admin/openSystemAdministration";
    else
        url="/user/openLogin";
    RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher(url);
    rd.forward(request, response);
%>