/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika._base.BaseThread;
import de.bandika._base.Logger;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import java.util.HashMap;

public class ApplicationContextListener implements ServletContextListener {

  static HashMap<String, BaseThread> threads = new HashMap<String, BaseThread>();

  public static void registerThread(BaseThread thread) {
    if (threads.containsKey(thread.getName())) {
      try {
        threads.get(thread.getName()).stopRunning();
      } catch (Exception ignore) {
      }
    }
    threads.put(thread.getName(), thread);
  }

  public void contextInitialized(ServletContextEvent servletContextEvent) {
  }

  public ApplicationContextListener() {
  }

  public void contextDestroyed(ServletContextEvent servletContextEvent) {
    Logger.info(null, "context destroyed - stopping threads");
    for (BaseThread thread : threads.values()) {
      if (thread.isAlive()) {
        Logger.info(null, "trying to stop thread " + thread.getName());
        try {
          thread.stopRunning();
          thread.interrupt();
          Thread.sleep(500);
          if (thread.isAlive())
            Logger.warn(null, "thread is still alive: " + thread.getName());
          else
            Logger.info(null, "thread successfully stopped: " + thread.getName());
        } catch (Exception ignore) {
        }
      }
    }
    threads.clear();
    try {
      Thread.sleep(1000);
    } catch (Exception ignore) {
    }
    Logger.info(null, "finished stopping threads");
  }


}
