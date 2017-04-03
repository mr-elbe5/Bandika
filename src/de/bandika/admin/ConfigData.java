/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.admin;

import de.bandika.base.BaseData;
import de.bandika.base.AdminStrings;
import de.bandika.base.RequestError;
import de.bandika.http.SessionData;
import de.bandika.http.RequestData;

import java.util.ArrayList;

/**
 * Class UserData is the data class for users. <br>
 * Usage:
 */
public class ConfigData extends BaseData {

	protected String basePath;

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  @Override
	public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
		setBasePath(rdata.getParamString("basePath"));
		if (!isComplete(basePath))
			err.addErrorString(AdminStrings.notcomplete);
		if (!err.isEmpty()) {
			rdata.setError(err);
			return false;
		}
		return true;
	}
  
}