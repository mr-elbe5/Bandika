/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.data;

import de.elbe5.base.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataProperties {

    protected String header="";
    protected List<Pair<String,String>> properties=new ArrayList<>();

    public String getHeader() {
        return header;
    }

    public void setKeyHeader(String key, Locale locale) {
        setHeader(StringUtil.getString(key,locale));
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void addKeyProperty(String key, String value, Locale locale){
        addProperty(StringUtil.getString(key, locale),value);
    }

    public void addProperty(String key, String value){
        properties.add(new Pair<>(StringUtil.toHtml(key),StringUtil.toHtml(value)));
    }

    public void  addKeyPropertyLines(String key, String value, Locale locale) {
        addPropertyLines(StringUtil.getString(key, locale),value);
    }

    public void  addPropertyLines(String key, String value) {
        if (value==null || value.length()==0)
            return;
        String[] strings=value.split("\n");
        properties.add(new Pair<>(StringUtil.toHtml(key),StringUtil.toHtml(strings)));
    }

    public void addHtmlKeyProperty(String key, String value, Locale locale){
        addHtmlProperty(StringUtil.getString(key, locale),value);
    }

    public void addHtmlProperty(String key, String htmlValue){
        properties.add(new Pair<>(StringUtil.toHtml(key),htmlValue));
    }

    public void addKeyProperty(String key, int i, Locale locale){
        addProperty(StringUtil.getString(key,locale),i);
    }

    public void addProperty(String key, int i){
        properties.add(new Pair<>(StringUtil.toHtml(key),String.valueOf(i)));
    }

    public void addKeyProperty(String key, Date date, Locale locale){
        addProperty(StringUtil.getString(key,locale),date, locale);
    }

    public void addProperty(String key, Date date, Locale locale){
        SimpleDateFormat sformat=new SimpleDateFormat(StringUtil.getString("_datetimepattern", locale));
        properties.add(new Pair<>(StringUtil.toHtml(key),StringUtil.toHtml(sformat.format(date))));
    }

    public List<Pair<String, String>> getProperties() {
        return properties;
    }

}
