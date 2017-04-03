package de.bandika.taglib;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class UserPasswordInputTag extends AdminLineTag {

	String text="";
  int maxlength=0;

  public void setText(String text) {
		this.text=text;
	}

  public void setMaxlength(int maxlength) {
		this.maxlength=maxlength;
	}

  protected void doInputTag(JspWriter writer) throws IOException {
    writer.print("<input class=\"userInput\" type=\"password\" name=\"");
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