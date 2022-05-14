package de.elbe5.application;

import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.response.HtmlResponse;
import de.elbe5.rights.SystemZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class AdminPage extends HtmlResponse {

    IAdminIncludePage include;
    String title;

    public AdminPage(IAdminIncludePage include, String title){
        this.include = include;
        this.title = title;
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        StringBuilder sb = new StringBuilder();
        appendHtmlStart(sb, rdata);
        if (include != null) {
            include.appendHtml(sb, rdata);
        }
        appendHtmlEnd(sb);
        html=sb.toString();
        super.processResponse(context, rdata, response);
    }

    public void appendHtmlStart(StringBuilder sb, RequestData rdata) {
        sb.append(Strings.format("""
                <!DOCTYPE html>
                <html lang="{1}">
                <head>
                    <meta charset="utf-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                    <title>{2}</title>
                    <link rel="shortcut icon" href="/favicon.ico"/>
                    <link rel="stylesheet" href="/static-content/css/bootstrap.css"/>
                    <link rel="stylesheet" href="/static-content/css/bandika.css"/>
                    <link rel="stylesheet" href="/static-content/css/layout.css"/>
                    <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
                    <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
                    <script type="text/javascript" src="/static-content/js/bootstrap.tree.js"></script>
                    <script type="text/javascript" src="/static-content/ckeditor/ckeditor.js"></script>
                    <script>
                        CKEDITOR.config.language = '{3}';
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
                                        <a class="nav-link fa fa-home" href="/" title="{4}>"></a></li>
                                    <li class="nav-item">
                                        <a class="nav-link fa fa-sign-out" href="/ctrl/user/logout" title="{5}"></a>
                                    </li>
                                </ul>
                            </section>
                        </div>
                            <div class="menu row">
                                <section class="col-12 menu">
                                    <nav class="navbar navbar-expand-lg">
                                        <span class="navbar-brand" >{6}</span>
                                        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                                            <span class="navbar-toggler-icon"></span>
                                        </button>
                                        <div class="collapse navbar-collapse" id="navbarSupportedContent">
                                            <ul class="navbar-nav mr-auto">
                                            """,
                Configuration.getLocale().getLanguage(),
                Strings.toHtml(title),
                Configuration.getLocale().getLanguage(),
                Strings.getHtml("_home"),
                Strings.getHtml("_logout"),
                Strings.getHtml("_administration")
                ));
        if (rdata.hasSystemRight(SystemZone.APPLICATION)) {
            sb.append(Strings.format("""
                        <li class="nav-item">
                            <a class="nav-link"
                               href="/ctrl/admin/openSystemAdministration">{1}
                            </a>
                        </li>
                    """,
                    Strings.getHtml("_systemAdministration")
                    ));
        }
        if (rdata.hasSystemRight(SystemZone.USER)) {
            sb.append(Strings.format("""
                    <li class="nav-item">
                        <a class="nav-link"
                           href="/ctrl/admin/openUserAdministration">{1}
                        </a>
                    </li>
                    """,
                    Strings.getHtml("_userAdministration")));
        }
        if (rdata.hasAnyContentRight()) {
            sb.append(Strings.format("""
                    <li class="nav-item">
                        <a class="nav-link" href="/ctrl/admin/openContentAdministration">{1}
                        </a>
                    </li>""",
                    Strings.getHtml("_contentAdministration")
            ));
        }
        sb.append("""
                                            </ul>
                                        </div>
                                    </nav>
                                </section>
                            </div>
                        </header>
                        <main id="main" role="main">
                            <div id="pageContainer">
                            """);
    }

    public void appendHtmlEnd(StringBuilder sb) {
        sb.append(Strings.format("""
                                <tpl:content/>
                            </div>
                        </main>
                    </div>
                    <div class="container fixed-bottom">
                        <footer>
                            <div class="container">
                                <ul class="nav">
                                    &copy; {1}
                                </ul>
                            </div>
                        </footer>
                    </div>
                    <div class="modal" id="modalDialog" tabindex="-1" role="dialog"></div>
                <script type="text/javascript">
                    function confirmDelete() {
                        return confirm('{2}');
                    }
                                
                    function confirmExecute() {
                        return confirm('{3}');
                    }
                </script>
                                
                </body>
                </html>
                """,
                Strings.getHtml("_copyright"),
                Strings.getHtml("_confirmDelete"),
                Strings.getHtml("_confirmExecute")
        ));
    }
}
