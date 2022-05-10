import de.elbe5.html.IncludeTag;
import de.elbe5.html.MessageTag;
import de.elbe5.layout.*;
import de.elbe5.application.Configuration;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestType;
import de.elbe5.template.TemplateCache;
import de.elbe5.template.TemplateTagFactory;
import de.elbe5.template.Template;
import de.elbe5.serverpagetags.*;

import javax.servlet.http.HttpServletRequest;

public class CmsTest {

    public static void main(String[] args) {
        new CmsTest().test();
    }

    HttpServletRequest request = new TestRequest();
    RequestData rdata = new RequestData("GET", RequestType.content, request);

    void test(){
        prepare();
        run();
    }

    void prepare(){
        Template.templateBasePath = System.getProperty("user.dir") + "/../Bandika/web/WEB-INF/_shtml/";
        System.out.println(Template.templateBasePath);
        TemplateTagFactory.addTagType(IncludeTag.TYPE, IncludeTag.class);
        TemplateTagFactory.addTagType(BreadcrumbTag.TYPE, BreadcrumbTag.class);
        TemplateTagFactory.addTagType(ContentTag.TYPE, ContentTag.class);
        TemplateTagFactory.addTagType(FooterTag.TYPE, FooterTag.class);
        TemplateTagFactory.addTagType(SPHtmlFieldTag.TYPE, SPHtmlFieldTag.class);
        TemplateTagFactory.addTagType(MainNavTag.TYPE, MainNavTag.class);
        TemplateTagFactory.addTagType(SPSectionTag.TYPE, SPSectionTag.class);
        TemplateTagFactory.addTagType(SysNavTag.TYPE, SysNavTag.class);
        TemplateTagFactory.addTagType(SPTextFieldTag.TYPE, SPTextFieldTag.class);
        rdata.getTemplateAttributes().put("language", Configuration.getLocale().getLanguage());
        rdata.getTemplateAttributes().put("include", "_layout/page/defaultPage");
    }

    void run(){
        Template page = TemplateCache.getTemplate("user/editUser");
        System.out.println(page.getPath());
        System.out.println(page.getCode());
    }

}