import de.elbe5.application.ApplicationPath;
import de.elbe5.application.Configuration;
import de.elbe5.request.RequestData;
import de.elbe5.request.RequestType;
import de.elbe5.serverpage.SPPageCache;
import de.elbe5.serverpage.SPTagFactory;
import de.elbe5.serverpage.ServerPage;
import de.elbe5.serverpage.SPParser;
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
        ServerPage.shtmlBasePath = System.getProperty("user.dir") + "/web/WEB-INF/_shtml/";
        System.out.println(ServerPage.shtmlBasePath);
        SPTagFactory.addTagType(SPIfTag.TYPE, SPIfTag.class);
        SPTagFactory.addTagType(SPIncludeTag.TYPE, SPIncludeTag.class);
        SPTagFactory.addTagType(SPBreadcrumbTag.TYPE, SPBreadcrumbTag.class);
        SPTagFactory.addTagType(SPContentTag.TYPE, SPContentTag.class);
        SPTagFactory.addTagType(SPContentTreeTag.TYPE, SPContentTreeTag.class);
        SPTagFactory.addTagType(SPFooterTag.TYPE, SPFooterTag.class);
        SPTagFactory.addTagType(SPFormTag.TYPE, SPFormTag.class);
        SPTagFactory.addTagType(SPFormCheckTag.TYPE, SPFormCheckTag.class);
        SPTagFactory.addTagType(SPFormDateTag.TYPE, SPFormDateTag.class);
        SPTagFactory.addTagType(SPFormErrorTag.TYPE, SPFormErrorTag.class);
        SPTagFactory.addTagType(SPFormFileTag.TYPE, SPFormFileTag.class);
        SPTagFactory.addTagType(SPFormLineTag.TYPE, SPFormLineTag.class);
        SPTagFactory.addTagType(SPFormPasswordTag.TYPE, SPFormPasswordTag.class);
        SPTagFactory.addTagType(SPFormRadioTag.TYPE, SPFormRadioTag.class);
        SPTagFactory.addTagType(SPFormSelectTag.TYPE, SPFormSelectTag.class);
        SPTagFactory.addTagType(SPFormTextAreaTag.TYPE, SPFormTextAreaTag.class);
        SPTagFactory.addTagType(SPFormTextTag.TYPE, SPFormTextTag.class);
        SPTagFactory.addTagType(SPGroupListTag.TYPE, SPGroupListTag.class);
        SPTagFactory.addTagType(SPHtmlFieldTag.TYPE, SPHtmlFieldTag.class);
        SPTagFactory.addTagType(SPMainNavTag.TYPE, SPMainNavTag.class);
        SPTagFactory.addTagType(SPMessageTag.TYPE, SPMessageTag.class);
        SPTagFactory.addTagType(SPSectionTag.TYPE, SPSectionTag.class);
        SPTagFactory.addTagType(SPSysNavTag.TYPE, SPSysNavTag.class);
        SPTagFactory.addTagType(SPTextFieldTag.TYPE, SPTextFieldTag.class);
        SPTagFactory.addTagType(SPUserListTag.TYPE, SPUserListTag.class);
        rdata.getPageAttributes().put("language", Configuration.getLocale().getLanguage());
        rdata.getPageAttributes().put("include", "template/page/defaultPage");
    }

    void run(){
        ServerPage page = SPPageCache.getPage("template/master/defaultMaster");
        System.out.println(page.getHtml(rdata));
    }

}