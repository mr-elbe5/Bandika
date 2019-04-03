/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import de.elbe5.base.log.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KeyValueMap extends HashMap<String, Object> {

    public String getString(String key) {
        Object obj = get(key);
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof String[]) {
            return ((String[]) obj)[0];
        }
        return null;
    }

    public String getString(String key, String def) {
        Object obj = get(key);
        if (obj == null) {
            return def;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof String[]) {
            return ((String[]) obj)[0];
        }
        return def;
    }

    public List<String> getStringList(String key) {
        List<String> list = new ArrayList<>();
        Object obj = get(key);
        if (obj != null) {
            if (obj instanceof String) {
                StringTokenizer stk = new StringTokenizer((String) obj, ",");
                while (stk.hasMoreTokens()) {
                    list.add(stk.nextToken());
                }
            } else if (obj instanceof String[]) {
                String[] values = (String[]) obj;
                list.addAll(Arrays.asList(values));
            }
        }
        return list;
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

    public List<Integer> getIntegerList(String key) {
        List<Integer> list = new ArrayList<>();
        Object obj = get(key);
        if (obj != null){
            if (obj instanceof String){
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
            } else if (obj instanceof String[]){
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
        }
        return list;
    }

    public Set<Integer> getIntegerSet(String key) {
        return new HashSet<>(getIntegerList(key));
    }

    public long getLong(String key, int defaultValue) {
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

    public double getDouble(String key, double defaultValue) {
        double value = defaultValue;
        try {
            String str = getString(key);
            value = Double.parseDouble(str);
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean value = defaultValue;
        try {
            String str = getString(key);
            value = str.equalsIgnoreCase("true");
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public LocalDateTime getDate(String key) {
        LocalDateTime value = null;
        try {
            String str = getString(key);
            value = LocalDateTime.parse(str,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public LocalDateTime getTime(String key) {
        LocalDateTime value = null;
        try {
            String str = getString(key);
            value = LocalDateTime.parse(str,DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public LocalDateTime getDateTime(String key) {
        LocalDateTime value = null;
        try {
            String str = getString(key);
            value = LocalDateTime.parse(str,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception ignore) {/* do nothing */
        }
        return value;
    }

    public BinaryFileData getFile(String key) {
        BinaryFileData file = null;
        try {
            Object obj = get(key);
            if (obj instanceof BinaryFileData) {
                file = (BinaryFileData) obj;
            }
        } catch (Exception ignore) {/* do nothing */

        }
        return file;
    }

    public List<BinaryFileData> getFileList(String key) {
        List<BinaryFileData> list = new ArrayList<>();
        Object obj = get(key);
        if (obj != null){
            if (obj instanceof BinaryFileData[]){
                BinaryFileData[] values = (BinaryFileData[]) get(key);
                if (values != null) {
                    Collections.addAll(list, values);
                }
            }
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("KeyValueMap:\n");
        for (String key : keySet()) {
            Object value = get(key);
            sb.append(key);
            sb.append('=');
            sb.append(value);
            sb.append('\n');
        }
        return sb.toString();
    }










}
