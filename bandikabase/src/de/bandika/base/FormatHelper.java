/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import java.net.URLEncoder;


/**
 * Class Formatter ids a helper class for various kinds of string/data formatting.<br>
 * Usage:
 */
public class FormatHelper {

  public static String toHtml(String src) {
    if (src == null)
      return "";
    StringBuffer buffer = new StringBuffer();
    char ch;
    for (int i = 0; i < src.length(); i++) {
      ch = src.charAt(i);
      switch (ch) {
        case '<':
          buffer.append("&lt;");
          break;
        case '>':
          buffer.append("&gt;");
          break;
        case '&':
          buffer.append("&amp;");
          break;
        case '"':
          buffer.append("&quot;");
          break;
        case '\r':
          break;
        case '\n':
          buffer.append("<br>");
          break;
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

  public static String toHtmlInput(String src) {
    if (src == null)
      return "";
    StringBuffer buffer = new StringBuffer();
    char ch;
    for (int i = 0; i < src.length(); i++) {
      ch = src.charAt(i);
      switch (ch) {
        case '<':
          buffer.append("&lt;");
          break;
        case '>':
          buffer.append("&gt;");
          break;
        case '&':
          buffer.append("&amp;");
          break;
        case '"':
          buffer.append("&quot;");
          break;
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

  public static String toHtml(String[] strings) {
    if (strings == null)
      return "";
    StringBuffer buffer = new StringBuffer(toHtml(strings[0]));
    for (int i = 1; i < strings.length; i++)
      buffer.append("<br>").append(toHtml(strings[i]));
    return buffer.toString();
  }

  public static String toXml(String source) {
    StringBuffer buffer = new StringBuffer();
    if (source == null)
      return buffer.toString();
    int len = source.length();
    char ch;
    for (int i = 0; i < len; i++) {
      ch = source.charAt(i);
      switch (ch) {
        case '\r':
          break;
        case '\n':
          buffer.append("\r\n");
          break;
        case '<':
          buffer.append("&lt;");
          break;
        case '>':
          buffer.append("&gt;");
          break;
        case '&':
          buffer.append("&amp;");
          break;
        case '"':
          buffer.append("&quot;");
          break;
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

  public static String toJS(String src) {
    if (src == null)
      return "";
    StringBuffer buffer = new StringBuffer();
    for (char ch : src.toCharArray()) {
      switch (ch) {
        case '\'':
          buffer.append("\\'");
          break;
        case '\r':
          break;
        case '\n':
          buffer.append("\\n");
          break;
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

	public static String encode(String src){
		try{
			return URLEncoder.encode(src,"UTF-8");
		}
		catch (Exception e){
			return src;
		}
	}

}
