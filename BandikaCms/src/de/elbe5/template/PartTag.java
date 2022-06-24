package de.elbe5.template;

import de.elbe5.page.*;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.response.IFormBuilder;

import java.util.List;
import java.util.Map;

public class PartTag extends TemplateTag implements IFormBuilder {

    public static final String TYPE = "part";

    public PartTag() {
        this.type = TYPE;
    }

    static final String editStartHtml = """
            <div id="{{wrapperId}}" class="partWrapper {{cssClass}}" title="{{editTitle}}">
            """;
    static final String editButtonsStartHtml = """
                <div class="partEditButtons">
                    <div class="btn-group btn-group-sm" role="group">
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{{_newPart}}"></button>
                            <div class="dropdown-menu">
            """;
    static final String addTemplateHtml = """
                                <a class="dropdown-item" href="" onclick="return addPart({{partId}},'{{sectionName}}','{{partType}}','{{templateName}}');">"{{templateKey}}"
                                </a>
            """;
    static final String addHtml = """
                                <a class="dropdown-item" href="" onclick="return addPart({{partId}},'{{sectionName}}','{{partType}}');">{{typeName}}
                                </a>
            """;
    static final String editButtonsEndHtml = """
                            </div>
                        </div>
                        <div class="btn-group btn-group-sm" role="group">
                            <button type="button" class="btn  btn-secondary dropdown-toggle fa fa-ellipsis-h" data-toggle="dropdown" title="{{_more}}"></button>
                            <div class="dropdown-menu">
                                <a class="dropdown-item" href="" onclick="return movePart({{partId}},-1);">{{_up}}
                                </a>
                                <a class="dropdown-item" href="" onclick="return movePart({{partId}},1);">{{_down}}
                                </a>
                                <a class="dropdown-item" href="" onclick="if (confirmDelete()) return deletePart({{partId}});">{{_delete}}
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            """;
    static final String partStartHtml = """
            <div id="{{wrapperId}}" class="partWrapper {{css}}">
            """;

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata) {
        PageData pageData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        TemplatePartData part = rdata.getAttributes().get(PagePartData.KEY_PART, TemplatePartData.class);
        if (pageData == null || part == null)
            return;
        String cssClass = getStringAttribute("cssClass", "");
        if (pageData.isEditing()) {
            appendPartEditStart(sb, part, cssClass);
        } else {
            appendPartStart(sb, part, cssClass);
        }
    }

    void appendPartEditStart(StringBuilder sb, PagePartData partData, String cssClass) {
        append(sb, editStartHtml,
                Map.ofEntries(
                        Map.entry("wrapperId", partData.getPartWrapperId()),
                        Map.entry("cssClass", cssClass),
                        Map.entry("editTitle", toHtml(partData.getEditTitle()))));
        appendHiddenField(sb, partData.getPartPositionName(), Integer.toString(partData.getPosition()));
        append(sb, editButtonsStartHtml, null);
        for (String partType : PagePartFactory.getTypes()) {
            if (PagePartFactory.useLayouts(partType)) {
                List<Template> templates = TemplateCache.getInstance().getTemplates("part");
                for (Template template : templates) {
                    append(sb, addTemplateHtml,
                            Map.ofEntries(
                                    Map.entry("partId", Integer.toString(partData.getId())),
                                    Map.entry("sectionName", toHtml(partData.getSectionName())),
                                    Map.entry("partType", partType),
                                    Map.entry("templateName", toHtml(template.getName())),
                                    Map.entry("templateKey", template.getKey())));
                }
            } else {
                append(sb, addHtml,
                        Map.ofEntries(
                                Map.entry("partId", Integer.toString(partData.getId())),
                                Map.entry("sectionName", toHtml(partData.getSectionName())),
                                Map.entry("partType", partType),
                                Map.entry("typeName", getHtml("class." + partType))));
            }
        }
        append(sb, editButtonsEndHtml,
                Map.ofEntries(
                        Map.entry("partId", Integer.toString(partData.getId()))));
    }

    void appendPartStart(StringBuilder sb, PagePartData partData, String cssClass) {
        append(sb, partStartHtml,
                Map.ofEntries(
                        Map.entry("wrapperId", partData.getPartWrapperId()),
                        Map.entry("css", cssClass)));
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata) {
        sb.append("</div>");
    }


}
