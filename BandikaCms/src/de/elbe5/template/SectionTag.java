package de.elbe5.template;

import de.elbe5.page.PageData;
import de.elbe5.page.PagePartData;
import de.elbe5.page.PagePartFactory;
import de.elbe5.page.SectionData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;
import java.util.Map;

public class SectionTag extends TemplateTag {

    public static final String TYPE = "section";

    public SectionTag() {
        this.type = TYPE;
    }

    static final String sectionEditStartHtml = """
            <div class="section {{css}}" id="{{id}}" title="Section {{name}}">
                <div class="sectionEditButtons">
                    <div class="btn-group btn-group-sm" role="group">
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{{_newPart}}"></button>
                            <div class="dropdown-menu">
            """;
    static final String templateLinkHtml = """
                                <a class="dropdown-item" href="" onclick="return addPart(-1,'{{name}}','{{partType}}','{{templateName}}');">{{templateKey}}
                                </a>
            """;
    static final String linkHtml = """
                                <a class="dropdown-item" href="" onclick="return addPart(-1,'{{name}}','{{partType}}');">{{typeName}}
                                </a>
            """;
    static final String endButtonsHtml = """
                            </div>
                        </div>
                    </div>
                </div>
            """;
    static final String sectionEditEndHtml = """
            </div>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        String name = getStringAttribute("name", "");
        String cssClass = getStringAttribute("cssClass", "");
        PageData pageData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        SectionData sectionData = pageData.ensureSection(name);
        if (sectionData != null) {
            sectionData.setCssClass(cssClass);
            if (pageData.isEditing()) {
                appendEditSection(sb, rdata, sectionData);
            } else {
                appendSection(sb, rdata, sectionData);
            }
        }
    }

    void appendEditSection(StringBuilder sb, RequestData rdata, SectionData sectionData) {
        List<Template> templates = TemplateCache.getInstance().getTemplates("part");
        append(sb, sectionEditStartHtml,
                Map.ofEntries(
                        Map.entry("css", sectionData.getCssClass()),
                        Map.entry("id", sectionData.getSectionId()),
                        Map.entry("name", toHtml(sectionData.getName()))));
        for (String partType : PagePartFactory.getTypes()) {
            if (PagePartFactory.useLayouts(partType)) {
                for (Template template : templates) {
                    append(sb, templateLinkHtml,
                            Map.ofEntries(
                                    Map.entry("name", toHtml(sectionData.getName())),
                                    Map.entry("partType", partType),
                                    Map.entry("templateName", toHtml(template.getName())),
                                    Map.entry("templateKey", getHtml(template.getKey()))));
                }
            } else {
                append(sb, linkHtml,
                        Map.ofEntries(
                                Map.entry("name", toHtml(sectionData.getName())),
                                Map.entry("partType", partType),
                                Map.entry("typeName", getHtml("class." + partType))));
            }
        }
        sb.append(endButtonsHtml);
        for (PagePartData partData : sectionData.getParts()) {
            partData.appendHtml(sb, rdata);
        }
        sb.append(sectionEditEndHtml);
    }

    static final String sectionStartHtml = """
            <div class="section {{css}}">
            """;
    static final String sectionEndHtml = """
            </div>
            """;

    void appendSection(StringBuilder sb, RequestData rdata, SectionData sectionData) {
        if (!sectionData.getParts().isEmpty()) {
            append(sb, sectionStartHtml,
                    Map.ofEntries(
                            Map.entry("css", sectionData.getCssClass())));
            for (PagePartData partData : sectionData.getParts()) {
                partData.appendHtml(sb, rdata);
            }
            sb.append(sectionEndHtml);
        }
    }

}
