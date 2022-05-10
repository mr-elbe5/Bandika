package de.elbe5.serverpagetags;

import de.elbe5.page.PageData;
import de.elbe5.page.SectionData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateTag;
import de.elbe5.template.Template;

public class SPSectionTag extends TemplateTag {

    public static final String TYPE = "section";

    public SPSectionTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        String name = getStringParam("name", rdata,"");
        String cssClass = getStringParam("cssClass",rdata,"");
        PageData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        SectionData sectionData = contentData.ensureSection(name);
        if (sectionData != null) {
            sectionData.setCssClass(cssClass);
            rdata.getAttributes().put("sectionData", sectionData);
            String url;
            if (contentData.isEditing()) {
                url = "page/editSection";
            } else {
                url = "page/section";
            }
            Template.includePage(sb, url, rdata);
            rdata.getAttributes().remove("sectionData");
        }
    }

}
