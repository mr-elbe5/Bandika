package de.bandika.http.taglib;

import de.bandika.http.*;
import de.bandika.page.fields.BlogField;
import de.bandika.page.ParagraphData;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class BlogTag extends TemplateBaseTag {

	BlogField field;

	public int doStartTag() throws JspException {
		RequestData rdata= HttpHelper.getRequestData((HttpServletRequest)context.getRequest());
		field=(BlogField)pdata.ensureField(name,"blog");
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