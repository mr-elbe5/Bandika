/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import de.elbe5.base.BaseThread;
import de.elbe5.application.AppContextListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SearchQueue {

    public static final int ACTION_INDEX_PAGES = 1;
    public static final int ACTION_INDEX_USERS = 2;

    private static SearchQueue instance = null;

    public static SearchQueue getInstance() {
        if (instance == null) {
            instance = new SearchQueue();
        }
        return instance;
    }

    protected List<Integer> actionList = Collections.synchronizedList(new LinkedList<>());
    protected final Object lockObj = 1;
    protected ActionThread actionThread = null;

    public void addAction(int actionId) {
        if (actionList.size() > 0) {
            int lastData = actionList.get(actionList.size() - 1);
            if (lastData == actionId) {
                return;
            }
        }
        actionList.add(actionId);
        if (actionThread == null) {
            synchronized (lockObj) {
                if (actionThread == null) {
                    actionThread = new ActionThread("ActionThread");
                    actionThread.startRunning();
                    AppContextListener.registerThread(actionThread);
                }
            }
        }
    }

    protected void executeAction(int actionId) {
        if (actionId == 0)
            return;
        switch (actionId) {
            case ACTION_INDEX_PAGES:
                SearchBean.getInstance().indexPages();
                break;
            case ACTION_INDEX_USERS:
                SearchBean.getInstance().indexUsers();
                break;
        }
    }

    public List<Integer> getActions() {
        return new ArrayList<>(actionList);
    }

    public void stop() {
        if (actionThread != null) {
            actionThread.stopRunning();
            actionThread.interrupt();
        }
    }

    private class ActionThread extends BaseThread {

        public int SLEEP_INTERVAL = 500;
        protected int actionId = 0;

        public ActionThread(String name) {
            super(name);
        }

        public int getCurrentAction() {
            return actionId;
        }

        public void run() {
            while (running) {
                actionId = 0;
                try {
                    if (!actionList.isEmpty())
                        actionId = actionList.remove(0);
                    executeAction(actionId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (actionId == 0) {
                    if (!running)
                        break;
                    try {
                        Thread.sleep(SLEEP_INTERVAL);
                    } catch (InterruptedException e) {
                        running = false;
                        if (actionList.isEmpty())
                            break;
                    }
                }
            }
            actionThread = null;
        }

    }

}
