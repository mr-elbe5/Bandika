package de.bandika.cms.taglib;

import de.bandika.application.StringCache;
import de.bandika.cms.LinkField;
import de.bandika.cms.BaseField;
import de.bandika._base.*;
import de.bandika.page.PageData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;

public class LinkTag extends CmsBaseTag {

  protected LinkField field;
  protected String type = "page";
  protected String availableTypes = "page,document,image";

  public void setType(String type) {
    this.type = type;
  }

  public void setAvailableTypes(String availableTypes) {
    this.availableTypes = availableTypes;
  }

  private static final String editTag = "" +
    "<fieldset class=\"editTagFields\">" +
    "<label>%s</label>" +
    "<div class=\"input-append\">" +
    "<input type=\"text\" id=\"%sLink\" name=\"%sLink\" value=\"%s\" />" +
    "<button class=\"btn\" onclick=\"return openSetLink('%s','%s','%s',%s);\" type=\"button\">%s</button>" +
    "</div>" +
    "<label>%s</label>" +
    "<input type=\"text\" id=\"%sTarget\" name=\"%sTarget\" value=\"%s\" />" +
    "<label>%s</label>" +
    "<input type=\"text\" id=\"%sText\" name=\"%sText\" value=\"%s\" />" +
    "</fieldset>";
  private static final String runtimeTag = "<a href=\"%s\" %s>%s</a>";

  @Override
  protected void doEditTag(RequestData rdata) throws JspException {
    SessionData sdata = RequestHelper.getSessionData((HttpServletRequest) getContext().getRequest());
    PageData data = (PageData) sdata.getParam("pageData");
    int pageId = data == null ? 0 : data.getId();
    field = (LinkField) pdata.ensureField(name, BaseField.FIELDTYPE_LINK);
    try {
      JspWriter writer = getWriter();
      writer.print(String.format(editTag,
        StringCache.getHtml("link"),
        field.getIdentifier(),
        field.getIdentifier(),
        field.getLink(),
        field.getIdentifier(),
        type,
        availableTypes,
        pageId,
        StringCache.getHtml("browse"),
        StringCache.getHtml("target"),
        field.getIdentifier(),
        field.getIdentifier(),
        field.getTarget(),
        StringCache.getHtml("text"),
        field.getIdentifier(),
        field.getIdentifier(),
        field.getText()));
    } catch (Exception ignored) {
    }
  }

  @Override
  protected void doRuntimeTag(RequestData rdata) throws JspException {
    field = (LinkField) pdata.ensureField(name, BaseField.FIELDTYPE_LINK);
    try {
      JspWriter writer = getWriter();
      writer.print(String.format(runtimeTag,
        field.getLink(),
        field.getTarget().equals("") ? "" : "target=" + field.getTarget() + "\"",
        FormatHelper.toHtml(field.getText())));
    } catch (Exception ignored) {
    }
  }

}
