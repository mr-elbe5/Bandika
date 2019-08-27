/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.template;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TemplateParser {

    public static final String TEMPLATE_TAG = "cms:template";

    public static final String TEMPLATE_ATTR_TYPE = "type";
    public static final String TEMPLATE_ATTR_NAME = "name";
    public static final String TEMPLATE_ATTR_DISPLAYNAME = "displayName";

    public static List<TemplateData> parseTemplates(String code) {
        List<TemplateData> templates = new ArrayList<>();
        Document doc = Jsoup.parse(code, "", Parser.xmlParser());
        Elements elements = doc.getElementsByTag(TEMPLATE_TAG);
        for (Element element : elements) {
            TemplateData template = new TemplateData();
            template.setType(element.attr(TEMPLATE_ATTR_TYPE));
            template.setName(element.attr(TEMPLATE_ATTR_NAME));
            template.setDisplayName(element.attr(TEMPLATE_ATTR_DISPLAYNAME));
            template.setCode(element.html());
            templates.add(template);
        }
        return templates;
    }

}
