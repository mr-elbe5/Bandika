package de.bandika.taglib;

import de.bandika.page.ParagraphData;
import de.bandika.page.fields.MailField;
import de.bandika.page.fields.BaseField;
import de.bandika.base.RequestHelper;
import de.bandika.data.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class MailTag extends TemplateBaseTag {

	MailField field;

	public int doStartTag() throws JspException {
		RequestData rdata= RequestHelper.getRequestData((HttpServletRequest)context.getRequest());
		pdata=(ParagraphData)rdata.getParam("pdata");
		field=(MailField)pdata.ensureField(name, BaseField.FIELDTYPE_MAIL);
		try{
			context.include("/jsp/communication/mailForm.jsp");
		}
		catch(Exception e){
		  throw new JspException(e);
		}
	  return SKIP_BODY;
	}

}