/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.util.Date;

/**
 * Class BaseData is the base class for all data classes. <br>
 * Usage:
 */
public class BaseData {

  public static final int ID_MIN = 1000;

  protected boolean beingCreated = false;
  protected Date changeDate = new Date();

  public boolean isBeingCreated() {
    return beingCreated;
  }

  public void setBeingCreated(boolean beingCreated) {
    this.beingCreated = beingCreated;
  }

  public Date getChangeDate() {
    return changeDate;
  }

  public java.sql.Timestamp getSqlChangeDate() {
    return new java.sql.Timestamp(changeDate.getTime());
  }

  public void setChangeDate(Date changeDate) {
    this.changeDate = changeDate;
  }

  public Date setChangeDate() {
    changeDate = new Date();
    return changeDate;
  }

  public void prepareEditing() throws Exception {
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
  }

  public void stopEditing() throws Exception {
    setBeingCreated(false);
  }

  public boolean isComplete() {
    return true;
  }

}
