package de.elbe5.template;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentData;
import de.elbe5.page.PageData;
import de.elbe5.page.PagePartData;
import de.elbe5.page.PartHtmlField;
import de.elbe5.page.TemplatePartData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

public class HtmlFieldTag extends TemplateTag {

    public static final String TYPE = "htmlfield";

    public HtmlFieldTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        String name = getStringParam("name", rdata, "");
        String placeholder = getStringParam("placeholder", rdata, "");
        PageData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        TemplatePartData partData = (TemplatePartData) rdata.getAttributes().get(PagePartData.KEY_PART);
        PartHtmlField field = partData.ensureHtmlField(name);
        boolean editMode = contentData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
        if (editMode) {
            sb.append(Strings.format("""
                    <div class="ckeditField" id="{1}" contenteditable="true">{2}</div>
                    <input type="hidden" name="{3}" value="{4}" />
                    <script type="text/javascript">
                        $('#{5}').ckeditor({toolbar : 'Full',filebrowserBrowseUrl : '/ctrl/ckeditor/openLinkBrowser?contentId={6}',filebrowserImageBrowseUrl : '/ctrl/ckeditor/openImageBrowser?contentId={7}'});
                    </script>""",
                    field.getIdentifier(),
                    field.getContent().isEmpty() ? Strings.toHtml(placeholder) : field.getContent(),
                    field.getIdentifier(),
                    Strings.toHtml(field.getContent()),
                    field.getIdentifier(),
                    Integer.toString(contentData.getId()),
                    Integer.toString(contentData.getId())
            ));
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
