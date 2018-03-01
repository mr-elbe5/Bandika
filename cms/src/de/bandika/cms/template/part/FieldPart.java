/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template.part;

import de.bandika.cms.field.Field;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.template.part.TemplatePart;

import java.io.IOException;

public class FieldPart extends TemplatePart {

    public static final String KEY = "field";

    public String getKey(){
        return KEY;
    }

    public boolean isDynamic(){
        return false;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        Field field = outputData.partData.ensureField(getAttributes().get("name"), getAttributes().get("fieldType"));
        outputData.attributes=attributes;
        outputData.content=content;
        field.appendFieldHtml(outputContext, outputData);
    }

}