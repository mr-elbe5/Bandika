<%
    RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/default.srv");
    rd.forward(request, response);
%>