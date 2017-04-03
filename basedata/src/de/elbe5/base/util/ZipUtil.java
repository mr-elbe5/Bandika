/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Class ZipHelper is a helper data class for handling zipped content. <br>
 * Usage:
 */
public class ZipUtil {

    public static String readFile(ZipInputStream zin) throws IOException {
        final char[] buffer = new char[0x2048];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(zin, "US-ASCII");
        int read;
        while ((read = in.read(buffer, 0, buffer.length)) > 0) out.append(buffer, 0, read);
        String s = out.toString();
        int startPos = 0;
        //check for utf-8 / endian start
        while (s.charAt(startPos) > 0xff) startPos++;
        if (startPos > 0) {
            s = s.substring(startPos);
        }
        return s;
    }

    public static void addEntry(ZipOutputStream zout, String name, byte[] bytes) throws IOException{
        zout.putNextEntry(new ZipEntry(name));
        zout.write(bytes);
        zout.closeEntry();
    }

}