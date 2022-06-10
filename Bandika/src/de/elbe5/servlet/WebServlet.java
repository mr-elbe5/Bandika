/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.servlet;

import de.elbe5.response.HtmlResponse;
import de.elbe5.response.IHtmlBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public abstract class WebServlet extends HttpServlet implements IHtmlBuilder {

    public final static String GET = "GET";
    public final static String POST = "POST";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(GET, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(POST, request, response);
    }

    protected abstract void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException;

    static final String exceptionHtml = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                <title>Exception</title>
            </head>
            <style>
                body {
                    position: fixed;
                    top: 0;
                    bottom: 0;
                    left: 0;
                    right: 0;
                    background: #f8f9fa;
                    color: #343a40;
                    font-family: Arial, Helvetica, sans-serif;
                    font-size: 1rem;
                }
                        
                main{
                    max-width:360px;
                    margin: 20% auto;
                    padding: 30px;
                    background: #f8f9fa;
                    border: 1px solid #343a40;
                    border-radius: 5px;
                }
                h1{
                    text-align:center;
                    font-size: 1.5rem;
                }
                .errorText{
                    text-align: center;
                    font-size: 1.2rem;
                }
                .link{
                    padding-top: 2rem;
                    text-align: center;
                }
                        
            </style>
            <body>
            <main>
                <h1>$error$</h1>
                <div class="errorText">$errorText$</div>
                <div class="link"><a href="/" title="Home">$home$</a></div>
            </main>
            </body>
            </html>          
            """;

    protected void handleException(HttpServletRequest request, HttpServletResponse response, int code) {
        String errorKey = (String) request.getAttribute("errorKey");
        String html = format(exceptionHtml,
                Map.ofEntries(
                        htmlParam("error", getString("_error" + code)),
                        htmlParam("errorText", errorKey != null ? getHtml(errorKey) : ""),
                        param("home", "_home")
                )
        );
        HtmlResponse resp = new HtmlResponse(html);
        resp.sendHtml(response);
    }
}
