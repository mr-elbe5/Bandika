package de.bandika.taglib;

import de.bandika.page.fields.ImageField;
import de.bandika.page.fields.BaseField;
import de.bandika.base.FormatHelper;
import de.bandika.data.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class ImageTag extends TemplateBaseTag {

	ImageField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(ImageField)pdata.ensureField(name, BaseField.FIELDTYPE_IMAGE);
		try{
			JspWriter writer=context.getOut();
			writer.print("<input type=\"hidden\" id=\""+field.getIdentifier()+"ImgId\" name=\""+field.getIdentifier()+"ImgId\" value=\""+field.getImgId()+"\" />" +
			"<input type=\"hidden\" id=\""+field.getIdentifier()+"Width\" name=\""+field.getIdentifier()+"Width\" value=\""+field.getWidth()+"\" />" +
			"<input type=\"hidden\" id=\""+field.getIdentifier()+"Height\" name=\""+"Height\" value=\""+field.getHeight()+"\" />" +
			"<input type=\"hidden\" id=\""+field.getIdentifier()+"Alt\" name=\""+field.getIdentifier()+"Alt\" value=\""+FormatHelper.toHtml(field.getAltText())+"\" />"+
			"<a href=\"#\" onclick=\"return openSetImage('"+field.getIdentifier()+"');\">"+"<img id=\""+field.getIdentifier()+"\" src=\"");
			if (field.getImgId() > 0) {
				writer.print("/_image?method=show&iid="+field.getImgId()+"\" alt=\""+field.getAltText()+"\"");
				if (field.getWidth() > 0) {
					writer.print(" width=\""+field.getWidth()+"\"");
				}
				if (field.getHeight() > 0) {
					writer.print(" height=\""+field.getHeight()+"\"");
				}
			} else {
				writer.print("/_statics/images/dummy.gif\"");
			}
			writer.print("/></a>");
		}
		catch (Exception ignored){
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(ImageField)pdata.ensureField(name, BaseField.FIELDTYPE_IMAGE);
		try{
			JspWriter writer=context.getOut();
			if (field.getImgId() > 0) {
				writer.print("<img src=\"/_image?method=show&iid="+field.getImgId()+"\" alt=\""+field.getAltText()+"\"");
				if (field.getWidth() > 0) {
					writer.print(" width=\""+field.getWidth()+"\"");
				}
				if (field.getHeight() > 0) {
					writer.print(" height=\""+field.getHeight()+"\"");
				}
				writer.print("/>");
			} else {
				writer.print("&nbsp;");
			}
		}
		catch (Exception ignored){
		}
	}

}