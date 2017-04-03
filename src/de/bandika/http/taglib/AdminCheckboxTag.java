package de.bandika.http.taglib;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class AdminCheckboxTag extends AdminLineTag {

	boolean flag=false;

  public void setFlag(boolean flag){
    this.flag=flag;
  }

  protected void doInputTag(JspWriter writer) throws IOException {
    writer.print("<input type=\"checkbox\" name=\"");
    writer.print(name);
    writer.print("\" value=\"1\" ");
    if (flag)
      writer.print( " checked ");
    writer.print("/>");
  }

}