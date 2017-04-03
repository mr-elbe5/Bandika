/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.TokenGroup;

public class SearchContextFormatter implements Formatter {

    private String preTag;
    private String postTag;

    public SearchContextFormatter() {
        preTag = "<span class=\"searchHighlight\">";
        postTag = "</span>";
    }

    public String highlightTerm(String originalText, TokenGroup tokenGroup) {
        StringBuilder buffer;
        if (tokenGroup.getTotalScore() > 0) {
            buffer = new StringBuilder();
            buffer.append(preTag);
            buffer.append(originalText);
            buffer.append(postTag);
            return buffer.toString();
        }
        return originalText;
    }
}

