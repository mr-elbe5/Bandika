package de.elbe5.layout;

import de.elbe5.base.Strings;
import de.elbe5.page.PageData;
import de.elbe5.page.PagePartData;
import de.elbe5.page.PagePartFactory;
import de.elbe5.page.SectionData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class SectionTag extends TemplateTag {

    public static final String TYPE = "section";

    public SectionTag() {
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        String name = getStringParam("name", rdata, "");
        String cssClass = getStringParam("cssClass", rdata, "");
        PageData pageData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        SectionData sectionData = pageData.ensureSection(name);
        if (sectionData != null) {
            sectionData.setCssClass(cssClass);
            if (pageData.isEditing()) {
                appendEditSection(sb, rdata, pageData, sectionData);
            } else {
                appendSection(sb, rdata, pageData, sectionData);
            }
        }
    }

    void appendEditSection(StringBuilder sb, RequestData rdata, PageData pageData, SectionData sectionData) {
        List<Template> templates = TemplateCache.getTemplates("part");
        sb.append(Strings.format("""
                <div class="section {1}" id="{2}" title="Section {3}">
                    <div class="sectionEditButtons">
                        <div class="btn-group btn-group-sm" role="group">
                            <div class="btn-group btn-group-sm" role="group">
                                <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{4}"></button>
                                <div class="dropdown-menu">
                                """,
                sectionData.getCssClass(),
                sectionData.getSectionId(),
                sectionData.getName(),
                Strings.getHtml("_newPart")
                ));
        for (String partType : PagePartFactory.getTypes()) {
            if (PagePartFactory.useLayouts(partType)) {
                for (Template template : templates) {
                    sb.append(Strings.format("""
                            <a class="dropdown-item" href="" onclick="return addPart(-1,'{1}','{2}','{3}');">{4}
                            </a>
                            """,
                            sectionData.getName(),
                            partType,
                            Strings.toHtml(template.getName()),
                            Strings.getHtml(template.getKey())
                            ));
                }
            } else {
                sb.append(Strings.format("""
                        <a class="dropdown-item" href="" onclick="return addPart(-1,'{1}','{2}');">{3}
                        </a>
                        """,
                        sectionData.getName(),
                        partType,
                        Strings.getHtml("class."+partType)
                        ));
            }
        }
        sb.append("""
                            </div>
                        </div>
                    </div>
                </div>
                """);
        for (PagePartData partData : sectionData.getParts()) {
            partData.appendHtml(sb, rdata);
        }
        sb.append("""  
                </div>
                """);
    }

    void appendSection(StringBuilder sb, RequestData rdata, PageData pageData, SectionData sectionData) {
        if (!sectionData.getParts().isEmpty()) {
            sb.append(Strings.format("""
                    <div class="section {1}">
                    """,
                    sectionData.getCssClass()
            ));
            for (PagePartData partData : sectionData.getParts()) {
                partData.appendHtml(sb, rdata);
            }
            sb.append("""  
                    </div>
                    """
            );
        }
    }

}
