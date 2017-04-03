package de.bandika.taglib;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class AdminTextTag extends AdminLineTag {

	String text="";

  public void setText(String text) {
		this.text=text;
	}

  protected void doInputTag(JspWriter writer) throws IOException {
    writer.print(text);
  }

}