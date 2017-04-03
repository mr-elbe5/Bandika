package de.bandika.taglib;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class AdminFileInputTag extends AdminLineTag {

	int maxlength=0;
  int size=0;

  public void setSize(int size) {
		this.size=size;
	}

  public void setMaxlength(int maxlength) {
		this.maxlength=maxlength;
	}

  protected void doInputTag(JspWriter writer) throws IOException {
    writer.print("<input class=\"adminInput\" type=\"file\" name=\"");
    writer.print(name);
    writer.print("\" value=\"");
    if (size!=0){
      writer.print("\" size=\"");
      writer.print(size);
    }
    if (maxlength!=0){
      writer.print("\" maxlength=\"");
      writer.print(maxlength);
    }
    writer.print("\" />");
  }

}