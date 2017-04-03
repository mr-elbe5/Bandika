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

public class ControlTextTag extends BaseTag {

    protected String key = "";
    protected Locale locale = null;
    protected String text = "";

    public void setKey(String key) {
        this.key = key;
    }

    public void setLocale(String localeName) {
        try {
            locale = new Locale(localeName);
        } catch (Exception e) {
            locale = AppConfiguration.getInstance().getStdLocale();
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    private static final String tag = "<div class=\"control-group\">%s</div>";

    public int doStartTag() throws JspException {
        JspWriter writer = getWriter();
        try {
            writer.print(String.format(tag,
                    text.length() > 0 ? StringFormat.toHtml(text) : StringCache.getHtml(key, locale)));
        } catch (Exception e) {
            throw new JspException(e);
        }
        return SKIP_BODY;
    }

}