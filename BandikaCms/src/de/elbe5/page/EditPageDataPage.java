package de.elbe5.page;

import de.elbe5.base.Strings;
import de.elbe5.content.EditContentDataPage;
import de.elbe5.html.Form;
import de.elbe5.layout.Template;
import de.elbe5.layout.TemplateCache;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class EditPageDataPage extends EditContentDataPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        PageData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, PageData.class);
        String url = "/ctrl/page/saveContentData/" + contentData.getId();
        List<Template> pageTemplates = TemplateCache.getTemplates("page");
        appendStartHtml(sb, rdata, contentData, url);
        Form.appendSelectStart(sb, rdata.hasFormErrorField("template"), "template", Strings.getHtml("_pageTemplate"), true, "");
        Form.appendOption(sb, "", Strings.getHtml("_pleaseSelect"), contentData.getTemplateName().isEmpty());
        for (Template template : pageTemplates) {
            String templateName = template.getName();
            Form.appendOption(sb, Strings.toHtml(templateName), Strings.getHtml(template.getKey()), templateName.equals(contentData.getTemplateName()));
        }
        Form.appendSelectEnd(sb);
        appendEndHtml(sb, url);
    }

}
