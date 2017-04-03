/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class SearchContextFormatter implements Formatter {

    public SearchContextFormatter() {
    }

    public String highlightTerm(String originalText, TokenGroup tokenGroup) {
        StringBuilder buffer;
        if (tokenGroup.getTotalScore() > 0) {
            buffer = new StringBuilder();
            buffer.append("<span class=\"searchHighlight\">");
            buffer.append(originalText);
            buffer.append("</span>");
            return buffer.toString();
        }
        return originalText;
    }
}

