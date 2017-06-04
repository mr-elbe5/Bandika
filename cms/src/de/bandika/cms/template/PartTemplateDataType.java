/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.log.Log;
import de.bandika.cms.page.HtmlPartData;
import de.bandika.cms.page.MultiHtmlPartData;
import de.bandika.cms.page.PagePartData;

public enum PartTemplateDataType {
    DEFAULT {
        public PagePartData getNewPagePartData() {
            return HTML.getNewPagePartData();
        }
    }, HTML {
        public PagePartData getNewPagePartData() {
            return new HtmlPartData();
        }
    }, MULTIHTML {
        public PagePartData getNewPagePartData() {
            return new MultiHtmlPartData();
        }
    };

    public abstract PagePartData getNewPagePartData();

    public static PartTemplateDataType getPageTemplateDataType(String dataTypeName) {
        try {
            return valueOf(dataTypeName);
        } catch (Exception e) {
            Log.warn("no valid page part data type: " + dataTypeName);
            return DEFAULT;
        }
    }

}
