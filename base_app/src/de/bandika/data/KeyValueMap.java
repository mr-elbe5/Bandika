/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class KeyValueMap extends Properties {

    public static final Date ERROR_DATE = new Date(0x0);

    public String getString(String key) {
        Object obj = get(key);
        if (obj == null)
            return "";
        if (obj instanceof String)
            return (String) obj;
        if (obj instanceof String[])
            return ((String[]) obj)[0];
        return null;
    }

    public String getString(String key, String def) {
        Object obj = get(key);
        if (obj == null)
            return def;
        if (obj instanceof String)
            return (String) obj;
        if (obj instanceof String[])
            return ((String[]) obj)[0];
        return def;
    }

    public float getCurrency(String key, Locale locale)
            throws NumberFormatException {
        NumberFormat format;
        if (locale == null)
            format = NumberFormat.getNumberInstance();
        else
            format = NumberFormat.getNumberInstance(locale);
        float fl = 0;
        try {
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(2);
            String str = getString(key);
            Number num = format.parse(str);
            fl = num.floatValue();
        } catch (Exception ignore) {/* do nothing */
        }
        return fl;
    }

    public int getInt(String key, int defaultValue) {
        int value = defaultValue;
        try {
            String str = getString(key);
            value = Integer.parseInt(str);
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        long value = defaultValue;
        try {
            String str = getString(key);
            value = Long.parseLong(str);
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public float getFloat(String key, float defaultValue) {
        float value = defaultValue;
        try {
            String str = getString(key);
            value = Float.parseFloat(str);
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean value = defaultValue;
        try {
            String str = getString(key);
            value = Integer.parseInt(str) > 0;
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public Date getDate(String key, String datePattern) {
        return getDate(key, new SimpleDateFormat(datePattern));
    }

    public Date getDate(String key, SimpleDateFormat format) {
        Date value = null;
        try {
            String str = getString(key);
            value = format.parse(str);
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public String getList(String key) {
        Object obj = get(key);
        if (obj != null) {
            if (obj instanceof String) {
                return (String) obj;
            } else if (obj instanceof String[]) {
                String[] values = (String[]) obj;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < values.length; i++) {
                    if (i > 0)
                        sb.append(",");
                    sb.append(values[i]);
                }
                return sb.toString();
            }
        }
        return "";
    }

    public List<String> getStringList(String key) {
        List<String> list = new ArrayList<>();
        Object obj = get(key);
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

    public List<Integer> getIntegerList(String key) {
        List<Integer> list = new ArrayList<>();
        Object obj = get(key);
        if (obj != null && obj instanceof String) {
            StringTokenizer stk = new StringTokenizer((String) obj, ",");
            String token = null;
            while (stk.hasMoreTokens()) {
                try {
                    token = stk.nextToken();
                    list.add(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    Log.error("wrong number format: " + token);
                }
            }
        } else {
            String[] values = (String[]) get(key);
            if (values != null) {
                for (String value : values) {
                    try {
                        list.add(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        Log.error("wrong number format: " + value);
                    }
                }
            }
        }
        return list;
    }

    public Set<Integer> getIntegerSet(String key) {
        Set<Integer> set = new HashSet<>();
        Object obj = get(key);
        if (obj != null && obj instanceof String) {
            StringTokenizer stk = new StringTokenizer((String) obj, ",");
            String token = null;
            while (stk.hasMoreTokens()) {
                try {
                    token = stk.nextToken();
                    set.add(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    Log.error("wrong number format: " + token);
                }
            }
        } else {
            String[] values = (String[]) get(key);
            if (values != null) {
                for (String value : values) {
                    try {
                        set.add(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        Log.error("wrong number format: " + value);
                    }
                }
            }
        }
        return set;
    }

    public FileData getFile(String key) {
        FileData file = null;
        try {
            Object obj = get(key);
            if (obj != null && obj instanceof FileData)
                file = (FileData) obj;
        } catch (Exception ignore) {/* do nothing */
        }
        return file;
    }

    public List<FileData> getFileList() {
        List<FileData> list = new ArrayList<>();
        try {
            for (Object key : keySet()) {
                Object obj = get(key);
                if (obj != null && obj instanceof FileData)
                    list.add((FileData) obj);
            }
        } catch (Exception e) {/* do nothing */
        }
        return list;
    }

    public Calendar getCalendar(String key) throws ParseException {
        Calendar cal = null;
        StringBuilder sb = new StringBuilder(key);
        int len = key.length();
        int year = getInt(sb.append("Year").toString(), -1);
        sb.setLength(len);
        int month = getInt(sb.append("Month").toString(), -1);
        sb.setLength(len);
        int day = getInt(sb.append("Day").toString(), -1);
        sb.setLength(len);
        int hour = getInt(sb.append("Hour").toString());
        sb.setLength(len);
        int min = getInt(sb.append("Min").toString());
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

    public Date getSafeDate(String key) {
        try {
            Calendar cal = getCalendar(key);
            return cal == null ? null : cal.getTime();
        } catch (Exception e) {
            return ERROR_DATE;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("KeyValueMap:\n");
        for (Object key : keySet()) {
            Object value = get(key);
            sb.append(key);
            sb.append("=");
            sb.append(value);
            sb.append("\n");
        }
        return sb.toString();
    }

}
