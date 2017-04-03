/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

/**
 * Class XmlData is a data class for simple XML exchange. <br>
 * Usage:
 */
public class XmlData {

  /**
   * Method startXml
   *
   * @return StringBuffer
   */
  public static StringBuffer startXml() {
    return new StringBuffer("<data>");
  }

  /**
   * Method addNode
   *
   * @param buffer of type StringBuffer
   * @param key    of type String
   * @param value  of type String
   */
  public static void addNode(StringBuffer buffer, String key, String value) {
    buffer.append("<");
    buffer.append(toXml(key));
    buffer.append(">");
    buffer.append(toXml(value));
    buffer.append("</");
    buffer.append(toXml(key));
    buffer.append(">");
  }

  /**
   * Method addIntNode
   *
   * @param buffer of type StringBuffer
   * @param key    of type String
   * @param value  of type int
   */
  public static void addIntNode(StringBuffer buffer, String key, int value) {
    addNode(buffer, key, Integer.toString(value));
  }

  /**
   * Method addLongNode
   *
   * @param buffer of type StringBuffer
   * @param key    of type String
   * @param value  of type long
   */
  public static void addLongNode(StringBuffer buffer, String key, long value) {
    addNode(buffer, key, Long.toString(value));
  }

  /**
   * Method finishXml
   *
   * @param buffer of type StringBuffer
   * @return String
   */
  public static String finishXml(StringBuffer buffer) {
    buffer.append("</data>");
    return buffer.toString();
  }

  /**
   * Method getNode
   *
   * @param xml of type String
   * @param key of type String
   * @return String
   */
  public static String getNode(String xml, String key) {
    if (xml == null)
      return "";
    int start = xml.indexOf("<" + toXml(key) + ">");
    if (start == -1)
      return "";
    start += key.length() + 2;
    int end = xml.indexOf("</" + toXml(key) + ">", start);
    if (end == -1)
      return "";
    return fromXml(xml.substring(start, end));
  }

  /**
   * Method getIntNode
   *
   * @param xml of type String
   * @param key of type String
   * @return int
   */
  public static int getIntNode(String xml, String key) {
    if (xml == null)
      return 0;
    int start = xml.indexOf("<" + toXml(key) + ">");
    if (start == -1)
      return 0;
    start += key.length() + 2;
    int end = xml.indexOf("</" + toXml(key) + ">", start);
    if (end == -1)
      return 0;
    try {
      return Integer.parseInt(xml.substring(start, end));
    }
    catch (Exception e) {
      return 0;
    }
  }

  /**
   * Method getLongNode
   *
   * @param xml of type String
   * @param key of type String
   * @return long
   */
  public static long getLongNode(String xml, String key) {
    if (xml == null)
      return 0;
    int start = xml.indexOf("<" + toXml(key) + ">");
    if (start == -1)
      return 0;
    start += key.length() + 2;
    int end = xml.indexOf("</" + toXml(key) + ">", start);
    if (end == -1)
      return 0;
    try {
      return Long.parseLong(xml.substring(start, end));
    }
    catch (Exception e) {
      return 0;
    }
  }

  /**
   * Method toXml
   *
   * @param source of type String
   * @return String
   */
  public static String toXml(String source) {
    StringBuffer buffer = new StringBuffer();
    if (source == null)
      return buffer.toString();
    int len = source.length();
    char ch;
    for (int i = 0; i < len; i++) {
      ch = source.charAt(i);
      switch (ch) {
        case '<':
          buffer.append("&lt;");
          break;
        case '>':
          buffer.append("&gt;");
          break;
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

  /**
   * Method fromXml
   *
   * @param source of type String
   * @return String
   */
  public static String fromXml(String source) {
    StringBuffer buffer = new StringBuffer();
    if (source == null)
      return buffer.toString();
    int len = source.length();
    char ch;
    for (int i = 0; i < len; i++) {
      ch = source.charAt(i);
      switch (ch) {
        case '&':
          if (source.length() > i + 3) {
            String str = source.substring(i, i + 4);
            if (str.equals("&lt;")) {
              buffer.append("<");
              i += 3;
              break;
            }
            if (str.equals("&gt;")) {
              buffer.append(">");
              i += 3;
              break;
            }
          }
        default:
          buffer.append(ch);
          break;
      }
    }
    return buffer.toString();
  }

}
