/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.admin;

import de.bandika.base.*;
import de.bandika.data.FileData;

import java.sql.*;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.*;

/**
 * Class AdminBean is the persistence class for basic administration. <br>
 * Usage:
 */
public class AdminBean extends Bean {

  private static AdminBean instance=null;

  public static AdminBean getInstance(){
    if (instance==null)
      instance=new AdminBean();
    return instance;
  }

  protected BaseAppConfig getBaseConfig() {
    return AppConfig.getInstance();
  }

  public Connection getConnection() throws SQLException {
		return AppConfig.getInstance().getCmsConnection();
	}

  /**
   * Method getTemplateList returns the templateList of this TemplateBean object.
   *
   * @return the templateList (type ArrayList<TemplateData>) of this TemplateBean object.
   */
  public ArrayList<TemplateData> getTemplateList() {
    ArrayList<TemplateData> list = new ArrayList<TemplateData>();
    Connection con = null;
    PreparedStatement pst = null;
    TemplateData data;
    String sql = "select name,description from t_template order by name asc";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        int i = 1;
        data = new TemplateData();
        data.setName(rs.getString(i++));
        data.setDescription(rs.getString(i));
        list.add(data);
      }
      rs.close();
    }
    catch (Exception e) {
      Logger.error(getClass(), "could not read template list", e);
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
    return list;
  }

  @SuppressWarnings({"ResultOfMethodCallIgnored"})
  public void replaceStylePack(FileData file) throws Exception{
    Logger.info(null,"deploying style pack '"+file.getName()+"'");
    String basePath= AppConfig.getInstance().getBasePath()+"/";
    ByteArrayInputStream in =new ByteArrayInputStream(file.getBytes());
    ZipInputStream zin=new ZipInputStream(in);
    ZipEntry entry;
    while ((entry = zin.getNextEntry()) != null) {
      String name=entry.getName();
      if (entry.isDirectory()){
        Logger.info(null,"ensure directory: " + name);
        FileHelper.ensureFolder(basePath+name);
      }
      else{
        byte[] buffer = new byte[2048];
        File f=new File(basePath+entry.getName());
        if (f.exists()){
          f.delete();
          Logger.info(null,"replace file: " + name +" ("+entry.getSize()+")");
        }
        else{
          Logger.info(null,"create file: " + name +" ("+entry.getSize()+")");
        }
        FileOutputStream fos = new FileOutputStream(basePath+name);
        BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
        int size;
        while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
          bos.write(buffer, 0, size);
        }
        bos.flush();
        bos.close();
      }
      zin.closeEntry();
    }
  }

  public void replaceTemplates(FileData file) throws Exception{
    writeTemplates(file,true);
    saveTemplates(true);
  }

  public void updateTemplates(FileData file) throws Exception{
    writeTemplates(file,false);
    saveTemplates(false);
  }

  @SuppressWarnings({"ResultOfMethodCallIgnored"})
  public void writeTemplates(FileData file,boolean deleteOld) throws Exception{
    Logger.info(null,"deploying templates from '"+file.getName()+"'");
    String basePath= AppConfig.getInstance().getBasePath()+"/_tpl/";
    FileHelper.ensureFolder(basePath);
    if (deleteOld){
      Logger.info(null,"deleting old templates");
      File dir=new File(basePath);
      File[] files=dir.listFiles();
      for (File f : files) f.delete();
    }
    ByteArrayInputStream in =new ByteArrayInputStream(file.getBytes());
    ZipInputStream zin=new ZipInputStream(in);
    ZipEntry entry;
    while ((entry = zin.getNextEntry()) != null) {
      String name=entry.getName();
      if (entry.isDirectory()){
        continue;
      }
      byte[] buffer = new byte[2048];
      File f=new File(basePath+entry.getName());
      if (f.exists()){
        f.delete();
        Logger.info(null,"replace template: " + name +" ("+entry.getSize()+")");
      }
      else{
        Logger.info(null,"create template: " + name +" ("+entry.getSize()+")");
      }
      FileOutputStream fos = new FileOutputStream(basePath+name);
      BufferedOutputStream fbos = new BufferedOutputStream(fos, buffer.length);
      int size;
      while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
        fbos.write(buffer, 0, size);
      }
      fbos.flush();
      fbos.close();
      zin.closeEntry();
    }
  }

  public void saveTemplates(boolean deleteOld) throws Exception{
    Logger.info(null,"saving templates");
    String basePath= AppConfig.getInstance().getBasePath()+"/_tpl/";
    Connection con=null;
    PreparedStatement pst1 = null;
    PreparedStatement pst2 = null;
    PreparedStatement pst3 = null;
    PreparedStatement pst4 = null;
    ArrayList<String> oldNames=new ArrayList<String>();
    char[] chars=new char[1024];
    try {
      con = getConnection();
      if (deleteOld){
        pst1 = con.prepareStatement("delete from t_template");
        pst1.executeUpdate();
        pst1.close();
      }
      else{
        pst1 = con.prepareStatement("select name from t_template");
        ResultSet rs = pst1.executeQuery();
        while (rs.next())
          oldNames.add(rs.getString(1));
      }
      pst1 = con.prepareStatement("insert into t_template (name,description,html) values (?,?,' ')");
      pst2 = con.prepareStatement("update t_template set description=?, html=' ' where name=?");
      pst3 = con.prepareStatement("select html from t_template where name=?");
      pst4 = con.prepareStatement("update t_template set html=? where name=?");
      File dir=new File(basePath);
      File[] files=dir.listFiles();
      for (File file : files){
        StringBuffer buffer=new StringBuffer();
        FileReader reader=new FileReader(file);
        int size;
        while ((size = reader.read(chars, 0, chars.length)) != -1) {
          buffer.append(chars, 0, size);
        }
        reader.close();
        String name=file.getName();
        String html=buffer.toString();
        String description="";
        try{
          int pos1=html.indexOf("<%--")+4;
          int pos2=html.indexOf("--%>");
          description=html.substring(pos1,pos2).trim();
        }
        catch(Exception ignore){}
        if (!oldNames.contains(name)){
          pst1.setString(1, name);
          pst1.setString(2, description);
          pst1.executeUpdate();
        }
        else{
          pst2.setString(1, description);
          pst2.setString(2, name);
          pst2.executeUpdate();
        }
        pst3.setString(1, name);
        ResultSet rs = pst3.executeQuery();
        rs.next();
        Clob clob = rs.getClob(1);
        rs.close();
        writeClob(clob, html);
        pst4.setClob(1, clob);
        pst4.setString(2, name);
        pst4.executeUpdate();
      }
    } finally {
      closeStatement(pst1);
      closeStatement(pst2);
      closeStatement(pst3);
      closeStatement(pst4);
      closeConnection(con);
    }
  }

  public void ensureTemplates(){
    Logger.info(null,"ensuring templates from database");
    String basePath= AppConfig.getInstance().getBasePath()+"/_tpl/";
    FileHelper.ensureFolder(basePath);
    Connection con = null;
    PreparedStatement pst = null;
    String sql = "select name,html from t_template";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        String name=rs.getString(1);
        if (!FileHelper.fileExists(basePath+name)){
          String html=readClob(rs.getClob(2));
          FileWriter writer=new FileWriter(basePath+name);
          writer.write(html);
          writer.flush();
          writer.close();
        }
      }
      rs.close();
    }
    catch (Exception e) {
      Logger.error(getClass(), "could not read template list", e);
    } finally {
      closeStatement(pst);
      closeConnection(con);
    }
  }

}
