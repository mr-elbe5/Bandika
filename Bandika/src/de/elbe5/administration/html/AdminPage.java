package de.elbe5.administration.html;

import de.elbe5.application.Configuration;
import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.response.HtmlResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class AdminPage extends HtmlResponse {

    static AdminMenu menu = new AdminMenu();

    public static void setMenu(AdminMenu menu) {
        AdminPage.menu = menu;
    }

    String title;

    public AdminPage(String title){
        this.title = title;
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response) {
        appendHtmlStart();
        menu.appendHtml(sb, rdata);
        appendHeaderEnd();
        appendPageHtml(rdata);
        appendHtmlEnd();
        sendHtml(response);
    }

    public void appendPageHtml(RequestData rdata){
    }

    public void appendHtmlStart() {
        append(sb,"""
                        <!DOCTYPE html>
                        <html lang="$lang">
                        <head>
                            <meta charset="utf-8"/>
                            <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                            <title>$tiitle</title>
                            <link rel="shortcut icon" href="/favicon.ico"/>
                            <link rel="stylesheet" href="/static-content/css/bootstrap.css"/>
                            <link rel="stylesheet" href="/static-content/css/bandika.css"/>
                            <link rel="stylesheet" href="/static-content/css/admin.css"/>
                            <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
                            <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
                            <script type="text/javascript" src="/static-content/js/bootstrap.tree.js"></script>
                            <script type="text/javascript" src="/static-content/ckeditor/ckeditor.js"></script>
                            <script>
                                CKEDITOR.config.language = '$lang';
                            </script>
                            <script type="text/javascript" src="/static-content/ckeditor/adapters/jquery.js"></script>
                            <script type="text/javascript" src="/static-content/js/bandika-webbase.js"></script>
                        </head>
                        <body class="admin">
                            <div class="container">
                                <header>
                                    <div class="top">
                                    <section class="sysnav">
                                        <ul class="nav justify-content-end">
                                            <li class="nav-item">
                                                <a class="nav-link fa fa-home" href="/" title="$home>"></a></li>
                                            <li class="nav-item">
                                                <a class="nav-link fa fa-sign-out" href="/ctrl/user/logout" title="$logout"></a>
                                            </li>
                                        </ul>
                                    </section>
                                </div>
                        """,
                parameters(
                        param("lang", Configuration.getLocale().getLanguage()),
                        param("title", title),
                        param("home", "_home"),
                        param("logout", "_logout")
                )
        );
    }

    public void appendHeaderEnd() {
        append("""
                     </header>
                        <main id="main" role="main">
                            <div id="pageContainer" class="container admin">
                """);
    }

    public void appendHtmlEnd() {
        append("""
                                    </div>
                                </main>
                            </div>
                            <div class="modal" id="modalDialog" tabindex="-1" role="dialog"></div>
                        <script type="text/javascript">
                            function confirmDelete() {
                                return confirm('{1}');
                            }
                                        
                            function confirmExecute() {
                                return confirm('{2}');
                            }
                        </script>
                                        
                        </body>
                        </html>
                        """,
                Strings.getHtml("_confirmDelete"),
                Strings.getHtml("_confirmExecute")
        );
    }

}
