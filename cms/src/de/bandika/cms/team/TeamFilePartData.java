/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.team;

import de.bandika.base.data.XmlData;
import de.bandika.cms.field.Field;
import de.bandika.cms.field.Fields;
import de.bandika.cms.page.PagePartData;
import de.bandika.webbase.servlet.RequestReader;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class TeamFilePartData extends PagePartData {

    public static final String DATAKEY = "data|teamfilepart";

    public final static int MODE_LIST = 0;
    public final static int MODE_EDIT = 1;
    public final static int MODE_DELETE = 2;
    public final static int MODE_HISTORY = 3;
    public final static int MODE_HISTORY_DELETE = 4;

    protected String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void createXml(){
        XmlData data=XmlData.create();
        assert data!=null;
        Element root=data.createRootNode("part");
        Element elem = data.addNode(root, "title");
        data.addCDATA(elem, title);
        for (Field field : fields.values()) {
            field.createXml(data, root);
        }
        content = data.toString();
    }

    public void parseXml(){
        XmlData data=XmlData.create(content);
        if (data==null)
            return;
        Element root=data.getRootNode();
        Element tchild = data.findChildElements(root, "title", false).get(0);
        title=data.getCData(tchild);
        List<Element> children = data.findChildElements(root, "field", true);
        for (Element child : children) {
            String fieldType = data.getStringAttribute(child, "fieldType");
            Field field = Fields.getNewField(fieldType);
            if (field != null) {
                field.parseXml(data, child);
                fields.put(field.getName(), field);
            }
        }
    }

    public boolean isComplete() {
        return isComplete(title);
    }

    public boolean readPagePartRequestData(HttpServletRequest request) {
        setTitle(RequestReader.getString(request, "title"));
        return isComplete();
    }

}