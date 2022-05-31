package de.elbe5.page.html;

import de.elbe5.base.Strings;
import de.elbe5.content.html.EditContentDataPage;
import de.elbe5.template.Template;
import de.elbe5.template.TemplateCache;
import de.elbe5.page.PageData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class EditPageDataPage extends EditContentDataPage {

    @Override
    public void appendHtml(RequestData rdata) {
        PageData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, PageData.class);
        String url = "/ctrl/page/saveContentData/" + contentData.getId();
        List<Template> pageTemplates = TemplateCache.getTemplates("page");
        appendStartHtml(rdata, contentData, url);
        appendSelectStart(sb, rdata.hasFormErrorField("template"), "template", Strings.getHtml("_pageTemplate"), true, "");
        appendOption(sb, "", Strings.getHtml("_pleaseSelect"), contentData.getTemplateName().isEmpty());
        for (Template template : pageTemplates) {
            String templateName = template.getName();
            appendOption(sb, Strings.toHtml(templateName), Strings.getHtml(template.getKey()), templateName.equals(contentData.getTemplateName()));
        }
        appendSelectEnd(sb);
        appendEndHtml(url);
    }

}
