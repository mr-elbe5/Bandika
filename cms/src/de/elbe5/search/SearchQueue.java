/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.search;

import de.elbe5.base.thread.BaseThread;
import de.elbe5.application.AppContextListener;

import java.util.*;

public class SearchQueue {

    private static SearchQueue instance = null;

    public static SearchQueue getInstance() {
        if (instance == null) {
            instance = new SearchQueue();
        }
        return instance;
    }

    protected List<SearchActionData> actionList = Collections.synchronizedList(new LinkedList<SearchActionData>());
    protected final Object lockObj = 1;
    protected ActionThread actionThread = null;

    public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {

    }

    public void onItemChanged(String type, String action, String name) {
    }

    public void addAction(SearchActionData data) {
        if (actionList.size() > 0) {
            SearchActionData lastData = actionList.get(actionList.size() - 1);
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

    protected void executeAction(SearchActionData data) {
        if (data == null)
            return;
        switch (data.getActionId()) {
            case SearchActionData.ACTION_INDEX_ALL:
                SearchBean.getInstance().indexAll();
                break;
            case SearchActionData.ACTION_ADD_ID:
                SearchBean.getInstance().addItem(data.getId(), data.getDataKey());
                break;
            case SearchActionData.ACTION_UPDATE_ID:
                SearchBean.getInstance().updateItem(data.getId(), data.getDataKey());
                break;
            case SearchActionData.ACTION_DELETE_ID:
                SearchBean.getInstance().deleteItem(data.getId(), data.getDataKey());
                break;
        }
    }

    public SearchActionData getRunningAction() {
        ActionThread th = actionThread;
        if (th == null)
            return null;
        return th.getCurrentAction();
    }

    public List<SearchActionData> getActions() {
        ArrayList<SearchActionData> lst = new ArrayList<SearchActionData>();
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
        protected SearchActionData data = null;

        public ActionThread(String name) {
            super(name);
        }

        public SearchActionData getCurrentAction() {
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