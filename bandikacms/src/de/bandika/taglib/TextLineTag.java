package de.bandika.taglib;

import de.bandika.base.FormatHelper;
import de.bandika.base.Logger;
import de.bandika.page.fields.TextLineField;
import de.bandika.page.fields.BaseField;
import de.bandika.data.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class TextLineTag extends TemplateBaseTag {

	TextLineField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(TextLineField)pdata.ensureField(name, BaseField.FIELDTYPE_TEXTLINE);
		try{
			JspWriter writer=context.getOut();
			writer.print("<input type=\"text\" name=\""+field.getIdentifier()+"\" value=\""+FormatHelper.toHtml(field.getText())+"\">");
		}
		catch (Exception e){
			Logger.error(null,"textline tag error",e);
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(TextLineField)pdata.ensureField(name,BaseField.FIELDTYPE_TEXTLINE);
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