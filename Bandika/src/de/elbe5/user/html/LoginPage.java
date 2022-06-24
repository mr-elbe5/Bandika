package de.elbe5.user.html;

import de.elbe5.application.Configuration;
import de.elbe5.request.RequestData;
import de.elbe5.response.HtmlIncludePage;
import de.elbe5.response.IHtmlBuilder;

import java.util.Map;

public class LoginPage extends HtmlIncludePage implements IHtmlBuilder {

    static final String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                <title>{{title}}</title>
                <link rel="shortcut icon" href="/favicon.ico"/>
                <link rel="stylesheet" href="/static-content/css/bootstrap.css"/>
                <link rel="stylesheet" href="/static-content/css/bandika.css"/>
                <link rel="stylesheet" href="/static-content/css/{{name}}.css"/>
                <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
                <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
                            
            </head>
            <body class="login">
            <main id="main" role="main">
                <div class="container">
                    <tpl:message/>
                    <section class="mainSection loginSection text-center">
                        <form class="form" action="/ctrl/user/login" method="post" name="loginForm" accept-charset="UTF-8">
                            <img class="mb-4" src="/static-content/img/login-logo.png" alt="{{title}}">
                            <label for="login" class="sr-only">{{_loginName}}
                            </label>
                            <input type="text" id="login" name="login" class="form-control"
                                   placeholder="{{_loginName}}" required autofocus>
                            <label for="password" class="sr-only">{{_password}}
                            </label>
                            <input type="password" id="password" name="password" class="form-control"
                                   placeholder="{{_password}}" required>
                            <button class="btn btn-outline-primary" type="submit">{{_login}}
                            </button>
                            <button class="btn btn-outline-secondary"
                                    onclick="$(location).attr('href','/');">{{_cancel}}
                            </button>
                        </form>
                    </section>
                </div>
            </main>
            <footer>
            </footer>
            </body>
            </html>
            """;

    public void appendHtml(StringBuilder sb, RequestData rdata) {
        append(sb, html,
                Map.ofEntries(
                        Map.entry("title", toHtml(Configuration.getAppTitle())),
                        Map.entry("name", toHtml(Configuration.getAppName()))));

    }
}
