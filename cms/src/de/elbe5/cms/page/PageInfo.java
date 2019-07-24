/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;


import de.elbe5.base.log.Log;

import java.lang.reflect.Constructor;

public class PageInfo {

    private String type;
    private Constructor<? extends PageData> ctor;
    private PageExtrasBean extrasBean;

    public PageInfo(Class pageClass, PageExtrasBean bean) {
        type = pageClass.getSimpleName();
        try {
            ctor = pageClass.getConstructor();
        } catch (Exception e) {
            Log.error("no valid constructor found", e);
        }
        this.extrasBean = bean;
    }

    public String getType() {
        return type;
    }

    public Constructor<? extends PageData> getCtor() {
        return ctor;
    }

    public PageExtrasBean getExtrasBean() {
        return extrasBean;
    }
}
