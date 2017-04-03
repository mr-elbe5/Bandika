package de.bandika.http.taglib;

import de.bandika.http.*;
import de.bandika.page.fields.DocumentField;
import de.bandika.base.Formatter;
import de.bandika.base.UserStrings;
import de.bandika.document.DocumentController;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class DocumentTag extends TemplateBaseTag {

	DocumentField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(DocumentField)pdata.ensureField(name,"document");
		try{
			JspWriter writer=context.getOut();
			writer.print("<input type=\"hidden\" id=\""+field.getIdentifier()+"DocId\" name=\""+field.getIdentifier()+"DocId\" value=\""+field.getDocId()+"\" />+" +
			"<input type=\"text\" id=\""+field.getIdentifier()+"Text\" name=\""+field.getIdentifier()+"Text\" value=\""+Formatter.toHtml(field.getText())+"\" />"+
			"<a href=\"#\" onclick=\"return openSetDocument('"+field.getIdentifier()+"');\">"+ UserStrings.document+"</a>");
		}
		catch (Exception ignored){
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(DocumentField)pdata.ensureField(name,"document");
		try{
			JspWriter writer=context.getOut();
			writer.print("<a href=\"");
			if (field.getDocId() > 0) {
				writer.print("/srv25?ctrl="+DocumentController.KEY_DOCUMENT+"&method=show&did="+field.getDocId()+"\"");
			} else {
				writer.print("#\"");
			}
			writer.print(" target=\"_blank\">"+Formatter.toHtml(field.getText())+"</a>");
		}
		catch (Exception ignored){
		}
	}

}