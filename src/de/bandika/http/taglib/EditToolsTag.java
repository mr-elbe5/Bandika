package de.bandika.http.taglib;

import de.bandika.base.AdminStrings;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;
import java.io.IOException;

public class EditToolsTag extends BaseTag {

	int id=0;
  int idx=0;
  boolean endTag=false;

  public void setId(int id) {
		this.id=id;
	}

  public void setIdx(int idx) {
		this.idx=idx;
	}

  public void setEndTag(boolean endTag){
    this.endTag=endTag;
  }

  public int doStartTag() throws JspException {
		JspWriter writer=context.getOut();
    try{
      writer.print("<div class=\"editTools\">");
      writer.print("<a href=\"#\" onclick=\"return openTemplateSelectLayer("+idx+");\"><img src=\"/_statics/images/new.gif\" alt=\"" + AdminStrings._new + "\"></a>");
      if (!endTag) {
        writer.print("<a href=\"/_jsp/pageEditContent.jsp?ctrl=page&method=editParagraph&id="+id+"&idx="+idx+"\"><img src=\"/_statics/images/edit.gif\" alt=\"" + AdminStrings.change + "\"></a>");
        writer.print("<a href=\"/_jsp/pageEditContent.jsp?ctrl=page&method=moveParagraph&id="+id+"&idx="+idx+"&dir=-1\"><img src=\"/_statics/images/arrup.gif\" alt=\"" + AdminStrings.up + "\"></a>");
        writer.print("<a href=\"/_jsp/pageEditContent.jsp?ctrl=page&method=moveParagraph&id="+id+"&idx="+idx+"&dir=1\"><img src=\"/_statics/images/arrdn.gif\" alt=\"" + AdminStrings.down + "\"></a>");
        writer.print("<a href=\"/_jsp/pageEditContent.jsp?ctrl=page&method=deleteParagraph&id="+id+"&idx="+idx+"\"><img src=\"/_statics/images/del.gif\" alt=\"" + AdminStrings.delete + "\"></a>");
      }
      writer.print("</div>");
    }
    catch (Exception e){
      throw new JspException(e);
    }
	  return SKIP_BODY;
	}

}