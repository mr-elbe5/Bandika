package de.bandika.http.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class UserTableTag extends BaseTag {

	public int doStartTag() throws JspException {
    JspWriter writer=context.getOut();
    try{
      writer.write("<div class=\"userArea\"><table class=\"userTable\">");
    }catch (IOException e){
      throw new JspException(e);
    }
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspException {
		JspWriter writer=context.getOut();
    try{
      writer.write("</table></div>");
    }catch (IOException e){
      throw new JspException(e);
    }
		return 0;
	}

}