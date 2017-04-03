/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {

    public static boolean fileExists(String path) {
        File f = new File(path);
        return f.exists();
    }

    public static boolean writeFile(String path, byte[] bytes) {
        try {
            File f = new File(path);
            if (f.exists() && !f.delete())
                return false;
            if (!f.createNewFile())
                return false;
            FileOutputStream out = new FileOutputStream(f);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean writeFile(String path, String content) {
        try {
            File f = new File(path);
            if (f.exists() && !f.delete())
                return false;
            if (!f.createNewFile())
                return false;
            FileWriter out = new FileWriter(f);
            out.write(content);
            out.flush();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static String readTextFile(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            File f = new File(path);
            if (!f.exists())
                return "";
            FileReader reader = new FileReader(f);
            char[] chars = new char[4096];
            int len = 4096;
            while (len > 0) {
                len = reader.read(chars, 0, 4096);
                if (len > 0)
                    sb.append(chars, 0, len);
            }
            reader.close();
        } catch (IOException e) {
            return "";
        }
        return sb.toString();
    }

    public static byte[] readBinaryFile(String path) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            File f = new File(path);
            if (!f.exists())
                return null;
            FileInputStream fin = new FileInputStream(f);
            byte[] bytes = new byte[4096];
            int len = 4096;
            while (len > 0) {
                len = fin.read(bytes, 0, 4096);
                if (len > 0)
                    out.write(bytes, 0, len);
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
        if (f.exists())
            return f.isDirectory();
        return f.mkdirs();
    }

    public static List<String> getSubFolders(String path) {
        List<String> folders = new ArrayList<>();
        File f = new File(path);
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory())
                    folders.add(path + '/' + file.getName());
            }
        }
        return folders;
    }

    public static boolean removeFolder(String path) {
        File f = new File(path);
        return !f.exists() || f.delete();
    }

}