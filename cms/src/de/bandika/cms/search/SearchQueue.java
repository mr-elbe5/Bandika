/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.search;

import de.bandika.webbase.application.AppContextListener;
import de.bandika.base.thread.BaseThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SearchQueue {

    private static SearchQueue instance = null;

    public static SearchQueue getInstance() {
        if (instance == null) {
            instance = new SearchQueue();
        }
        return instance;
    }

    protected List<SearchQueueAction> actionList = Collections.synchronizedList(new LinkedList<SearchQueueAction>());
    protected final Object lockObj = 1;
    protected ActionThread actionThread = null;

    public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {

    }

    public void onItemChanged(String type, String action, String name) {
    }

    public void addAction(SearchQueueAction data) {
        if (actionList.size() > 0) {
            SearchQueueAction lastData = actionList.get(actionList.size() - 1);
            if (lastData.isEqual(data)) {
                return;
            }
        }
        actionList.add(data);
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

    protected void executeAction(SearchQueueAction data) {
        if (data == null)
            return;
        switch (data.getActionId()) {
            case SearchQueueAction.ACTION_INDEX_ALL_CONTENT:
                SearchBean.getInstance().indexAllContent();
                break;
            case SearchQueueAction.ACTION_INDEX_ALL_USERS:
                SearchBean.getInstance().indexAllUsers();
                break;
            case SearchQueueAction.ACTION_ADD_ID:
                SearchBean.getInstance().addItem(data.getId(), data.getDataType());
                break;
            case SearchQueueAction.ACTION_UPDATE_ID:
                SearchBean.getInstance().updateItem(data.getId(), data.getDataType());
                break;
            case SearchQueueAction.ACTION_DELETE_ID:
                SearchBean.getInstance().deleteItem(data.getId(), data.getDataType());
                break;
        }
    }

    public SearchQueueAction getRunningAction() {
        ActionThread th = actionThread;
        if (th == null)
            return null;
        return th.getCurrentAction();
    }

    public List<SearchQueueAction> getActions() {
        ArrayList<SearchQueueAction> lst = new ArrayList<SearchQueueAction>();
        lst.addAll(actionList);
        return lst;
    }

    public void stop() {
        if (actionThread != null) {
            actionThread.stopRunning();
            actionThread.interrupt();
        }
    }

    private class ActionThread extends BaseThread {

        public int SLEEP_INTERVAL = 500;
        protected SearchQueueAction data = null;

        public ActionThread(String name) {
            super(name);
        }

        public SearchQueueAction getCurrentAction() {
            return data;
        }

        public void run() {
            while (running) {
                data = null;
                try {
                    if (!actionList.isEmpty())
                        data = actionList.remove(0);
                    executeAction(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (data == null) {
                    if (!running)
                        break;
                    try {
                        sleep(SLEEP_INTERVAL);
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