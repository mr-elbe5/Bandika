/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.http;

import de.net25.base.Logger;
import de.net25.base.resources.FileData;
import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;

import java.util.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Class ParamData is the base class for holding key/value pairs in a map.<br>
 * Usage:
 */
public class ParamData {

  protected HashMap<String, Object> params = new HashMap<String, Object>();

  /**
   * Method setParam
   *
   * @param key   of type String
   * @param value of type Object
   */
  public void setParam(String key, Object value) {
    params.put(key, value);
  }

  /**
   * Method removeParam
   *
   * @param key of type String
   */
  public void removeParam(String key) {
    params.remove(key);
  }

  /**
   * Method getParam
   *
   * @param key of type String
   * @return Object
   */
  public Object getParam(String key) {
    return params.get(key);
  }

  /**
   * Method getParamString
   *
   * @param key of type String
   * @return String
   */
  public String getParamString(String key) {
    Object obj = params.get(key);
    if (obj == null)
      return "";
    if (obj instanceof String)
      return (String) obj;
    if (obj instanceof String[])
      return ((String[]) obj)[0];
    return null;
  }

  /**
   * Method getParamCurrency
   *
   * @param key    of type String
   * @param locale of type Locale
   * @return float
   * @throws NumberFormatException when data processing is not successful
   */
  public float getParamCurrency(String key, Locale locale) throws NumberFormatException {
    NumberFormat format = null;
    if (locale == null)
      format = NumberFormat.getNumberInstance();
    else
      format = NumberFormat.getNumberInstance(locale);
    float fl = 0;
    try {
      format.setMinimumFractionDigits(0);
      format.setMaximumFractionDigits(2);
      String str = getParamString(key);
      Number num = format.parse(str);
      fl = num.floatValue();
    } catch (Exception ignore) {/*do nothing*/}
    return fl;
  }

  /**
   * Method getParamInt
   *
   * @param key          of type String
   * @param defaultValue of type int
   * @return int
   */
  public int getParamInt(String key, int defaultValue) {
    int value = defaultValue;
    try {
      String str = getParamString(key);
      value = Integer.parseInt(str);
    } catch (Exception ignore) {/*do nothing*/}
    return value;
  }

  /**
   * Method getParamInt
   *
   * @param key of type String
   * @return int
   */
  public int getParamInt(String key) {
    return getParamInt(key, 0);
  }

  /**
   * Method getParamLong
   *
   * @param key          of type String
   * @param defaultValue of type long
   * @return long
   */
  public long getParamLong(String key, long defaultValue) {
    long value = defaultValue;
    try {
      String str = getParamString(key);
      value = Long.parseLong(str);
    } catch (Exception ignore) {/*do nothing*/}
    return value;
  }

  /**
   * Method getParamLong
   *
   * @param key of type String
   * @return long
   */
  public long getParamLong(String key) {
    return getParamLong(key, 0);
  }

  /**
   * Method getParamFloat
   *
   * @param key          of type String
   * @param defaultValue of type float
   * @return float
   */
  public float getParamFloat(String key, float defaultValue) {
    float value = defaultValue;
    try {
      String str = getParamString(key);
      value = Float.parseFloat(str);
    } catch (Exception ignore) {/*do nothing*/}
    return value;
  }

  /**
   * Method getParamFloat
   *
   * @param key of type String
   * @return float
   */
  public float getParamFloat(String key) {
    return getParamFloat(key, 0);
  }

  /**
   * Method getParamBoolean
   *
   * @param key          of type String
   * @param defaultValue of type boolean
   * @return boolean
   */
  public boolean getParamBoolean(String key, boolean defaultValue) {
    boolean value = defaultValue;
    try {
      String str = getParamString(key);
      value = Integer.parseInt(str) > 0;
    } catch (Exception ignore) {/*do nothing*/}
    return value;
  }

  /**
   * Method getParamBoolean
   *
   * @param key of type String
   * @return boolean
   */
  public boolean getParamBoolean(String key) {
    return getParamBoolean(key, false);
  }

  /**
   * Method getParamDate
   *
   * @param key   of type String
   * @param sdata of type SessionData
   * @return Date
   */
  public Date getParamDate(String key, SessionData sdata) {
    return getParamDate(key, new SimpleDateFormat(Strings.getString("datePattern", sdata.getLocale())));
  }

  /**
   * Method getParamDate
   *
   * @param key    of type String
   * @param format of type SimpleDateFormat
   * @return Date
   */
  public Date getParamDate(String key, SimpleDateFormat format) {
    Date value = null;
    try {
      String str = getParamString(key);
      value = format.parse(str);
    } catch (Exception ignore) {/*do nothing*/}
    return value;
  }

