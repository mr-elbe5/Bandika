/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tree;

import de.bandika.base.data.BaseIdData;
import de.bandika.webbase.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Class SortData is the data class for sorting child pages. <br>
 * Usage:
 */
public class TreeNodeSortData extends BaseIdData implements Comparable<TreeNodeSortData> {

    protected int ranking = 0;
    protected String name = "";
    protected List<TreeNodeSortData> children = new ArrayList<>();

    public TreeNodeSortData() {
    }

    @Override
    public int compareTo(TreeNodeSortData node) {
        return ranking - node.ranking;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeNodeSortData> getChildren() {
        return children;
    }

    public boolean readSortRequestData(HttpServletRequest request) {
        int idx = RequestReader.getInt(request, "childIdx");
        int childRanking = RequestReader.getInt(request, "childRanking");
        TreeNodeSortData child = getChildren().remove(idx);
        if (childRanking >= getChildren().size()) {
            getChildren().add(child);
        } else {
            getChildren().add(childRanking, child);
        }
        for (int i = 0; i < getChildren().size(); i++) {
            getChildren().get(i).setRanking(i);
        }
        return true;
    }
}
