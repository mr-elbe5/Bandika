package de.bandika.http.taglib;

import de.bandika.page.fields.HtmlField;
import de.bandika.base.Formatter;
import de.bandika.http.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class HtmlTag extends TemplateBaseTag {

	HtmlField field;

	@Override
	protected void doEditTag(RequestData rdata) throws JspException {
		field=(HtmlField)pdata.ensureField(name,"html");
		try{
			JspWriter writer=context.getOut();
			writer.print("\n<script type=\"text/javascript\" src=\"/_statics/ckeditor/ckeditor.js\"></script>\n");
      writer.print("\n<script type=\"text/javascript\" src=\"/_statics/startckeditor.js\"></script>\n");
			writer.print("<textarea name=\"");
			writer.print(field.getIdentifier());
			writer.print("\" cols=\"100\" rows=\"10\">");
			writer.print(Formatter.toHtmlInput(field.getHtml()));
			writer.print("</textarea>");
      writer.print("<script type=\"text/javascript\">CKEDITOR.replace('");
			writer.print(field.getIdentifier());
			writer.print("',{customConfig : '/_statics/editorConfig.js'});</script>\n");
		}
		catch (Exception e){
		}
	}

	@Override
	protected void doRuntimeTag(RequestData rdata) throws JspException {
		field=(HtmlField)pdata.ensureField(name,"html");
		try{
			JspWriter writer=context.getOut();
			if (field.getHtml().length() == 0)
      	writer.print("&nbsp;");
			else
    		writer.print(field.getHtml());
		}
		catch (Exception e){
		}
	}

}