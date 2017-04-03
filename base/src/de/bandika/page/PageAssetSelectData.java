/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import java.util.ArrayList;

/**
 * Class PageAssetSelectData is the data class for selecting page assets. <br>
 * Usage:
 */
public class PageAssetSelectData {

  public final static String ASSET_TYPE_FILE = "FILE";
  public final static String ASSET_TYPE_LINK = "LINK";

  protected ArrayList<String> availableTypes = new ArrayList<String>();
  protected String type;
  protected int pageId = 0;
  protected boolean dimensioned;
  protected int preferredWidth = 0;
  protected boolean forHtmlEditor = false;
  protected String assetType = ASSET_TYPE_FILE;
  protected int callbackFuncNum = 0;

  public ArrayList<String> getAvailableTypes() {
    return availableTypes;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public boolean isDimensioned() {
    return dimensioned;
  }

  public void setDimensioned(boolean dimensioned) {
    this.dimensioned = dimensioned;
  }

  public int getPreferredWidth() {
    return preferredWidth;
  }

  public void setPreferredWidth(int preferredWidth) {
    this.preferredWidth = preferredWidth;
  }

  public boolean isForHtmlEditor() {
    return forHtmlEditor;
  }

  public void setForHtmlEditor(boolean forHtmlEditor) {
    this.forHtmlEditor = forHtmlEditor;
  }

  public String getAssetType() {
    return assetType;
  }

  public void setAssetType(String assetType) {
    this.assetType = assetType;
  }

  public int getCallbackFuncNum() {
    return callbackFuncNum;
  }

  public void setCallbackFuncNum(int callbackFuncNum) {
    this.callbackFuncNum = callbackFuncNum;
  }

}