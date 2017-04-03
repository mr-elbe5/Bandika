package de.bandika.taglib;

import de.bandika.page.fields.TextAreaField;
import de.bandika.page.fields.BaseField;
import de.bandika.base.FormatHelper;
import de.bandika.data.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class TextAreaTag extends TemplateBaseTag {

	TextAreaField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(TextAreaField)pdata.ensureField(name, BaseField.FIELDTYPE_TEXTAREA);
		try{
			JspWriter writer=context.getOut();
			writer.print("<textarea name=\""+field.getIdentifier()+"\" rows=\"5\" >"+FormatHelper.toHtmlInput(field.getText())+"</textarea>");
		}
		catch (Exception ignored){
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(TextAreaField)pdata.ensureField(name,BaseField.FIELDTYPE_TEXTAREA);
		try{
			JspWriter writer=context.getOut();
			if (field.getText().length() == 0)
      	writer.print("&nbsp;");
			else
    		writer.print(FormatHelper.toHtml(field.getText()));
		}
		catch (Exception ignored){
		}
	}

}