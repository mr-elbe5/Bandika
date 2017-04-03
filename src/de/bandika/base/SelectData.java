/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

/**
 * Class SelectData is the base class for all paged selection classes. <br>
 * Usage:
 */
public abstract class SelectData {

  protected int itemsPerPage=10;
  protected int page = 0;

  public abstract int getItems();

  public int getItemsPerPage() {
    return itemsPerPage;
  }

  public void setItemsPerPage(int itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getMaxPage(){
    if (getItems()==0 || itemsPerPage==0)
      return 0;
    return (getItems()-1)/itemsPerPage;
  }

  public void setPreviousPage(){
    if (page==0)
      return;
    page--;
  }

  public void setNextPage(){
    if (page==getMaxPage())
      return;
    page++;
  }

  public int getMinItem(){
    return page*itemsPerPage;
  }

  public int getMaxItem(){
    if (page==getMaxPage())
      return getItems()-1;
    return (page+1)*itemsPerPage-1;
  }

}