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

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        String name = getStringParam("name", rdata, "");
        String cssClass = getStringParam("cssClass", rdata, "");
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
        List<Template> templates = TemplateCache.getTemplates("part");
        append(sb,"""
                <div class="section $css$" id="$id$" title="Section $name$">
                    <div class="sectionEditButtons">
                        <div class="btn-group btn-group-sm" role="group">
                            <div class="btn-group btn-group-sm" role="group">
                                <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="$newPart$"></button>
                                <div class="dropdown-menu">
                                """,
                Map.ofEntries(
                        param("css",sectionData.getCssClass()),
                        param("id",sectionData.getSectionId()),
                        param("name",sectionData.getName()),
                        param("newPart","_newPart")
                )
        );
        for (String partType : PagePartFactory.getTypes()) {
            if (PagePartFactory.useLayouts(partType)) {
                for (Template template : templates) {
                    append(sb,"""
                            <a class="dropdown-item" href="" onclick="return addPart(-1,'$name$','$partType$','$templateName$');">$templateKey$
                            </a>
                            """,
                            Map.ofEntries(
                                    param("name",sectionData.getName()),
                                    param("partType",partType),
                                    param("templateName",template.getName()),
                                    param("templateKey",getHtml(template.getKey()))
                            )
                    );
                }
            } else {
                append(sb,"""
                        <a class="dropdown-item" href="" onclick="return addPart(-1,'$name$','$partType$');">$typeName$
                        </a>
                        """,
                        Map.ofEntries(
                                param("name",sectionData.getName()),
                                param("partType",partType),
                                param("typeName",getHtml("class."+partType))
                        )
                );
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

    void appendSection(StringBuilder sb, RequestData rdata, SectionData sectionData) {
        if (!sectionData.getParts().isEmpty()) {
            append(sb,"""
                    <div class="section $css$">
                    """,
                    Map.ofEntries(
                            param("css",sectionData.getCssClass())
                    )
            );
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
