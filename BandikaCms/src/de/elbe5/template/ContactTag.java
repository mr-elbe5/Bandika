package de.elbe5.template;

import de.elbe5.base.Strings;
import de.elbe5.response.FormHtml;
import de.elbe5.page.PageData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.Date;

public class ContactTag extends TemplateTag {

    public static final String TYPE = "contact";

    public ContactTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        PageData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
        String contactName = rdata.getAttributes().getString("contactName");
        String contactEmail = rdata.getAttributes().getString("contactEmail");
        String contactMessage = rdata.getAttributes().getString("contactMessage");
        String cssClass = getStringParam("cssClass", rdata, "");
        appendContactHtml(sb, rdata, contentData.getId(), contactName, contactEmail, contactMessage, cssClass);
    }

    static public void appendContactHtml(StringBuilder sb, RequestData rdata, int contentId, String contactName, String contactEmail, String contactMessage, String cssClass){
        sb.append(Strings.format("""
                <div class="{1}">
                """,
                Strings.toHtml(cssClass)));
        FormHtml.appendFormStart(sb, "/ctrl/ctrl/sendContact/" + contentId, "contactform");
        FormHtml.appendFormError(sb, rdata);
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("contactName"), Strings.toHtml("contactName"), Strings.getHtml("_name"), true, Strings.toHtml(contactName),0);
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("contactEmail"), Strings.toHtml("contactEmail"), Strings.getHtml("_email"), true, Strings.toHtml(contactEmail));
        FormHtml.appendTextareaLine(sb, rdata.hasFormErrorField("contactMessage"), Strings.toHtml("contactMessage"), Strings.getHtml("_message"), true, Strings.toHtml(contactMessage),"10rem");
        FormHtml.appendLineStart(sb, "", "");
        sb.append(Strings.format("""
                <img src="/ctrl/user/showCaptcha?v={1}" alt="" />
        """,
                Long.toString(new Date().getTime())));
        FormHtml.appendLineEnd(sb);
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("captcha"), Strings.toHtml("captcha"), Strings.getHtml("_captcha"), true, "");
        FormHtml.appendLineStart(sb, "", "");
        sb.append(Strings.format("""
                <div>{1}</div>
        """,
                Strings.getHtml("_captchaHint")));
        FormHtml.appendLineEnd(sb);
        sb.append(Strings.format("""
                            <div class="form-group row">
                                <div class = "col-md-12">
                                    <button type="submit" class="btn btn-outline-primary pull-right">{1}
                                    </button>
                                </div>
                            </div>
                """,
                Strings.getHtml("_send")));
        FormHtml.appendFormEnd(sb);
        sb.append("</div>");
    }

}
