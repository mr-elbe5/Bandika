package de.bandika.http.taglib;

import de.bandika.http.*;
import de.bandika.page.fields.TextAreaField;
import de.bandika.base.Formatter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class TextAreaTag extends TemplateBaseTag {

	TextAreaField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(TextAreaField)pdata.ensureField(name,"textarea");
		try{
			JspWriter writer=context.getOut();
			writer.print("<textarea name=\""+field.getIdentifier()+"\" rows=\"5\" >"+Formatter.toHtmlInput(field.getText())+"</textarea>");
		}
		catch (Exception e){
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(TextAreaField)pdata.ensureField(name,"textarea");
		try{
			JspWriter writer=context.getOut();
			if (field.getText().length() == 0)
      	writer.print("&nbsp;");
			else
    		writer.print(Formatter.toHtml(field.getText()));
		}
		catch (Exception e){
		}
	}

}