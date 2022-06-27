/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package de.elbe5.data;

import de.elbe5.companion.JsonCompanion;
import de.elbe5.log.Log;
import org.json.JSONObject;

public interface IJsonData extends JsonCompanion {

    // default methods

    default String getJSONString() {
        return toJSONObject().toString(2);
    }

    default Object toJson(Object value){
        if (value instanceof IJsonData) {
            return ((IJsonData)value).toJSONObject();
        }
        return JsonCompanion.super.toJson(value);
    }

    static IJsonData createIJsonData(JSONObject jo){
        IJsonData data = null;
        try {
            String clsName = jo.get(JsonCompanion.classKey).toString();
            Class<?> cls = Class.forName(clsName);
            Object obj = cls.getDeclaredConstructor().newInstance();
            if (obj instanceof IJsonData) {
                data = (IJsonData) obj;
                data.fromJSONObject(jo);
            }
        }
        catch (Exception e){
            Log.error("could not create from JSON " + e.getMessage());
        }
        return data;
    }

    default Object fromJsonObject(JSONObject jsonObject, Class<?> baseClass) {
        if (IJsonData.class.isAssignableFrom(baseClass)){
            return createIJsonData(jsonObject);
        }
        return JsonCompanion.super.fromJsonObject(jsonObject, baseClass);
    }

    default void dump(){
        JSONObject obj = toJSONObject();
        Log.log(obj.toString(2));
    }
    
}
