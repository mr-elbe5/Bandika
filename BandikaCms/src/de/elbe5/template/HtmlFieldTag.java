package de.elbe5.template;

import de.elbe5.content.ContentData;
import de.elbe5.page.PageData;
import de.elbe5.page.PagePartData;
import de.elbe5.page.PartHtmlField;
import de.elbe5.page.TemplatePartData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.Map;

public class HtmlFieldTag extends TemplateTag {

    public static final String TYPE = "htmlfield";

    public HtmlFieldTag() {
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        String name = getStringAttribute("name", "");
        String placeholder = getStringAttribute("placeholder", "");
        PageData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        TemplatePartData partData = (TemplatePartData) rdata.getAttributes().get(PagePartData.KEY_PART);
        PartHtmlField field = partData.ensureHtmlField(name);
        boolean editMode = contentData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
        if (editMode) {
            append(sb, """
                            <div class="ckeditField" id="{{identifier}}" contenteditable="true">{{placeholder}}</div>
                            <input type="hidden" name="{{identifier}}" value="{{content}}" />
                            <script type="text/javascript">
                                $('#{{identifier}}').ckeditor({toolbar : 'Full',filebrowserBrowseUrl : '/ctrl/ckeditor/openLinkBrowser?contentId={{id}}',filebrowserImageBrowseUrl : '/ctrl/ckeditor/openImageBrowser?contentId={{id}}'});
                            </script>""",
                    Map.ofEntries(
                            Map.entry("identifier", field.getIdentifier()),
                            Map.entry("placeholder", field.getContent().isEmpty() ? toHtml(placeholder) : field.getContent()),
                            Map.entry("content", toHtml(field.getContent())),
                            Map.entry("id", Integer.toString(contentData.getId()))));
        } else {
            try {
                if (!field.getContent().isEmpty()) {
                    sb.append(field.getContent());
                }
            } catch (Exception ignored) {
            }
        }
    }

}
