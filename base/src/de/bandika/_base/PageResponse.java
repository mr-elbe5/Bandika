/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import de.bandika.menu.MenuCache;

/**
 * Class SystemResponse is the response class for showing a page within a master
 * and layout page <br>
 * Usage:
 */
public class PageResponse implements Response {

  protected int id = 0;
  protected String title = "";

  public PageResponse(int id) {
    this.id = id;
  }

  public PageResponse(int id, String title) {
    this.id = id;
    this.title = title;
  }

  public String getMaster() {
    return "/_jsp/_master/" + MenuCache.getInstance().getMasterTemplate(id) + ".jsp";
  }

  public String getLayout() {
    return "/_jsp/_layout/" + MenuCache.getInstance().getLayoutTemplate(id) + ".jsp";
  }

  public String getTitle() {
    return title;
  }

}