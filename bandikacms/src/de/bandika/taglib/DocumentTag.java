package de.bandika.taglib;

import de.bandika.page.fields.DocumentField;
import de.bandika.page.fields.BaseField;
import de.bandika.base.FormatHelper;
import de.bandika.base.Strings;
import de.bandika.data.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class DocumentTag extends TemplateBaseTag {

	DocumentField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(DocumentField)pdata.ensureField(name, BaseField.FIELDTYPE_DOCUMENT);
		try{
			JspWriter writer=context.getOut();
			writer.print("<input type=\"hidden\" id=\""+field.getIdentifier()+"DocId\" name=\""+field.getIdentifier()+"DocId\" value=\""+field.getDocId()+"\" />+" +
			"<input type=\"text\" id=\""+field.getIdentifier()+"Text\" name=\""+field.getIdentifier()+"Text\" value=\""+FormatHelper.toHtml(field.getText())+"\" />"+
			"<a href=\"#\" onclick=\"return openSetDocument('"+field.getIdentifier()+"');\">"+ Strings.getHtml("document")+"</a>");
		}
		catch (Exception ignored){
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(DocumentField)pdata.ensureField(name, BaseField.FIELDTYPE_DOCUMENT);
		try{
			JspWriter writer=context.getOut();
			writer.print("<a href=\"");
			if (field.getDocId() > 0) {
				writer.print("/_doc?method=show&did="+field.getDocId()+"\"");
			} else {
				writer.print("#\"");
			}
			writer.print(" target=\"_blank\">"+FormatHelper.toHtml(field.getText())+"</a>");
		}
		catch (Exception ignored){
		}
	}

}