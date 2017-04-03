package de.bandika.taglib;

import de.bandika.page.fields.HtmlField;
import de.bandika.page.fields.BaseField;
import de.bandika.base.FormatHelper;
import de.bandika.data.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class HtmlTag extends TemplateBaseTag {

	HtmlField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(HtmlField)pdata.ensureField(name, BaseField.FIELDTYPE_HTML);
		try{
			JspWriter writer=context.getOut();
			writer.print("<textarea name=\"");
			writer.print(field.getIdentifier());
			writer.print("\" cols=\"100\" rows=\"10\">");
			writer.print(FormatHelper.toHtmlInput(field.getHtml()));
			writer.print("</textarea>");
      writer.print("<script type=\"text/javascript\">CKEDITOR.replace('");
			writer.print(field.getIdentifier());
			writer.print("',{customConfig : '/_statics/editorConfig.js'});</script>\n");
		}
		catch (Exception ignored){
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(HtmlField)pdata.ensureField(name, BaseField.FIELDTYPE_HTML);
		try{
			JspWriter writer=context.getOut();
			if (field.getHtml().length() == 0)
      	writer.print("&nbsp;");
			else
    		writer.print(field.getHtml());
		}
		catch (Exception ignored){
		}
	}

}