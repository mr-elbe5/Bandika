/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.menu;

import de.bandika.sql.PersistenceBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import de.bandika.data.Log;

/**
 * Class MenuBean is the class for reading the menu for caching. <br>
 * Usage:
 */
public class MenuBean extends PersistenceBean {

    private static MenuBean instance = null;

    public static MenuBean getInstance() {
        if (instance == null)
            instance = new MenuBean();
        return instance;
    }

    public Map<Locale,Integer> readHomePageIds() {
        Map<Locale,Integer> map = new HashMap<>();
        Connection con = null;
        PreparedStatement pst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select t1.id,t2.locale from t_page t1, t_page_locale t2 where t1.id=t2.id and t1.parent_id is null");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                int id=rs.getInt(i++);
                String locale=rs.getString(i);
                try{
                    map.put(new Locale(locale), id);
                }
                catch (Exception e){
                    Log.error("no appropriate locale",e);
                }
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return map;
    }

    public List<MenuData> readCache() {
        List<MenuData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        PreparedStatement rightsPst = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("select t1.id,t1.change_date,t1.parent_id,t1.ranking,t1.name,t1.path,t1.master_template,t1.layout_template,t1.restricted,t1.inherits_rights,t1.visible,t2.published_version,t2.draft_version " +
                    "from t_page t1, t_page_current t2 " +
                    "where t1.id=t2.id " +
                    "order by t1.parent_id nulls first, t1.ranking");
            rightsPst = con.prepareStatement("select group_id,rights from t_page_right where page_id=?");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                MenuData data = new MenuData();
                data.setId(rs.getInt(i++));
                data.setChangeDate(rs.getTimestamp(i++));
                data.setParentId(rs.getInt(i++));
                data.setRanking(rs.getInt(i++));
                data.setName(rs.getString(i++));
                data.setPath(rs.getString(i++));
                data.setMasterTemplate(rs.getString(i++));
                data.setLayoutTemplate(rs.getString(i++));
                data.setRestricted(rs.getBoolean(i++));
                data.setInheritsRights(rs.getBoolean(i++));
                data.setVisible(rs.getBoolean(i++));
                data.setPublishedVersion(rs.getInt(i++));
                data.setDraftVersion(rs.getInt(i));
                rightsPst.setInt(1, data.getId());
                ResultSet rrs = rightsPst.executeQuery();
                while (rrs.next())
                    data.getRights().put(rrs.getInt(1), rrs.getInt(2));
                rrs.close();
                list.add(data);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeStatement(rightsPst);
            closeConnection(con);
        }
        return list;
    }

}