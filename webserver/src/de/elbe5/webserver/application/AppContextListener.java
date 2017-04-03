/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.application;

import de.elbe5.base.log.Log;
import de.elbe5.base.database.DbConnector;
import de.elbe5.base.thread.BaseThread;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.Map;

public class AppContextListener implements ServletContextListener {
    private static Map<String, BaseThread> threads = new HashMap<>();

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

    public AppContextListener() {
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Log.log("context destroyed - stopping threads");
        for (BaseThread thread : threads.values()) {
            if (thread.isAlive()) {
                Log.log("trying to stop thread " + thread.getName());
                try {
                    thread.stopRunning();
                    thread.interrupt();
                    Thread.sleep(500);
                    if (thread.isAlive()) Log.warn("thread is still alive: " + thread.getName());
                    else Log.log("thread successfully stopped: " + thread.getName());
                } catch (Exception ignore) {
                }
            }
        }
        threads.clear();
        Log.log("context destroyed - purging connections");
        DbConnector.getInstance().clear();
        try {
            Thread.sleep(1000);
        } catch (Exception ignore) {
        }
        Log.log("finished stopping threads");
    }
}
