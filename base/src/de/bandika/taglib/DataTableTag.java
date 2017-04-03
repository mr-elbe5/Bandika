/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika.application.Configuration;
import de.bandika.application.StringCache;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;

public class DataTableTag extends BaseTag {

  private String id = "";
  private String checkId = "";
  private String formName = "";
  private String[] headerKeyArray = new String[0];
  protected Locale locale = null;
  private boolean sort = false;
  private boolean paging = false;
  private int displayLength = -1;

  public void setId(String id) {
    this.id = id;
  }

  public void setCheckId(String checkId) {
    this.checkId = checkId;
  }

  public void setFormName(String formName) {
    this.formName = formName;
  }

  public void setHeaderKeys(String headerKeys) {
    StringTokenizer stk = new StringTokenizer(headerKeys, ",");
    headerKeyArray = new String[stk.countTokens()];
    int i = 0;
    while (stk.hasMoreTokens()) {
      headerKeyArray[i] = stk.nextToken();
      i++;
    }
  }

  public void setLocale(String localeName) {
    try {
      locale = new Locale(localeName);
    } catch (Exception e) {
      locale = Configuration.getStdLocale();
    }
  }

  public void setSort(boolean sort) {
    this.sort = sort;
  }

  public void setPaging(boolean paging) {
    this.paging = paging;
  }

  public void setDisplayLength(int displayLength) {
    this.displayLength = displayLength;
  }

  private static final String startStartTag = "" +
    "<table class=\"table table-striped table-bordered\" id=\"%s\"><thead>";
  private static final String checkHeaderTag = "" +
    "<th><input type=\"checkbox\" onclick=\"toggleCheckboxes(this,'%s','%s');\" /></th>";
  private static final String headerTag = "<th>%s</th>";
  private static final String startEndTag = "</thead><tbody>";
  private static final String endTag = "" +
    "</tbody></table>" +
    "<script type=\"text/javascript\">$('#%s').dataTable({%s " +
    "\"sDom\": '%s'," +
    "\"bSort\": %s," +
    "%s" +
    "\"bPaging\": %s," +
    "\"oLanguage\":{" +
    "\" sProcessing\":\"%s\"," +
    "\" sLengthMenu\":\"%s\"," +
    "\" sZeroRecords\":\"%s\"," +
    "\" sInfo\":\"%s\"," +
    "\" sInfoEmpty\":\"%s\"," +
    "\" sInfoFiltered\":\"%s\"," +
    "\" sSearch\":\"%s\"," +
    "\" oPaginate\":{" +
    "\"  sFirst\":\"%s\"," +
    "\"  sPrevious\":\"%s\"," +
    "\"  sNext\":\"%s\"," +
    "\"  sLast\":\"%s\"" +
    "}}});" +
    "$.extend( $.fn.dataTableExt.oStdClasses, {\"sWrapper\": \"dataTables_wrapper form-inline\"\n" +
    "} );" +
    "</script>";
  private static final String pagingTag = "" +
    "\"aLengthMenu\": [[%s -1], [%s \"%s\"]]," +
    "\"iDisplayLength\" : %s,";

  private String getSortTag() {
    if (sort && !checkId.equals("") && !formName.equals("")) {
      StringBuffer sb = new StringBuffer("\"aoColumns\" : [{ \"bSortable\": false }");
      for (String key : headerKeyArray)
        sb.append(",{ \"bSortable\": true }");
      sb.append(" ],");
      return sb.toString();
    }
    return "";
  }

  private String getDomTag() {
    if (paging) {
      return "ftp";
    }
    return "t";
  }

  public int doStartTag() throws JspException {
    JspWriter writer = getWriter();
    try {
      writer.print(String.format(startStartTag,
        id));
      if (!checkId.equals("") && !formName.equals(""))
        writer.print(String.format(checkHeaderTag, checkId, formName));
      for (String key : headerKeyArray)
        writer.write(String.format(headerTag, StringCache.getHtml(key, locale)));
      writer.write(startEndTag);
    } catch (IOException e) {
      throw new JspException(e);
    }
    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws JspException {
    JspWriter writer = getWriter();
    String dspl = displayLength + "," + (2 * displayLength) + "," + (3 * displayLength) + ",";
    try {
      writer.print(String.format(endTag,
        id,
        paging ?
          String.format(pagingTag,
            dspl,
            dspl,
            StringCache.getHtml("all"),
            displayLength) : "",
        getDomTag(),
        sort ? "true" : "false",
        getSortTag(),
        paging ? "true" : "false",
        StringCache.getHtml("table_sProcessing"),
        StringCache.getHtml("table_sLengthMenu"),
        StringCache.getHtml("table_sZeroRecords"),
        StringCache.getHtml("table_sInfo"),
        StringCache.getHtml("table_sInfoEmpty"),
        StringCache.getHtml("table_sInfoFiltered"),
        StringCache.getHtml("table_sSearch"),
        StringCache.getHtml("table_sFirst"),
        StringCache.getHtml("table_sPrevious"),
        StringCache.getHtml("table_sNext"),
        StringCache.getHtml("table_sLast")));
    } catch (IOException e) {
      throw new JspException(e);
    }
    return 0;
  }

}