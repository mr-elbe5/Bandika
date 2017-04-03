/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.link;

import de.bandika._base.*;

public class LinkController extends Controller {

  public static final String LINKKEY_LINKS = "link|links";

  private static LinkController instance = null;

  public static LinkController getInstance() {
    if (instance == null) {
      instance = new LinkController();
    }
    return instance;
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

}