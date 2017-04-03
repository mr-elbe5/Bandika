package de.bandika.taglib;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;

public class BaseTag implements Tag {

	Tag parent=null;
	PageContext context=null;

	public void setPageContext(PageContext pageContext) {
		context=pageContext;
	}

	public void setParent(Tag tag) {
		parent=tag;
	}

	public Tag getParent() {
		return parent;
	}

	public int doStartTag() throws JspException {
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		return 0;
	}

	public void release() {
	}
}