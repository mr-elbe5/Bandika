package de.bandika.http.taglib;

import de.bandika.http.*;
import de.bandika.page.fields.ImageField;
import de.bandika.base.Formatter;
import de.bandika.image.ImageController;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class ImageTag extends TemplateBaseTag {

	ImageField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(ImageField)pdata.ensureField(name,"image");
		try{
			JspWriter writer=context.getOut();
			writer.print("<input type=\"hidden\" id=\""+field.getIdentifier()+"ImgId\" name=\""+field.getIdentifier()+"ImgId\" value=\""+field.getImgId()+"\" />" +
			"<input type=\"hidden\" id=\""+field.getIdentifier()+"Width\" name=\""+field.getIdentifier()+"Width\" value=\""+field.getWidth()+"\" />" +
			"<input type=\"hidden\" id=\""+field.getIdentifier()+"Height\" name=\""+"Height\" value=\""+field.getHeight()+"\" />" +
			"<input type=\"hidden\" id=\""+field.getIdentifier()+"Alt\" name=\""+field.getIdentifier()+"Alt\" value=\""+Formatter.toHtml(field.getAltText())+"\" />"+
			"<a href=\"#\" onclick=\"return openSetImage('"+field.getIdentifier()+"');\">"+"<img id=\""+field.getIdentifier()+"\" src=\"");
			if (field.getImgId() > 0) {
				writer.print("/srv25?ctrl="+ImageController.KEY_IMAGE+"&method=show&iid="+field.getImgId()+"\" alt=\""+field.getAltText()+"\"");
				if (field.getWidth() > 0) {
					writer.print(" width=\""+field.getWidth()+"\"");
				}
				if (field.getHeight() > 0) {
					writer.print(" height=\""+field.getHeight()+"\"");
				}
			} else {
				writer.print("/_img/dummy.gif\"");
			}
			writer.print("/></a>");
		}
		catch (Exception e){
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(ImageField)pdata.ensureField(name,"image");
		try{
			JspWriter writer=context.getOut();
			if (field.getImgId() > 0) {
				writer.print("<img src=\"/srv25?ctrl="+ImageController.KEY_IMAGE+"&method=show&iid="+field.getImgId()+"\" alt=\""+field.getAltText()+"\"");
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
		catch (Exception e){
		}
	}

}