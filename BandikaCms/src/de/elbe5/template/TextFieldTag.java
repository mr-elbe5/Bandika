package de.elbe5.template;

import de.elbe5.content.ContentData;
import de.elbe5.page.PageData;
import de.elbe5.page.PagePartData;
import de.elbe5.page.PartTextField;
import de.elbe5.page.TemplatePartData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.Map;

public class TextFieldTag extends TemplateTag {

    public static final String TYPE = "textfield";

    public TextFieldTag() {
        this.type = TYPE;
    }

    static final String textareaHtml = """
            <textarea class="editField" name="{{id}}" rows="{{rows}}">{{content}}</textarea>
            """;
    static final String inputHtml = """
            <input type="text" class="editField" name="{{id}}" placeholder="{{id}}" value="{{content}}" />
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        String name = getStringAttribute("name", "");
        String placeholder = getStringAttribute("placeholder", "");
        int rows = getIntAttribute("rows", 1);
        PageData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        TemplatePartData partData = rdata.getAttributes().get(PagePartData.KEY_PART, TemplatePartData.class);
        PartTextField field = partData.ensureTextField(name);

        boolean editMode = contentData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
        String content = field.getContent();
        if (editMode) {
            if (rows > 1) {
                append(sb, textareaHtml,
                        Map.ofEntries(
                                Map.entry("id", field.getIdentifier()),
                                Map.entry("rows", Integer.toString(rows)),
                                Map.entry("content", toHtml(content.isEmpty() ? placeholder : content))));
            } else
                append(sb, inputHtml,
                        Map.ofEntries(
                                Map.entry("id", field.getIdentifier()),
                                Map.entry("content", toHtml(content))));
        } else {
            if (content.length() == 0) {
                sb.append("&nbsp;");
            } else {
                sb.append(toHtmlMultiline(content));
            }
        }

    }

}
