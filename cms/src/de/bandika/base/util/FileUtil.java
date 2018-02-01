/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.base.util;

import java.io.*;

public class FileUtil {

    public static String readTextFile(String path) {
        File f = new File(path);
        return readTextFile(f);
    }

    public static String readTextFile(File f) {
        StringBuilder sb = new StringBuilder();
        try {
            if (!f.exists()) {
                return "";
            }
            FileReader reader = new FileReader(f);
            char[] chars = new char[4096];
            int len = 4096;
            while (len > 0) {
                len = reader.read(chars, 0, 4096);
                if (len > 0) {
                    sb.append(chars, 0, len);
                }
            }
            reader.close();
        } catch (IOException e) {
            return "";
        }
        return sb.toString();
    }

    public static byte[] readBinaryFile(File f) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (!f.exists()) {
                return null;
            }
            FileInputStream fin = new FileInputStream(f);
            byte[] bytes = new byte[4096];
            int len = 4096;
            while (len > 0) {
                len = fin.read(bytes, 0, 4096);
                if (len > 0) {
                    out.write(bytes, 0, len);
                }
            }
            out.flush();
            fin.close();
        } catch (IOException e) {
            return null;
        }
        return out.toByteArray();
    }

    public static boolean ensureFolder(String path) {
        File f = new File(path);
        if (f.exists()) {
            return f.isDirectory();
        }
        return f.mkdirs();
    }

    public static void touch(File file) throws IOException {
        long timestamp = System.currentTimeMillis();
        touch(file, timestamp);
    }

    public static void touch(File file, long timestamp) throws IOException {
        if (!file.exists()) {
            new FileOutputStream(file).close();
        }
        file.setLastModified(timestamp);
    }

    public static String getFileNameFromPath(String path) {
        if (path == null) {
            return path;
        }
        String uri = path.replace('\\', '/');
        int pos = uri.lastIndexOf('/');
        if (pos == -1) {
            return uri;
        }
        return uri.substring(pos + 1);
    }

    public static String getExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return null;
        }
        return fileName.substring(pos).toLowerCase();
    }

    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) {
            return fileName;
        }
        int pos = fileName.lastIndexOf('.');
        if (pos == -1) {
            return fileName;
        }
        return fileName.substring(0, pos);
    }

}
