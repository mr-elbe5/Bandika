package de.bandika.taglib;

import de.bandika.page.ParagraphData;
import de.bandika.base.RequestHelper;
import de.bandika.data.RequestData;

import javax.servlet.jsp.JspException;
import javax.servlet.http.HttpServletRequest;

public class TemplateBaseTag extends BaseTag {

	String name="";
	ParagraphData pdata=null;

	public void setName(String name) {
		this.name=name;
	}

	public int doStartTag() throws JspException {
		RequestData rdata= RequestHelper.getRequestData((HttpServletRequest)context.getRequest());
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