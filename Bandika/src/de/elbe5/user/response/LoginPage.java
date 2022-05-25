package de.elbe5.user.response;

import de.elbe5.application.Configuration;
import de.elbe5.base.Strings;

public class LoginPage {

    public static String getHtml() {
        return Strings.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="utf-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                    <title>{1}
                    </title>
                    <link rel="shortcut icon" href="/favicon.ico"/>
                    <link rel="stylesheet" href="/static-content/css/bootstrap.css"/>
                    <link rel="stylesheet" href="/static-content/css/bandika.css"/>
                    <link rel="stylesheet" href="/static-content/css/{2}.css"/>
                    <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
                    <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
                                
                </head>
                <body class="login">
                <main id="main" role="main">
                    <div class="container">
                        <tpl:message/>
                        <section class="mainSection loginSection text-center">
                            <form class="form" action="/ctrl/user/login" method="post" name="loginForm" accept-charset="UTF-8">
                                <img class="mb-4" src="/static-content/img/login-logo.png" alt="{3}">
                                <label for="login" class="sr-only">{4}
                                </label>
                                <input type="text" id="login" name="login" class="form-control"
                                       placeholder="{5}" required autofocus>
                                <label for="password" class="sr-only">{6}
                                </label>
                                <input type="password" id="password" name="password" class="form-control"
                                       placeholder="{7}" required>
                                <button class="btn btn-outline-primary" type="submit">{8}
                                </button>
                                <button class="btn btn-outline-secondary"
                                        onclick="$(location).attr('href','/');">{9}
                                </button>
                            </form>
                        </section>
                    </div>
                </main>
                <footer>
                </footer>
                </body>
                </html>
                """,
                Configuration.getAppTitle(),
                Configuration.getAppName(),
                Configuration.getAppTitle(),
                Strings.getHtml("_loginName"),
                Strings.getHtml("_loginName"),
                Strings.getHtml("_password"),
                Strings.getHtml("_password"),
                Strings.getHtml("_login"),
                Strings.getHtml("_cancel")
                );

    }
}
