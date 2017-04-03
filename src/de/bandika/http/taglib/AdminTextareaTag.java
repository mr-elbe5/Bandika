package de.bandika.http.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class AdminTextareaTag extends AdminLineTag {

  int cols=0;
  int rows=0;

  public void setCols(int cols) {
		this.cols=cols;
	}

  public void setRows(int rows) {
		this.rows=rows;
	}

  public int doStartTag() throws JspException {
		JspWriter writer=context.getOut();
    try{
      writer.print("<tr class=\"adminWhiteLine\"><td class=\"adminLeft\">");
      writer.print(label);
      writer.print("</td><td class=\"adminRight\"><textarea class=\"adminInput\" name=\"");
      writer.print(name);
      if (cols!=0){
        writer.print("\" cols=\"");
        writer.print(cols);
      }
      if (rows!=0){
        writer.print("\" rows=\"");
        writer.print(rows);
      }
      writer.print("\">");
    }
    catch (Exception e){
      throw new JspException(e);
    }
	  return EVAL_BODY_INCLUDE;
	}

  public int doEndTag() throws JspException {
    JspWriter writer=context.getOut();
    try{
      writer.print("</textarea></td></tr>");
    }
    catch (Exception e){
      throw new JspException(e);
    }
    return 0;
  }

}