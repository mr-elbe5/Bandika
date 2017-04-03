/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.taglib;

import de.bandika.application.AppConfiguration;
import de.bandika.data.StringFormat;
import de.bandika.data.StringCache;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.util.Locale;

public class ControlGroupTag extends BaseTag {

    protected String name = "";
    protected String label = "";
    protected String labelKey = "";
    protected Locale locale = null;
    protected boolean padded = false;

    private boolean mandatory = false;

    public void setName(String name) {
        this.name = name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    public void setLocale(String localeName) {
        try {
            locale = new Locale(localeName);
        } catch (Exception e) {
            locale = AppConfiguration.getInstance().getStdLocale();
        }
    }

    public void setMandatory(boolean flag) {
        this.mandatory = flag;
    }

    public void setPadded(boolean padded) {
        this.padded = padded;
    }

    private static final String startTag = "<div class=\"control-group\"><label class=\"control-label\" %s >%s%s</label><div class=\"controls\">";
    private static final String endTag = "</div></div>";
    private static final String paddingStartTag = "<div class=\"control-pad\">";
    private static final String paddingEndTag = "</div>";

    public int doStartTag() throws JspException {
        JspWriter writer = getWriter();
        try {
            writer.print(String.format(startTag,
                    name.length() > 0 ? "for=\"" + StringFormat.toHtml(name) + "\"" : "",
                    label.length() > 0 ? StringFormat.toHtml(label) : StringCache.getHtml(labelKey, locale),
                    mandatory ? "&nbsp;*" : ""));
            if (padded)
                writer.print(paddingStartTag);
        } catch (Exception e) {
            throw new JspException(e);
        }
        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        JspWriter writer = getWriter();
        try {
            if (padded)
                writer.print(paddingEndTag);
            writer.println(endTag);
        } catch (Exception e) {
            throw new JspException(e);
        }
        return 0;
    }

}

