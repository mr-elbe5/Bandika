package de.elbe5.template;

import de.elbe5.page.*;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.response.IFormBuilder;

import java.util.List;
import java.util.Map;

public class PartTag extends TemplateTag implements IFormBuilder {

    public static final String TYPE = "part";

    public PartTag(){
        this.type = TYPE;
    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){
        PageData pageData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        TemplatePartData part = rdata.getAttributes().get(PagePartData.KEY_PART, TemplatePartData.class);
        if (pageData == null || part == null)
            return;
        String cssClass = getStringParam("cssClass", rdata, "");
        if (pageData.isEditing()){
            appendPartEditStart(sb, part, cssClass);
        }
        else{
            appendPartStart(sb, part, cssClass);
        }
    }

    void appendPartEditStart(StringBuilder sb, PagePartData partData, String cssClass) {
        append(sb,"""
                        <div id="$wrapperId$" class="partWrapper $cssClass$" title="$editTitle$">
                        """,
                Map.ofEntries(
                        param("wrapperId",partData.getPartWrapperId()),
                        param("cssClass",cssClass),
                        param("editTitle",partData.getEditTitle())
                )
        );
        appendHiddenField(sb, partData.getPartPositionName(), Integer.toString(partData.getPosition()));
        append(sb,"""
                        <div class="partEditButtons">
                            <div class="btn-group btn-group-sm" role="group">
                                <div class="btn-group btn-group-sm" role="group">
                                    <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="$title$"></button>
                                    <div class="dropdown-menu">
                                    """,
                Map.ofEntries(
                        param("title","_newPart")
                )
        );
        for (String partType : PagePartFactory.getTypes()) {
            if (PagePartFactory.useLayouts(partType)) {
                List<Template> templates = TemplateCache.getTemplates("part");
                for (Template template : templates) {
                    append(sb,"""
                                                    <a class="dropdown-item" href="" onclick="return addPart($partId$,'$sectionName$','$partType$','$templateName$');">"$templateKey$"
                                                    </a>
                                    """,
                            Map.ofEntries(
                                    param("partId",partData.getId()),
                                    param("sectionName",partData.getSectionName()),
                                    param("partType",partType),
                                    param("templateName",template.getName()),
                                    param("templateKey",template.getKey())
                            )
                    );
                }
            } else {
                append(sb,"""
                                                    <a class="dropdown-item" href="" onclick="return addPart($partId$,'$sectionName$','$partType$');">$typeName$
                                                    </a>
                                """,
                        Map.ofEntries(
                                param("partId",partData.getId()),
                                param("sectionName",partData.getSectionName()),
                                param("partType",partType),
                                param("typeName",getHtml("class." + partType))
                        )
                );
            }
        }
        append(sb,"""
                                                </div>
                                            </div>
                                            <div class="btn-group btn-group-sm" role="group">
                                                <button type="button" class="btn  btn-secondary dropdown-toggle fa fa-ellipsis-h" data-toggle="dropdown" title="$more$"></button>
                                                <div class="dropdown-menu">
                                                    <a class="dropdown-item" href="" onclick="return movePart($partId$,-1);">$up$
                                                    </a>
                                                    <a class="dropdown-item" href="" onclick="return movePart($partId$,1);">$down$
                                                    </a>
                                                    <a class="dropdown-item" href="" onclick="if (confirmDelete()) return deletePart($partId$);">$delete$
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                        """,
                Map.ofEntries(
                        param("more","_more"),
                        param("partId",partData.getId()),
                        param("up","_up"),
                        param("down","_down"),
                        param("delete","_delete")
                )
        );
    }

    void appendPartStart(StringBuilder sb, PagePartData partData, String cssClass) {
        append(sb,"""
                        <div id="$wrapperId$" class="partWrapper $css$">
                        """,
                Map.ofEntries(
                        param("wrapperId",partData.getPartWrapperId()),
                        param("css",cssClass)
                )
        );
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata) {
        sb.append("</div>");
    }


}
