/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.log.Log;
import de.bandika.cms.template.part.*;
import de.bandika.webbase.util.TagAttributes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TemplateParser {

    public static final String TEMPLATE_TAG = "cms:template";
    public static final String INCLUDE_TAG = "cms:include";

    public static final String TEMPLATE_ATTR_TYPE = "type";
    public static final String TEMPLATE_ATTR_NAME = "name";
    public static final String TEMPLATE_ATTR_DISPLAYNAME = "displayName";
    public static final String TEMPLATE_ATTR_SECTION_TYPES = "sectionTypes";

    public static final String PLACEHOLDER_START = "{{";
    public static final String PLACEHOLDER_END = "}}";

    public static List<TemplateData> parseTemplates(String code){
        List<TemplateData> templates=new ArrayList<>();
        Document doc=Jsoup.parse(code,"", Parser.xmlParser());
        Elements elements = doc.getElementsByTag(TEMPLATE_TAG);
        for (Element element : elements){
            TemplateData template=new TemplateData();
            template.setType(element.attr(TEMPLATE_ATTR_TYPE));
            template.setName(element.attr(TEMPLATE_ATTR_NAME));
            template.setDisplayName(element.attr(TEMPLATE_ATTR_DISPLAYNAME));
            template.setSectionTypes(element.attr(TEMPLATE_ATTR_SECTION_TYPES));
            template.setCode(element.html());
            if (parseTemplate(template))
                templates.add(template);
        }
        return templates;
    }

    public static boolean parseTemplate(TemplateData data){
        List<TemplateInclude> list=data.getTemplateIncludes();
        list.clear();
        Document doc=Jsoup.parse(data.getCode(),"",Parser.xmlParser());
        Elements elements = doc.getElementsByTag(INCLUDE_TAG);
        for (Element element : elements){
            TemplateInclude include=getTemplateInclude(element);
            if (include==null) {
                Log.warn("element without type");
                continue;
            }
            if (include instanceof TemplatePart) {
                ((TemplatePart)include).setAttributes(new TagAttributes(element.attributes()));
                ((TemplatePart)include).setContent(element.html());
            }
            list.add(include);
            TextNode placeholder=new TextNode(PLACEHOLDER_START+(list.size()-1)+PLACEHOLDER_END);
            element.replaceWith(placeholder);
        }
        data.setParsedCode(doc.html());
        return true;
    }

    private static TemplateInclude getTemplateInclude(Element element){
        String type=element.attr(TEMPLATE_ATTR_TYPE);
        return TemplateCache.getTemplateInclude(type);
    }

    public static TemplateInclude parseIncludeTag(String tag) {
        Document doc= Jsoup.parse(tag,"", Parser.xmlParser());
        Element element=doc.getElementsByTag("include").first();
        if (element==null)
            return null;
        TemplateInclude include=getTemplateInclude(element);
        if (include==null)
            return null;
        if (include instanceof TemplatePart) {
            ((TemplatePart) include).setAttributes(new TagAttributes(element.attributes()));
        }
        return include;
    }

}
