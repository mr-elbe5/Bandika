package de.bandika.taglib;

import de.bandika.page.fields.BlogField;
import de.bandika.page.fields.BaseField;
import de.bandika.page.ParagraphData;
import de.bandika.base.RequestHelper;
import de.bandika.data.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class BlogTag extends TemplateBaseTag {

	BlogField field;

	public int doStartTag() throws JspException {
		RequestData rdata= RequestHelper.getRequestData((HttpServletRequest)context.getRequest());
		field=(BlogField)pdata.ensureField(name, BaseField.FIELDTYPE_BLOG);
		pdata=(ParagraphData)rdata.getParam("pdata");
		try{
			context.include("/jsp/communication/blogForm.jsp");
		}
		catch(Exception e){
		  throw new JspException(e);
		}
	  return SKIP_BODY;
	}

}