package de.bandika.taglib;

import de.bandika.base.Strings;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;

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
      writer.print("<a href=\"#\" onclick=\"return openTemplateSelectLayer("+idx+");\"><img src=\"/_statics/images/new.gif\" alt=\"" + Strings.getHtml("new") + "\"></a>");
      if (!endTag) {
        writer.print("<a href=\"/_page?method=editParagraph&id="+id+"&idx="+idx+"\"><img src=\"/_statics/images/edit.gif\" alt=\"" + Strings.getHtml("change") + "\"></a>");
        writer.print("<a href=\"/_page?method=moveParagraph&id="+id+"&idx="+idx+"&dir=-1\"><img src=\"/_statics/images/arrup.gif\" alt=\"" + Strings.getHtml("up") + "\"></a>");
        writer.print("<a href=\"/_page?method=moveParagraph&id="+id+"&idx="+idx+"&dir=1\"><img src=\"/_statics/images/arrdn.gif\" alt=\"" + Strings.getHtml("down") + "\"></a>");
        writer.print("<a href=\"/_page?method=deleteParagraph&id="+id+"&idx="+idx+"\"><img src=\"/_statics/images/del.gif\" alt=\"" + Strings.getHtml("delete") + "\"></a>");
      }
      writer.print("</div>");
    }
    catch (Exception e){
      throw new JspException(e);
    }
	  return SKIP_BODY;
	}

}