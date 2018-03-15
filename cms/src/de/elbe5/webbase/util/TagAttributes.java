/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webbase.util;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;

import java.io.Serializable;
import java.util.HashMap;

public class TagAttributes extends HashMap<String, String> implements Serializable {

    public TagAttributes(){

    }

    public TagAttributes(Attributes attributes){
        for (Attribute attr : attributes){
            put(attr.getKey(),attr.getValue());
        }
    }

    public String getString(String key) {
        String value = get(key);
        if (value != null)
            return value;
        return "";
    }

    public int getInt(String key) {
        int value = -1;
        try {
            value = Integer.parseInt(get(key));
        } catch (Exception ignore) {
        }
        return value;
    }

    public boolean getBoolean(String key) {
        boolean value = false;
        try {
            value = Boolean.parseBoolean(get(key));
        } catch (Exception ignore) {
        }
        return value;
    }

}
