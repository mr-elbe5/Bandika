import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestType;
import de.elbe5.serverpage.ServerPage;
import de.elbe5.serverpage.SPParser;

public class TemplateTest {

    public static void main(String[] args) {
        SPParser parser = new SPParser(code);
        ServerPage tpl = parser.parse();
        StringMap params = new StringMap();
        System.out.println(tpl.getHtml(params));
    }

    static String code = """
           <!DOCTYPE html>
           <html xmlns:spg="http://elbe5.de" lang="{{language}}">
               <head>
                   <meta charset="utf-8"/>
                   <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
                   <title>{{title}}</title>
                   <meta name="keywords" content="{{keywords}}"/>
                   <meta name="description" content="{{description}}"/>
                   <link rel="shortcut icon" href="/favicon.ico"/>
                   <link rel="stylesheet" href="/css/bandika.css"/>
                   <link rel="stylesheet" href="/layout/layout.css"/>
                   <script type="text/javascript" src="/js/jquery-1.12.4.min.js"></script>
                   <script type="text/javascript" src="/js/bootstrap.bundle.min.js"></script>
                   <script type="text/javascript" src="/js/bootstrap.tree.js"></script>
                   <script type="text/javascript" src="/ckeditor/ckeditor.js"></script>
                   <script type="text/javascript" src="/ckeditor/adapters/jquery.js"></script>
                   <script type="text/javascript" src="/js/bandika.js"></script>
               </head>
               <body>
                   <div class="container">
                       <header>
                           <section class="sysnav">
                               <spg:sysnav/>
                           </section>
                           <div class="menu row">
                               <spg:mainnav/>
                           </div>
                           <div class="bc row">
                               <spg:breadcrumb/>
                           </div>
                       </header>
                       <main id="main" role="main">
                           <div id="pageContainer">
                               <spg:content/>
                           </div>
                       </main>
                   </div>
                   <div class="container fixed-bottom">
                       <footer class="footer">
                           <spg:footer/>
                       </footer>
                   </div>
                   <div class="modal" id="modalDialog" tabindex="-1" role="dialog"></div>
               </body>
           </html>
            """;
}