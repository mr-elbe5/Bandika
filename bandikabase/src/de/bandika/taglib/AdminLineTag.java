package de.bandika.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class AdminLineTag extends BaseTag {

	String label="";
  String name="";
  boolean mandatory=false;

	public void setLabel(String label) {
		this.label=label;
	}

  public void setName(String name) {
    this.name=name;
  }

  public void setMandatory(boolean flag) {
    this.mandatory=flag;
  }

	public int doStartTag() throws JspException {
		JspWriter writer=context.getOut();
    try{
      writer.print("<tr class=\"adminWhiteLine\"><td class=\"adminLeft\">");
      writer.print(label);
      if (mandatory)
        writer.print("&nbsp;*");
      writer.print("</td><td class=\"adminRight\">");
      doInputTag(writer);
      writer.print("</td></tr>");
    }
    catch (Exception e){
      throw new JspException(e);
    }
	  return SKIP_BODY;
	}

  protected void doInputTag(JspWriter writer) throws IOException {
  }

}