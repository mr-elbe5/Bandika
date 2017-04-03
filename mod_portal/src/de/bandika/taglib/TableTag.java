/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika.application.AppConfiguration;
import de.bandika.data.StringCache;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.StringTokenizer;

public class TableTag extends BaseTag {

    private String id = "";
    private String checkId = "";
    private String formName = "";
    private String[] headerKeyArray = new String[0];
    protected Locale locale = null;

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
            locale = AppConfiguration.getInstance().getStdLocale();
        }
    }

    private static final String startStartTag = "" +
            "<table class=\"table table-striped table-bordered\" id=\"%s\"><thead><tr>";
    private static final String checkHeaderTag = "" +
            "<th><input type=\"checkbox\" onclick=\"toggleCheckboxes(this,'%s','%s');\" /></th>";
    private static final String headerTag = "<th>%s</th>";
    private static final String startEndTag = "</tr></thead><tbody>";
    private static final String endTag = "</tbody></table>";

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
        try {
            writer.print(endTag);
        } catch (IOException e) {
            throw new JspException(e);
        }
        return 0;
    }

}