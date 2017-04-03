package de.bandika.http.taglib;

import de.bandika.http.*;
import de.bandika.page.ParagraphData;
import de.bandika.page.fields.MailField;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class MailTag extends TemplateBaseTag {

	MailField field;

	public int doStartTag() throws JspException {
		RequestData rdata= HttpHelper.getRequestData((HttpServletRequest)context.getRequest());
		pdata=(ParagraphData)rdata.getParam("pdata");
		field=(MailField)pdata.ensureField(name,"mail");
		try{
			context.include("/jsp/communication/mailForm.jsp");
		}
		catch(Exception e){
		  throw new JspException(e);
		}
	  return SKIP_BODY;
	}

}