/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.communication;

import de.bandika.base.*;

/**
 * Class UserServlet is the servlet class for receiving requests and returning responses for non-html output. <br>
 * Usage:
 */
public class CommunicationServlet extends BaseServlet {

  protected Controller getController() {
    return new CommunicationController();
  }

  protected BaseAppConfig getBaseConfig() {
    return AppConfig.getInstance();
  }

}