/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import java.io.*;
import java.util.ArrayList;

/**
 * Class FileData is a data class holding file data (name, bytes, content type). <br>
 * Usage:
 */
public class FileData extends BaseData {

	protected String name = null;
	protected String contentType = null;
	protected byte[] bytes = null;

	public FileData(){
	}

	public FileData(String name){
		this.name=name;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		if (name == null)
			return null;
		int pos = name.lastIndexOf(".");
		if (pos == -1)
			return null;
		return name.substring(pos + 1).toLowerCase();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

  public static boolean fileExists(String path){
    File f = new File(path);
    return f.exists();
  }

	public static boolean writeFile(FileData fdata, String basePath) {
		if (!basePath.endsWith("/"))
			basePath = basePath + "/";
		try {
			File f = new File(basePath + fdata.getName());
			if (f.exists())
				f.delete();
			f.createNewFile();
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(fdata.getBytes());
			fout.flush();
			fout.close();
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}

	public static boolean readFile(FileData fdata, String basePath) {
		if (!basePath.endsWith("/"))
			basePath = basePath + "/";
		try {
			File f = new File(basePath + fdata.getName());
			if (!f.exists())
				return false;
			FileInputStream fin = new FileInputStream(f);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] bytes = new byte[4096];
			int len = 4096;
			while (len > 0) {
				len = fin.read(bytes, 0, 4096);
				if (len > 0)
					bout.write(bytes, 0, len);
			}
			fin.close();
			fdata.setBytes(bout.toByteArray());
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}

	public static void deleteFile(FileData fdata, String basePath) {
		if (!basePath.endsWith("/"))
			basePath = basePath + "/";
		File f = new File(basePath + fdata.getName());
		if (f.exists())
			f.delete();
	}

  public static boolean ensureFolder(String path) {
		File f = new File(path);
		if (f.exists())
			return f.isDirectory();
		f.mkdirs();
		return true;
	}

	public static ArrayList<FileData> getFolderFiles(String path){
		ArrayList<FileData> files=new ArrayList<FileData>();
	  File f=new File(path);
		for (File file :f.listFiles()){
		  if (file.isFile())
				files.add(new FileData(file.getName()));
		}
		return files;
	}

	public static ArrayList<String> getSubFolders(String path){
		ArrayList<String> folders=new ArrayList<String>();
	  File f=new File(path);
		for (File file : f.listFiles()){
		  if (file.isDirectory())
				folders.add(path+'/'+file.getName());
		}
		return folders;
	}

	public static void removeFolder(String path) {
		File f = new File(path);
		if (f.exists())
			f.delete();
	}

}
