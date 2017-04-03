/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import de.bandika.application.Configuration;

public abstract class MasterResponse implements Response {

  public static final int TYPE_ADMIN = 0;
  public static final int TYPE_USER = 1;
  public static final int TYPE_ADMIN_POPUP = 2;
  public static final int TYPE_USER_POPUP = 3;

  int masterType = TYPE_USER;

  protected String title = "";

  public MasterResponse(int masterType) {
    this.masterType = masterType;
  }

  public MasterResponse(String title, int masterType) {
    this.title = title;
    this.masterType = masterType;
  }

  public String getMaster() {
    switch (masterType) {
      case TYPE_ADMIN:
        return "/_jsp/_master/" + Configuration.getConfigurationValue("adminMaster") + ".jsp";
      case TYPE_USER_POPUP:
        return "/_jsp/_master/" + Configuration.getConfigurationValue("userPopupMaster") + ".jsp";
      case TYPE_ADMIN_POPUP:
        return "/_jsp/_master/" + Configuration.getConfigurationValue("adminPopupMaster") + ".jsp";
      default:
        return "/_jsp/_master/" + Configuration.getConfigurationValue("layoutMaster") + ".jsp";
    }
  }

  public abstract String getJsp();

  public String getTitle() {
    return title;
  }

  public void setMessages(RequestData rdata) {
  }

}