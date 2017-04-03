/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.page;

import de.bandika._base.BaseServlet;
import de.bandika._base.Controller;

/**
 * Class PageServlet is the servlet class for receiving requests and returning
 * responses for non-html output. <br>
 * Usage:
 */
public class PageServlet extends BaseServlet {

  private static final long serialVersionUID = 1L;

  protected Controller getController() {
    return PageController.getInstance();
  }

}