/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.lucene;

import de.bandika._base.IChangeListener;
import de.bandika._base.BaseThread;
import de.bandika.user.UserController;
import de.bandika.page.PageController;
import de.bandika.file.FileController;
import de.bandika.application.ApplicationContextListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;

public class SearchQueue implements IChangeListener {

  private static SearchQueue instance = null;

  public static SearchQueue getInstance() {
    if (instance == null) {
      instance = new SearchQueue();
      instance.initialize();
    }
    return instance;
  }

  public void initialize() {
    FileController.getInstance().getChangeListeners().add(this);
    PageController.getInstance().getChangeListeners().add(this);
    UserController.getInstance().getChangeListeners().add(this);
  }

  protected List<SearchActionData> actionList = Collections.synchronizedList(new LinkedList<SearchActionData>());
  protected final Object lockObj = 1;
  protected ActionThread actionThread = null;

  public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
    if (action.equals(IChangeListener.ACTION_ADDED))
      addAction(new SearchActionData(SearchActionData.ACTION_ADD_ID, itemId, messageKey));
    else if (action.equals(IChangeListener.ACTION_UPDATED))
      addAction(new SearchActionData(SearchActionData.ACTION_UPDATE_ID, itemId, messageKey));
    else if (action.equals(IChangeListener.ACTION_DELETED))
      addAction(new SearchActionData(SearchActionData.ACTION_DELETE_ID, itemId, messageKey));
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
          ApplicationContextListener.registerThread(actionThread);
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