  /**
   * Method getParamList
   *
   * @param key of type String
   * @return String
   */
  public String getParamList(String key) {
    Object obj = getParam(key);
    if (obj != null) {
      if (obj instanceof String) {
        return (String) obj;
      } else if (obj instanceof String[]) {
        String[] values = (String[]) obj;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
          if (i > 0)
            buffer.append(",");
          buffer.append(values[i]);
        }
        return buffer.toString();
      }
    }
    return "";
  }

  /**
   * Method getParamArrayList
   *
   * @param key of type String
   * @return ArrayList<String>
   */
  public ArrayList<String> getParamArrayList(String key) {
    ArrayList<String> list = new ArrayList<String>();
    Object obj = getParam(key);
    if (obj != null) {
      if (obj instanceof String) {
        StringTokenizer stk = new StringTokenizer((String) obj, ",");
        while (stk.hasMoreTokens())
          list.add(stk.nextToken());
      } else if (obj instanceof String[]) {
        String[] values = (String[]) obj;
        list.addAll(Arrays.asList(values));
      }
    }
    return list;
  }

  /**
   * Method getParamIntegerList
   *
   * @param key of type String
   * @return ArrayList<Integer>
   */
  public ArrayList<Integer> getParamIntegerList(String key) {
    ArrayList<Integer> list = new ArrayList<Integer>();
    Object obj = getParam(key);
    if (obj != null && obj instanceof String) {
      StringTokenizer stk = new StringTokenizer((String) obj, ",");
      String token = null;
      while (stk.hasMoreTokens()) {
        try {
          token = stk.nextToken();
          list.add(Integer.parseInt(token));
        } catch (NumberFormatException e) {
          Logger.error(getClass(), "wrong number format: " + token);
        }
      }
    } else {
      String[] values = (String[]) getParam(key);
      if (values != null) {
        for (String value : values) {
          try {
            list.add(Integer.parseInt(value));
          } catch (NumberFormatException e) {
            Logger.error(getClass(), "wrong number format: " + value);
          }
        }
      }
    }
    return list;
  }

  /**
   * Method getParamIntegerSet
   *
   * @param key of type String
   * @return HashSet<Integer>
   */
  public HashSet<Integer> getParamIntegerSet(String key) {
    HashSet<Integer> set = new HashSet<Integer>();
    Object obj = getParam(key);
    if (obj != null && obj instanceof String) {
      StringTokenizer stk = new StringTokenizer((String) obj, ",");
      String token = null;
      while (stk.hasMoreTokens()) {
        try {
          token = stk.nextToken();
          set.add(Integer.parseInt(token));
        } catch (NumberFormatException e) {
          Logger.error(getClass(), "wrong number format: " + token);
        }
      }
    } else {
      String[] values = (String[]) getParam(key);
      if (values != null) {
        for (String value : values) {
          try {
            set.add(Integer.parseInt(value));
          } catch (NumberFormatException e) {
            Logger.error(getClass(), "wrong number format: " + value);
          }
        }
      }
    }
    return set;
  }

  /**
   * Method getParamFile
   *
   * @param key of type String
   * @return FileData
   */
  public FileData getParamFile(String key) {
    FileData file = null;
    try {
      Object obj = getParam(key);
      if (obj != null && obj instanceof FileData)
        file = (FileData) obj;
    } catch (Exception ignore) {/*do nothing*/}
    return file;
  }

  /**
   * Method getParamFileList returns the paramFileList of this ParamData object.
   *
   * @return the paramFileList (type ArrayList<FileData>) of this ParamData object.
   */
  public ArrayList<FileData> getParamFileList() {
    ArrayList<FileData> list = new ArrayList<FileData>();
    try {
      for (String s : params.keySet()) {
        Object obj = getParam(s);
        if (obj != null && obj instanceof FileData)
          list.add((FileData) obj);
      }
    } catch (Exception e) {/*do nothing*/}
    return list;
  }

  /**
   * Method getParamCalendar
   *
   * @param key of type String
   * @return Calendar
   * @throws ParseException when data processing is not successful
   */
  public Calendar getParamCalendar(String key) throws ParseException {
    Calendar cal = null;
    StringBuffer buffer = new StringBuffer(key);
    int len = key.length();
    int year = getParamInt(buffer.append("Year").toString(), -1);
    buffer.setLength(len);
    int month = getParamInt(buffer.append("Month").toString(), -1);
    buffer.setLength(len);
    int day = getParamInt(buffer.append("Day").toString(), -1);
    buffer.setLength(len);
    int hour = getParamInt(buffer.append("Hour").toString());
    buffer.setLength(len);
    int min = getParamInt(buffer.append("Min").toString());
    boolean yearok = year >= 0;
    boolean monthok = month >= 0;
    boolean dayok = day >= 0;
    if (yearok && monthok && dayok) {
      cal = new GregorianCalendar();
      cal.clear();
      cal.set(year, month, day, hour, min);
    } else if (yearok != monthok || yearok != dayok) {
      throw new ParseException("CALENDAR_NOT_VALID", 0);
    }
    return cal;
  }

  /**
   * Method getSafeParamDate
   *
   * @param key of type String
   * @return Date
   */
  public Date getSafeParamDate(String key) {
    try {
      Calendar cal = getParamCalendar(key);
      return cal == null ? null : cal.getTime();
    }
    catch (Exception e) {
      return Statics.ERROR_DATE;
    }
  }

  /**
   * Method toString
   *
   * @return String
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer("Parameters:\n");
    for (String s : params.keySet()) {
      Object obj = getParam(s);
      buffer.append(s);
      buffer.append("=");
      buffer.append(obj);
      buffer.append("\n");
    }
    return buffer.toString();
  }

}
