/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.page;

import de.elbe5.base.log.Log;

import java.lang.reflect.Constructor;
import java.util.*;

public class PageFactory {

    private static Map<String, PageInfo> infos = new HashMap<>();

    public static List<String> getTypes() {
        List<String> list = new ArrayList<>(infos.keySet());
        Collections.sort(list);
        return list;
    }

    public static void addInfo(PageInfo info) {
        infos.put(info.getType(), info);
    }

    public static PageData getPageData(String type) {
        if (!infos.containsKey(type)) {
            Log.error("no page info for type"+type);
            return null;
        }
        Constructor<? extends PageData> ctor = infos.get(type).getCtor();
        try {
            return ctor.newInstance();
        } catch (Exception e) {
            Log.error("could not create page data for type "+type);
        }
        return null;
    }

    public static PageExtrasBean getExtrasBean(String type) {
        if (!infos.containsKey(type))
            return null;
        return infos.get(type).getExtrasBean();
    }


}
