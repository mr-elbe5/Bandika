import de.elbe5.template.*;
import de.elbe5.application.Configuration;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestType;

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
        TemplateCache.templateBasePath = System.getProperty("user.dir") + "/../Bandika/templates/";
        System.out.println(TemplateCache.templateBasePath);
        TemplateTagFactory.addTagType(MessageTag.TYPE, MessageTag.class);
        TemplateTagFactory.addTagType(BreadcrumbTag.TYPE, BreadcrumbTag.class);
        TemplateTagFactory.addTagType(ContentTag.TYPE, ContentTag.class);
        TemplateTagFactory.addTagType(FooterTag.TYPE, FooterTag.class);
        TemplateTagFactory.addTagType(HtmlFieldTag.TYPE, HtmlFieldTag.class);
        TemplateTagFactory.addTagType(MainNavTag.TYPE, MainNavTag.class);
        TemplateTagFactory.addTagType(SectionTag.TYPE, SectionTag.class);
        TemplateTagFactory.addTagType(SysNavTag.TYPE, SysNavTag.class);
        TemplateTagFactory.addTagType(TextFieldTag.TYPE, TextFieldTag.class);
        rdata.getTemplateAttributes().put("language", Configuration.getLocale().getLanguage());
        rdata.getTemplateAttributes().put("include", "_template/ctrl/defaultPage");

        TemplateCache.addType("master");
        TemplateCache.addType("page");
        TemplateCache.addType("part");

        TemplateCache.load();
    }

    void run(){
        Template page = TemplateCache.getTemplate("master", "defaultMaster");
        System.out.println(page.getCode());
    }

}