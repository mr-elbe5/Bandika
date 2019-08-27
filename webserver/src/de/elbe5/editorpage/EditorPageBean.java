/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.editorpage;

import de.elbe5.page.PageData;
import de.elbe5.page.PageExtrasBean;

import java.sql.*;
import java.time.LocalDateTime;

public class EditorPageBean extends PageExtrasBean {

    private static EditorPageBean instance = null;

    public static EditorPageBean getInstance() {
        if (instance == null) {
            instance = new EditorPageBean();
        }
        return instance;
    }

    private static String GET_PAGE_EXTRAS_SQL = "SELECT content FROM t_editor_page WHERE id=?";

    public void readPageExtras(Connection con, PageData pageData) throws SQLException {
        if (!(pageData instanceof EditorPageData))
            return;
        EditorPageData data = (EditorPageData) pageData;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(GET_PAGE_EXTRAS_SQL);
            pst.setInt(1, data.getId());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    data.setContent(rs.getString(i));
                }
            }
        } finally {
            closeStatement(pst);
        }
    }

    private static String INSERT_PAGE_EXTRAS_SQL = "insert into t_editor_page (content,id) values(?,?)";
    private static String UPDATE_PAGE_EXTRAS_SQL = "update t_editor_page set content=? where id=?";

    public void writePageExtras(Connection con, PageData pageData) throws Exception {
        if (!(pageData instanceof EditorPageData))
            return;
        EditorPageData data = (EditorPageData) pageData;
        LocalDateTime now = getServerTime(con);
        data.setChangeDate(now);
        if (data.isNew()) {
            data.setCreationDate(now);
        }
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement(data.isNew() ? INSERT_PAGE_EXTRAS_SQL : UPDATE_PAGE_EXTRAS_SQL);
            int i = 1;
            pst.setString(i++, data.getContent());
            pst.setInt(i, data.getId());
            pst.executeUpdate();
            pst.close();
        } finally {
            closeStatement(pst);
        }
    }
}
