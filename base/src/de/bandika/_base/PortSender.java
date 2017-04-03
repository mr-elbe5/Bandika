/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PortSender {

  public static Object sendObject(Object obj, String ipAddress, int port, int timeout) {
    Socket client = null;
    try {
      client = new Socket();
      try {
        client.connect(new InetSocketAddress(ipAddress, port), timeout);
      } catch (SocketTimeoutException ex) {
        Logger.warn(null, "connection timeout");
        return null;
      }
      ObjectOutputStream obj_out = new ObjectOutputStream(client.getOutputStream());
      obj_out.writeObject(obj);
      obj_out.flush();
      ObjectInputStream obj_in = new ObjectInputStream(client.getInputStream());
      try {
        obj = obj_in.readObject();
        return obj;
      } catch (Exception e) {
        Logger.error(null, "error reading answer object", e);
        return null;
      }
    } catch (Exception e) {
      Logger.error(null, "error sending object", e);
      return null;
    } finally {
      if (client != null) {
        try {
          client.close();
        } catch (Exception ignore) {
        }
      }
    }
  }

}