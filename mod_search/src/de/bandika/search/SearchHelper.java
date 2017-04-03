/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.search;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

public class SearchHelper {

    public static String getSearchContent(byte[] bytes, String name, String contentType) {
        String searchContent = "";
        Parser parser = new AutoDetectParser();
        try {
            InputStream in = new ByteArrayInputStream(bytes);
            StringWriter writer = new StringWriter();
            ContentHandler handler = new BoilerpipeContentHandler(writer);
            Metadata metaData = new Metadata();
            metaData.set(Metadata.RESOURCE_NAME_KEY, name);
            metaData.set(Metadata.CONTENT_TYPE, contentType);
            parser.parse(in, handler, metaData, new ParseContext());
            in.close();
            writer.flush();
            searchContent = writer.toString().trim();
            searchContent = searchContent.replaceAll("\\s+", " ");
        } catch (Exception ignore) {
        }
        return searchContent;
    }

    public static String getSearchContentFromHtml(String html) {
        String searchContent = "";
        Parser parser = new AutoDetectParser();
        try {
            InputStream in = new ByteArrayInputStream(html.getBytes("UTF-8"));
            StringWriter writer = new StringWriter();
            ContentHandler handler = new BodyContentHandler(writer);
            Metadata metaData = new Metadata();
            metaData.set(Metadata.CONTENT_TYPE, "text/html");
            parser.parse(in, handler, metaData, new ParseContext());
            in.close();
            writer.flush();
            searchContent = writer.toString().trim();
            searchContent = searchContent.replaceAll("\\s+", " ");
        } catch (Exception ignore) {
        }
        return searchContent;
    }

}