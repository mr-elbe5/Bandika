package de.bandika.http.taglib;

import de.bandika.http.RequestData;
import de.bandika.http.HttpHelper;
import de.bandika.page.ParagraphData;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class TemplateBaseTag extends BaseTag {

	String name="";
	ParagraphData pdata=null;

	public void setName(String name) {
		this.name=name;
	}

	public int doStartTag() throws JspException {
		RequestData rdata= HttpHelper.getRequestData((HttpServletRequest)context.getRequest());
		boolean editMode=rdata.getParamInt("editMode",0)==1;
		pdata=(ParagraphData)rdata.getParam("pdata");
		if (editMode)
		  doEditTag(rdata);
		else
		  doRuntimeTag(rdata);
	  return SKIP_BODY;
	}

	protected void doEditTag(RequestData rdata) throws JspException {
	}

	protected void doRuntimeTag(RequestData rdata) throws JspException {
	}

}