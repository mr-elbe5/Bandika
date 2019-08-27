/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.template;

import java.util.*;

public class TemplateFactory {

    private static List<TemplateInfo> infos = new ArrayList<>();
    private static Map<String,String> tagLibStrings = new HashMap<>();

    public static List<TemplateInfo> getInfos() {
        return infos;
    }

    public static String getTagLibString(String type){
        if (!tagLibStrings.containsKey(type))
            return "";
        return tagLibStrings.get(type);
    }

    public static void addInfo(TemplateInfo info){
        infos.add(info);
        tagLibStrings.put(info.getType(),info.getTagLibsString());
    }

}
