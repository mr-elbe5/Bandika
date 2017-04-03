/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.timer;

import de.bandika.data.BaseCache;
import de.bandika.data.IChangeListener;
import de.bandika.data.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimerCache extends BaseCache implements IChangeListener {

    public static final String CACHEKEY = "cache|timer";

    private static TimerCache instance = null;

    public static TimerCache getInstance() {
        if (instance == null) {
            instance = new TimerCache();
            instance.initialize();
        }
        return instance;
    }

    protected List<TimerTaskData> tasks = new ArrayList<>();

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void initialize() {
        checkDirty();
        //todo
        //ClusterMessageProcessor.getInstance().putListener(CACHEKEY, this);
    }

    public void load() {
        tasks.clear();
        List<TimerTaskData> list = TimerBean.getInstance().getAllTimerTasks();
        Date now = TimerBean.getInstance().getServerTime();
        for (TimerTaskData task : list) {
            if (task.initialize(now))
                tasks.add(task);
        }
    }

    public List<TimerTaskData> getTasks() {
        checkDirty();
        return tasks;
    }

    public TimerTaskData getTaskCopy(String name) {
        checkDirty();
        for (TimerTaskData task : tasks) {
            if (task.getName().equals(name)) {
                try {
                    return (TimerTaskData) task.clone();
                } catch (CloneNotSupportedException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public void reloadTask(TimerTaskData data) {
        checkDirty();
        TimerBean.getInstance().reloadTimerTask(data);
        Date now = TimerBean.getInstance().getServerTime();
        data.setNextExecution(data.computeNextExecution(now));
    }

    public void replaceTask(TimerTaskData data) {
        checkDirty();
        Date now = TimerBean.getInstance().getServerTime();
        for (int i = 0; i < tasks.size(); i++) {
            TimerTaskData task = tasks.get(i);
            if (task.getName().equals(data.getName())) {
                tasks.set(i, data);
                data.setNextExecution(data.computeNextExecution(now));
                break;
            }
        }
        //todo
        //ClusterController.getInstance().broadcastMessage(CACHEKEY, ClusterMessage.ACTION_SETDIRTY, 0);
    }

    public void itemChanged(String messageKey, String action, String item, int itemId, boolean internal) {
        Log.info(String.format("%s changed with action %s, id %s", messageKey, action, itemId));
        if (action.equals(IChangeListener.ACTION_SETDIRTY))
            setDirty();
    }

}