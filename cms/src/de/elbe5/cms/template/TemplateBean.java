/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.catalina.FilePath;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.ZipUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TemplateBean {

    public static String TEMPLATE_INFO_START = "<%--TEMPLATE==";
    public static int TEMPLATE_INFO_START_LENGTH = TEMPLATE_INFO_START.length();
    public static String TEMPLATE_INFO_END = "--%>";

    private static TemplateBean instance = null;

    public static TemplateBean getInstance() {
        if (instance == null) instance = new TemplateBean();
        return instance;
    }

    protected String getTemplateFolder(String type) {
        return "/WEB-INF/_jsp/"+type+"/";
    }

    public void importAllTemplates() {
        Map<String, List<TemplateData>> templates=TemplateBean.getInstance().getAllTemplatesFromImport();
        for (List<TemplateData> list : templates.values()){
            for (TemplateData data : list){
                writeTemplateFile(data.getType(), data.getFileName(), data.getCode());
            }
        }
    }

    public Map<String, List<TemplateData>> getAllTemplatesFromImport(){
        Map<String, List<TemplateData>> map=new HashMap<>();
        String basePath = FilePath.getAppPath();
        File file=new File(basePath+"/cmstemplates.mod");
        if (!file.exists())
            return map;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(FileUtil.readBinaryFile(file));
            ZipInputStream zin = new ZipInputStream(in);
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                readEntry(entry, zin, map);
                zin.closeEntry();
            }
            zin.close();
        } catch (IOException ioe) {
            return map;
        }
        return map;
    }

    protected void readEntry(ZipEntry entry, ZipInputStream zin, Map<String, List<TemplateData>> map) throws IOException {
        String name = entry.getName();
        if (!entry.isDirectory() && name.endsWith(".jsp")) {
            int pos=name.indexOf('/');
            if (pos==-1)
                return;
            String type=name.substring(0,pos);
            name=name.substring(pos+1);
            if (!map.containsKey(type)){
                map.put(type,new ArrayList<TemplateData>());
            }
            TemplateData data=getTemplateDataFromFileCode(ZipUtil.readFile(zin), type, name);
            if (data!=null)
                map.get(type).add(data);
        }
    }

    public List<TemplateData> getAllTemplates(String type) {
        List<TemplateData> list = new ArrayList<>();
        String basePath = FilePath.getAppROOTPath();
        String folderPath=basePath + getTemplateFolder(type);
        File dir = new File(folderPath);
        File[] files = dir.listFiles();
        if (files!=null){
            for (File file : files){
                String code=FileUtil.readTextFile(file);
                TemplateData data=getTemplateDataFromFileCode(code, type, file.getName());
                if (data==null)
                    continue;
                list.add(data);
            }
        }
        return list;
    }

    public TemplateData getTemplateDataFromFileCode(String code, String type, String name) {
        int start=code.indexOf(TEMPLATE_INFO_START);
        if (start==-1)
            return null;
        start+=TEMPLATE_INFO_START_LENGTH;
        int end=code.indexOf(TEMPLATE_INFO_END,start);
        if (end==-1)
            return null;
        String[] infos=code.substring(start,end).split("==");
        if (infos.length!=4)
            return null;
        TemplateData data=new TemplateData();
        data.setType(type);
        data.setFileName(name);
        data.setDisplayName(infos[0]);
        data.setDescription(infos[1]);
        if (!infos[3].equals("none"))
            data.setClassName(infos[2]);
        data.setUsage(infos[3]);
        data.setCode(code);
        return data;
    }

    public String readTemplateFile(String templateType, String templateName) {
        String basePath = FilePath.getAppROOTPath() + getTemplateFolder(templateType);
        String path = basePath + templateName;
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        return FileUtil.readTextFile(path);
    }

    public void writeTemplateFile(String templateType, String templateName, String code) {
        String basePath = FilePath.getAppROOTPath() + getTemplateFolder(templateType);
        Log.log("writing template file " + templateName);
        if (!StringUtil.isNullOrEmtpy(templateName) && !StringUtil.isNullOrEmtpy(code)) {
            String path = basePath + templateName;
            try {
                File f = new File(path);
                if (f.exists()) f.delete();
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write(code);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                Log.error("could not write template file " + path);
            }
        }
    }

    public void deleteTemplateFile(String templateType, String templateName) {
        String basePath = FilePath.getAppROOTPath() + getTemplateFolder(templateType);
        Log.info("deleting template file " + templateName);
        String path = basePath + templateName;
        File f = new File(path);
        if (f.exists())
            f.delete();
    }

}
