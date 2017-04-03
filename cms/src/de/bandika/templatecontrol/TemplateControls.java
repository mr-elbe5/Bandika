/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.templatecontrol;

import de.bandika.base.log.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TemplateControls {

    protected static Map<String, Method> getInstanceMethodMap = new HashMap<>();

    public static void addPageControlClass(String name, Class<? extends TemplateControl> cls) {
        try {
            Method m = cls.getMethod("getInstance");
            if (m != null) {
                getInstanceMethodMap.put(name, m);
            }
        } catch (Exception e) {
            Log.error("could not find static getInstance() Method of class name " + name);
        }
    }

    public static TemplateControl getControl(String name) {
        try {
            Method m = getInstanceMethodMap.get(name);
            // getInstance()
            return (TemplateControl) m.invoke(null);
        } catch (Exception e) {
            Log.error("could not create instance of class name " + name);
            return null;
        }
    }

}
