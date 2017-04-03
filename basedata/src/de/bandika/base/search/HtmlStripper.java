/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.base.search;

import java.text.ParseException;

public class HtmlStripper {

    public static String stripHtml(String html) throws ParseException {
        if (html == null)
            return null;
        html = stripBody(html);
        html = stripComments(html);
        html = stripScripts(html);
        html = stripTags(html);
        html = convertEscapesAndWhites(html);
        return html.trim();
    }

    public static String stripBody(String html) throws ParseException {
        if (html == null)
            return null;
        String lowHtml = html.toLowerCase();
        int start = lowHtml.indexOf("<body");
        int end;
        if (start != -1) {
            end = lowHtml.indexOf('>', start);
            if (end == -1)
                throw new ParseException("body tag not closed", start);
            start = end + 1;
            end = lowHtml.lastIndexOf("</body>");
            if (end == -1)
                throw new ParseException("no body end tag found", start);
            html = html.substring(start, end);
        }
        return html;
    }

    public static String stripComments(String html) throws ParseException {
        if (html == null)
            return null;
        int start;
        int end = 0;
        StringBuilder buffer = new StringBuilder();
        while (true) {
            start = html.indexOf("<!--", end);
            if (start == -1) {
                buffer.append(html.substring(end));
                break;
            }
            buffer.append(html.substring(end, start));
            end = html.indexOf("-->", start);
            if (end == -1) {
                end = start + 4;
                continue;
            }
            end += 3;
        }
        return buffer.toString();
    }

    public static String stripScripts(String html) throws ParseException {
        if (html == null)
            return null;
        String lowHtml = html.toLowerCase();
        int start;
        int end = 0;
        StringBuilder buffer = new StringBuilder();
        while (true) {
            start = lowHtml.indexOf("<script", end);
            if (start == -1) {
                buffer.append(html.substring(end));
                break;
            }
            buffer.append(html.substring(end, start));
            end = lowHtml.indexOf("</script>", start);
            if (end == -1) {
                end = start + 1;
                continue;
            }
            end += 9;
        }
        return buffer.toString();
    }

    public static String stripTags(String html) {
        int len = html.length();
        char ch;
        boolean inTag = false;
        boolean inText = false;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < len; i++) {
            ch = html.charAt(i);
            switch (ch) {
                case '\"':
                    if (inTag)
                        inText = !inText;
                    if (!inTag)
                        buffer.append(ch);
                    break;
                case '<':
                    inTag = true;
                    break;
                case '>':
                    if (!inTag)
                        buffer.append(ch);
                    if (inTag && !inText) {
                        buffer.append(' ');
                        inTag = false;
                    }
                    break;
                default:
                    if (!inTag)
                        buffer.append(ch);
                    break;
            }
        }
        return buffer.toString();
    }

    public static String convertEscapesAndWhites(String source) {
        if (source == null)
            return "";
        StringBuilder buffer = new StringBuilder();
        int len = source.length();
        char ch;
        char lastch = 0;
        char newch = 0;
        int pos;
        for (int i = 0; i < len; i++) {
            ch = source.charAt(i);
            switch (ch) {
                case '&': {
                    pos = source.indexOf(';', i + 1);
                    if (pos == -1)
                        continue;
                    String sub = source.substring(i + 1, pos);
                    i = pos;
                    if ("gt".equals(sub))
                        newch = '>';
                    else if ("lt".equals(sub))
                        newch = '<';
          /**/
                    else if ("auml".equals(sub))
                        newch = '\u00e4';
                    else if ("ouml".equals(sub))
                        newch = '\u00f6';
                    else if ("uuml".equals(sub))
                        newch = '\u00fc';
                    else if ("Auml".equals(sub))
                        newch = '\u00c4';
                    else if ("Ouml".equals(sub))
                        newch = '\u00d6';
                    else if ("Uuml".equals(sub))
                        newch = '\u00dc';
                    else if ("szlig".equals(sub))
                        newch = '\u00df';
          /**/
                    else if ("amp".equals(sub))
                        newch = '&';
                    else if ("quot".equals(sub))
                        newch = '"';
                    else if ("euro".equals(sub))
                        newch = '\u20ac';
                    else if ("nbsp".equals(sub))
                        newch = ' ';
                    else if (sub.length() > 1 && sub.charAt(0) == '#')
                        newch = (char) Integer.parseInt(sub.substring(1));
                }
                break;
                case '\t':
                case '\r':
                case '\n':
                    newch = ' ';
                    break;
                default:
                    newch = ch;
                    break;
            }
            if (lastch == ' ' && newch == ' ')
                continue;
            buffer.append(newch);
            lastch = newch;
        }
        return buffer.toString();
    }

}
