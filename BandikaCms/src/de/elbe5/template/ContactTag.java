package de.elbe5.template;

import de.elbe5.page.PageData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.response.IFormBuilder;

import java.util.Date;
import java.util.Map;

public class ContactTag extends TemplateTag implements IFormBuilder {

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

    public void appendContactHtml(StringBuilder sb, RequestData rdata, int contentId, String contactName, String contactEmail, String contactMessage, String cssClass){
        append(sb,"""
                <div class="$cssClass$">
                """,
                Map.ofEntries(
                        param("cssClass",cssClass)
                )
        );
        appendFormStart(sb, "/ctrl/ctrl/sendContact/" + contentId, "contactform");
        appendFormError(sb, rdata);
        appendTextInputLine(sb, rdata.hasFormErrorField("contactName"), toHtml("contactName"), getHtml("_name"), true, toHtml(contactName),0);
        appendTextInputLine(sb, rdata.hasFormErrorField("contactEmail"), toHtml("contactEmail"), getHtml("_email"), true, toHtml(contactEmail));
        appendTextareaLine(sb, rdata.hasFormErrorField("contactMessage"), toHtml("contactMessage"), getHtml("_message"), true, toHtml(contactMessage),"10rem");
        appendLineStart(sb, "", "");
        append(sb,"""
                <img src="/ctrl/user/showCaptcha?v=$date$" alt="" />
        """,
                Map.ofEntries(
                        param("date",Long.toString(new Date().getTime()))
                )
        );
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("captcha"), toHtml("captcha"), getHtml("_captcha"), true, "");
        appendLineStart(sb, "", "");
        append(sb,"""
                <div>$hint$</div>
        """,
                Map.ofEntries(
                        param("hint","_captchaHint")
                )
        );
        appendLineEnd(sb);
        append(sb,"""
                            <div class="form-group row">
                                <div class = "col-md-12">
                                    <button type="submit" class="btn btn-outline-primary pull-right">$send$
                                    </button>
                                </div>
                            </div>
                """,
                Map.ofEntries(
                        param("send","_send")
                )
        );
        appendFormEnd(sb);
        sb.append("</div>");
    }

}
