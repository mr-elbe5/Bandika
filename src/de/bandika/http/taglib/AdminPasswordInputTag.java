package de.bandika.http.taglib;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class AdminPasswordInputTag extends AdminLineTag {

	String text="";
  int maxlength=0;

  public void setText(String text) {
		this.text=text;
	}

  public void setMaxlength(int maxlength) {
		this.maxlength=maxlength;
	}

  protected void doInputTag(JspWriter writer) throws IOException {
    writer.print("<input class=\"adminInput\" type=\"password\" name=\"");
    writer.print(name);
    writer.print("\" value=\"");
    writer.print(text);
    if (maxlength!=0){
      writer.print("\" maxlength=\"");
      writer.print(maxlength);
    }
    writer.print("\" />");
  }

}