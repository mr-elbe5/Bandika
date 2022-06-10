/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.companion.DateCompanion;
import de.elbe5.companion.StringCompanion;

import java.util.Map;

public interface IHtmlBuilder extends StringCompanion, DateCompanion {

    default void append(StringBuilder sb, String s){
        sb.append(s);
    }

    default void append(StringBuilder sb, String s, Map<String, String> params){
        sb.append(format(s, params));
    }

    default Map.Entry<String, String> param(String key, String value){
        if (value.startsWith("_")){
            return Map.entry(key, getHtml(value));
        }
        return Map.entry(key, toHtml(value));
    }

    default Map.Entry<String, String> htmlParam(String key, String value){
        return Map.entry(key, value);
    }

    default Map.Entry<String, String> param(String key, int value){
        return Map.entry(key, Integer.toString(value));
    }

    default Map.Entry<String, String> param(String key, boolean value){
        return Map.entry(key, Boolean.toString(value));
    }

}
