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
import de.bandika.page.fields.BaseField;

import java.sql.*;
import java.util.ArrayList;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.*;
import java.nio.CharBuffer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * Class AdminBean is the persistence class for basic administration. <br>
 * Usage:
 */
public class AdminBean extends Bean {

	public void readConfig() {
		Connection con = null;
		try {
			con = getConnection();
			readConfig(con);
			readFields(con);
			readBeans(con);
			readControllers(con);
			readAdminLinks(con);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			closeConnection(con);
		}
	}

	public void readConfig(Connection con) throws SQLException {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("select config_key,config_value from t_config");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int i=1;
				String key= rs.getString(i++);
				String value= rs.getString(i++);
				BaseConfig.getConfigs().put(key,value);
				Logger.info(null, key + " is: " + value);
			}
			rs.close();
		}
		finally {
			closeStatement(pst);
		}
	}

	protected void readFields(Connection con) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		BaseField.getFieldTypes().clear();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("select shortkey,field_class from t_field");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int i = 1;
				String shortKey = rs.getString(i++);
				String className = rs.getString(i++);
				Class cls = Class.forName(className);
				BaseField.addBaseFieldClass(shortKey, cls);
				Logger.info(null, "added field class " + cls.getName());
			}
			rs.close();
		}
		finally {
			closeStatement(pst);
		}
	}

	protected void readBeans(Connection con) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Bean.getBeans().clear();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("select shortkey,bean_class from t_bean");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int i = 1;
				String shortKey = rs.getString(i++);
				String className = rs.getString(i++);
				Class cls = Class.forName(className);
				Bean bean = (Bean) cls.newInstance();
				Bean.addBean(shortKey, bean);
				Logger.info(null, "added bean " + bean.getClass().getName());
			}
			rs.close();
		}
		finally {
			closeStatement(pst);
		}
	}

	protected void readControllers(Connection con) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Controller.getControllers().clear();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("select shortkey,controller_class from t_controller");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int i = 1;
				String shortKey = rs.getString(i++);
				String className = rs.getString(i++);
				Class cls = Class.forName(className);
				Controller controller = (Controller) cls.newInstance();
				Controller.addController(shortKey, controller);
				Logger.info(null, "added controller " + controller.getClass().getName());
			}
			rs.close();
		}
		finally {
			closeStatement(pst);
		}
	}

	protected void readAdminLinks(Connection con) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		AdminData.getLinks().clear();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("select name,url,ranking,editor,admin from t_adminlink order by ranking asc");
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				AdminData data = new AdminData();
				int i = 1;
				data.setName(rs.getString(i++));
				data.setUrl(rs.getString(i++));
				data.setRanking(rs.getInt(i++));
				data.setEditor(rs.getInt(i++) == 1);
				data.setAdmin(rs.getInt(i++) == 1);
				AdminData.getLinks().add(data);
			}
			rs.close();
		}
		finally {
			closeStatement(pst);
		}
	}

	public boolean writeConfig() {
		Connection con = startTransaction();
		try {
			writeConfig(con);
			return commitTransaction(con);
		}
		catch (Exception se) {
			return rollbackTransaction(con, se);
		}
	}

	protected void writeConfig(Connection con) throws SQLException {
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("update t_config set base_path=?");
			int i = 1;
			pst.setString(i++, BaseConfig.getBasePath());
			pst.executeUpdate();
		}
		finally {
			closeStatement(pst);
		}
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
        data.setDescription(rs.getString(i++));
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

  public void replaceStylePack(FileData file) throws Exception{
    Logger.info(null,"deploying style pack '"+file.getName()+"'");
    String basePath=BaseConfig.getBasePath()+"/";
    ByteArrayInputStream in =new ByteArrayInputStream(file.getBytes());
    ZipInputStream zin=new ZipInputStream(in);
    ZipEntry entry;
    while ((entry = zin.getNextEntry()) != null) {
      String name=entry.getName();
      if (entry.isDirectory()){
        Logger.info(null,"ensure directory: " + name);
        FileData.ensureFolder(basePath+name);
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

  public void writeTemplates(FileData file,boolean deleteOld) throws Exception{
    Logger.info(null,"deploying templates from '"+file.getName()+"'");
    String basePath=BaseConfig.getBasePath()+"/_tpl/";
    FileData.ensureFolder(basePath);
    if (deleteOld){
      Logger.info(null,"deleting old templates");
      File dir=new File(basePath);
      File[] files=dir.listFiles();
      for (int i=0;i<files.length;i++)
        files[i].delete();
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
    String basePath=BaseConfig.getBasePath()+"/_tpl/";
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
        pst1 = con.prepareStatement("select name t_template");
        ResultSet rs = pst1.executeQuery();
        while (rs.next())
          oldNames.add(rs.getString(1));
      }
      pst1 = con.prepareStatement("insert into t_template (name,description,html) values (?,?,' ')");
      pst2 = con.prepareStatement("update t_template description=?, html=' ' where name=?");
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
    String basePath=BaseConfig.getBasePath()+"/_tpl/";
    FileData.ensureFolder(basePath);
    FileData fdata=new FileData();
    Connection con = null;
    PreparedStatement pst = null;
    String sql = "select name,html from t_template";
    try {
      con = getConnection();
      pst = con.prepareStatement(sql);
      ResultSet rs = pst.executeQuery();
      while (rs.next()) {
        String name=rs.getString(1);
        if (!FileData.fileExists(basePath+name)){
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
