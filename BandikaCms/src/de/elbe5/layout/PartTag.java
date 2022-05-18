package de.elbe5.layout;

import de.elbe5.base.Strings;
import de.elbe5.page.*;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class PartTag extends TemplateTag {

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
        sb.append(Strings.format("""
                        <div id="{1}" class="partWrapper {2}" title="{3}">
                        """,
                partData.getPartWrapperId(),
                Strings.toHtml(cssClass),
                Strings.toHtml(partData.getEditTitle())
        ));
        sb.append(Strings.format("""
                        <input type="hidden" name="{1}" value="{2}" class="partPos"/>
                                    <div class="partEditButtons">
                                        <div class="btn-group btn-group-sm" role="group">
                                            <div class="btn-group btn-group-sm" role="group">
                                                <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{3}"></button>
                                                <div class="dropdown-menu">
                                                """,
                partData.getPartPositionName(),
                Integer.toString(partData.getPosition()),
                Strings.getHtml("_newPart")
        ));
        for (String partType : PagePartFactory.getTypes()) {
            if (PagePartFactory.useLayouts(partType)) {
                List<Template> templates = TemplateCache.getTemplates("part");
                for (Template template : templates) {
                    sb.append(Strings.format("""
                                                    <a class="dropdown-item" href="" onclick="return addPart({1},'{2}','{3}','{4}');">{5}
                                                    </a>
                                    """,
                            Integer.toString(partData.getId()),
                            Strings.toHtml(partData.getSectionName()),
                            Strings.toHtml(partType),
                            Strings.toHtml(template.getName()),
                            Strings.getHtml(template.getKey())
                    ));
                }
            } else {
                sb.append(Strings.format("""
                                                    <a class="dropdown-item" href="" onclick="return addPart({1},'{2}','{3}');">{4}
                                                    </a>
                                """,
                        Integer.toString(partData.getId()),
                        Strings.toHtml(partData.getSectionName()),
                        Strings.toHtml(partType),
                        Strings.getHtml("class." + partType)
                ));
            }
        }
        sb.append(Strings.format("""
                                                </div>
                                            </div>
                                            <div class="btn-group btn-group-sm" role="group">
                                                <button type="button" class="btn  btn-secondary dropdown-toggle fa fa-ellipsis-h" data-toggle="dropdown" title="{1}"></button>
                                                <div class="dropdown-menu">
                                                    <a class="dropdown-item" href="" onclick="return movePart({2},-1);">{3}
                                                    </a>
                                                    <a class="dropdown-item" href="" onclick="return movePart({4},1);">{5}
                                                    </a>
                                                    <a class="dropdown-item" href="" onclick="if (confirmDelete()) return deletePart({6});">{7}
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                        """,
                Strings.getHtml("_more"),
                Integer.toString(partData.getId()),
                Strings.getHtml("_up"),
                Integer.toString(partData.getId()),
                Strings.getHtml("_down"),
                Integer.toString(partData.getId()),
                Strings.getHtml("_delete")
        ));
    }

    void appendPartStart(StringBuilder sb, PagePartData partData, String cssClass) {
        sb.append(Strings.format("""
                        <div id="{1}" class="partWrapper {2}">
                        """,
                partData.getPartWrapperId(),
                Strings.toHtml(cssClass)
        ));
    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata) {
        sb.append("</div>");
    }


}
