package de.bandika.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class PagerTag extends TemplateBaseTag {

	int page=0;
  int maxPage=0;

  public void setPage(int page){
    this.page=page;
  }

  public void setMaxPage(int maxPage){
    this.maxPage=maxPage;
  }

	public int doStartTag() throws JspException {
		JspWriter writer=context.getOut();
    try{
      writer.print("<div class=\"hline\">&nbsp;</div>");
      writer.print("<div class=\"pager\">");
      writer.print("<a href=\"#\" onclick=\"return previousPage();\">&lt;</a>");
      for (int i=0;i<=maxPage;i++){
        if (page==i)
          writer.print("<span class=\"pagerCurrentPage\">"+(i+1)+"</span>");
        else
          writer.print("<a href=\"#\" onclick=\"return toPage("+i+");\">"+(i+1)+"</a>");
      }
      writer.print("<a href=\"#\" onclick=\"return nextPage();\">&gt;</a>");
      writer.print("</div>");
      writer.print("<div class=\"hline\">&nbsp;</div>");
    }
    catch (Exception e){
      throw new JspException(e);
    }
	  return SKIP_BODY;
	}

}