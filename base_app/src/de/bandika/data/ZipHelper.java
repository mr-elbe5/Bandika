/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

import java.io.*;
import java.util.zip.ZipInputStream;

/**
 * Class ZipHelper is a helper data class for handling zipped content. <br>
 * Usage:
 */
public class ZipHelper {

    public static void ensureDirectory(String path) throws IOException {
        Log.info("ensure directory: " + path);
        FileHelper.ensureFolder(path);
    }

    public static byte[] getFile(ZipInputStream zin) throws IOException {
        final byte[] buffer = new byte[0x1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        while ((read = zin.read(buffer, 0, buffer.length)) > 0)
            out.write(buffer, 0, read);
        return out.toByteArray();
    }

    public static String readFile(ZipInputStream zin) throws IOException {
        final char[] buffer = new char[0x2048];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(zin, "US-ASCII");
        int read;
        while ((read = in.read(buffer, 0, buffer.length)) > 0)
            out.append(buffer, 0, read);
        String s = out.toString();
        int startPos = 0;
        //check for utf-8 / endian start
        while (s.charAt(startPos) > 0xff)
            startPos++;
        if (startPos > 0) {
            s = s.substring(startPos);
        }
        return s;
    }

    public static void writeFile(ZipInputStream zin, String path, boolean replace) throws IOException {
        byte[] buffer = new byte[2048];
        File f = new File(path);
        if (f.exists()) {
            if (replace) {
                f.delete();
                Log.info("replacing file: " + path);
            } else {
                Log.info("file exists already: " + path);
            }
        } else {
            Log.info("creating file: " + path);
        }
        FileOutputStream fos = new FileOutputStream(path);
        BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
        int size;
        while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
            bos.write(buffer, 0, size);
        }
        bos.flush();
        bos.close();
    }

}