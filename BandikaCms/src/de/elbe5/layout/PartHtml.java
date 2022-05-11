package de.elbe5.layout;

import de.elbe5.base.Strings;
import de.elbe5.page.PagePartData;
import de.elbe5.page.PagePartFactory;

import java.util.List;

public class PartHtml {

    static public void appendPartEditStart(StringBuilder sb, PagePartData partData) {
        sb.append(Strings.format("""
                        <div id="{1}" class="partWrapper {2}" title="{3}">
                        """,
                partData.getPartWrapperId(),
                Strings.toHtml(partData.getCssClass()),
                Strings.toHtml(partData.getEditTitle())
        ));
        List<Template> templates = TemplateCache.getTemplates("part");
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

    static public void appendPartStart(StringBuilder sb, PagePartData partData) {
        sb.append(Strings.format("""
                        <div id="{1}" class="partWrapper {2}">
                        """,
                partData.getPartWrapperId(),
                Strings.toHtml(partData.getCssClass())
        ));
    }

    static public void appendPartEnd(StringBuilder sb) {
        sb.append("</div>");
    }

}
