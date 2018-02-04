/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.log.Log;
import de.bandika.cms.templatecontrol.*;
import de.bandika.cms.templateinclude.*;
import de.bandika.webbase.util.TagAttributes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TemplateParser {

    public static final String TEMPLATE_TAG = "cms-template";
    public static final String INCLUDE_TAG = "cms-include";

    public static final String TEMPLATE_ATTR_TYPE = "type";
    public static final String TEMPLATE_ATTR_NAME = "name";
    public static final String TEMPLATE_ATTR_DISPLAYNAME = "displayName";
    public static final String TEMPLATE_ATTR_USAGE = "usage";

    public static final String PLACEHOLDER_START = "{{";
    public static final String PLACEHOLDER_END = "}}";

    public static List<TemplateData> parseTemplates(String code) throws ParseException{
        List<TemplateData> templates=new ArrayList<>();
        Document doc=Jsoup.parse(code,"", Parser.xmlParser());
        Elements elements = doc.getElementsByTag(TEMPLATE_TAG);
        for (Element element : elements){
            TemplateData template=new TemplateData();
            template.setType(element.attr(TEMPLATE_ATTR_TYPE));
            template.setName(element.attr(TEMPLATE_ATTR_NAME));
            template.setDisplayName(element.attr(TEMPLATE_ATTR_DISPLAYNAME));
            template.setUsage(element.attr(TEMPLATE_ATTR_USAGE));
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
            include.setAttributes(new TagAttributes(element.attributes()));
            include.setContent(element.html());
            list.add(include);
            TextNode placeholder=new TextNode(PLACEHOLDER_START+(list.size()-1)+PLACEHOLDER_END);
            element.replaceWith(placeholder);
        }
        data.setParsedCode(doc.html());
        return true;
    }

    private static TemplateInclude getTemplateInclude(Element element){
        String type=element.attr(TEMPLATE_ATTR_TYPE);
        if (type.isEmpty()) {
            Log.warn("element without type: "+type);
            return null;
        }
        switch (type){
            case ContentInclude.KEY :
                 return new ContentInclude();
            case FieldInclude.KEY :
                return new FieldInclude();
            case JspInclude.KEY :
                return new JspInclude();
            case PartInclude.KEY :
                return new PartInclude();
            case ResourceInclude.KEY :
                return new ResourceInclude();
            case SectionInclude.KEY :
                return new SectionInclude();
            case SnippetInclude.KEY :
                return new SnippetInclude();
            //controls
            case BreadcrumbControl.KEY :
                return BreadcrumbControl.getInstance();
            case DocumentListControl.KEY :
                return DocumentListControl.getInstance();
            case HeadControl.KEY :
                return HeadControl.getInstance();
            case LayerControl.KEY :
                return LayerControl.getInstance();
            case MainMenuControl.KEY :
                return MainMenuControl.getInstance();
            case MessageControl.KEY :
                return MessageControl.getInstance();
            case SubMenuControl.KEY :
                return SubMenuControl.getInstance();
            case TopNavControl.KEY :
                return TopNavControl.getInstance();
        }
        Log.warn("element without valid type: "+type);
        return null;
    }

}
