package de.elbe5.serverpagetags;

import de.elbe5.content.ContentData;
import de.elbe5.page.LayoutPartData;
import de.elbe5.page.PageData;
import de.elbe5.page.PagePartData;
import de.elbe5.page.PartTextField;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

public class SPTextFieldTag extends SPFieldTag {

    public static final String TYPE = "textfield";

    private int rows = 1;

    public SPTextFieldTag(){
        this.type = TYPE;
    }

    @Override
    public void collectVariables(RequestData rdata) {
        super.collectVariables(rdata);
        rows = rdata.getPageAttributes().getInt("rows", 1);
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        PageData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        LayoutPartData partData = rdata.getAttributes().get(PagePartData.KEY_PART, LayoutPartData.class);
        PartTextField field = partData.ensureTextField(name);

        boolean editMode = contentData.getViewType().equals(ContentData.VIEW_TYPE_EDIT);
        String content = field.getContent();
        if (editMode) {
            if (rows > 1)
                sb.append(format("<textarea class=\"editField\" name=\"{1}\" rows=\"{2}\">{3}</textarea>",
                        field.getIdentifier(),
                        Integer.toString(rows),
                        toHtml(content.isEmpty() ? placeholder : content)));
            else
                sb.append(format("<input type=\"text\" class=\"editField\" name=\"{1}\" placeholder=\"{2}\" value=\"{3}\" />",
                        field.getIdentifier(),
                        field.getIdentifier(),
                        toHtml(content)));
        } else {
            if (content.length() == 0) {
                sb.append("&nbsp;");
            } else {
                sb.append(toHtmlMultiline(content));
            }
        }

    }

}
