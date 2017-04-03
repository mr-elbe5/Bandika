/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import java.util.ArrayList;
import java.util.List;

/**
 * Class PageAssetSelectData is the data class for selecting page assets. <br>
 * Usage:
 */
public class PageAssetSelectData {

    public final static String ASSET_USAGE_FILE = "FILE";
    public final static String ASSET_USAGE_LINK = "LINK";

    protected List<String> availableTypes = new ArrayList<>();
    protected String activeType;
    protected int pageId = 0;
    protected boolean dimensioned;
    protected int preferredWidth = 0;
    protected boolean forHtmlEditor = false;
    protected String assetUsage = ASSET_USAGE_FILE;
    protected int callbackFuncNum = 0;

    public List<String> getAvailableTypes() {
        return availableTypes;
    }

    public String getActiveType() {
        return activeType;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public boolean isSingleType() {
        return getAvailableTypes().size() <= 1;
    }


    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public boolean isForHtmlEditor() {
        return forHtmlEditor;
    }

    public void setForHtmlEditor(boolean forHtmlEditor) {
        this.forHtmlEditor = forHtmlEditor;
    }

    public String getAssetUsage() {
        return assetUsage;
    }

    public void setAssetUsage(String assetUsage) {
        this.assetUsage = assetUsage;
    }

    public int getCallbackFuncNum() {
        return callbackFuncNum;
    }

    public void setCallbackFuncNum(int callbackFuncNum) {
        this.callbackFuncNum = callbackFuncNum;
    }

}