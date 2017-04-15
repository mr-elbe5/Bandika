/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.application.AppContextListener;
import de.bandika.base.cache.BaseCache;
import de.bandika.base.log.Log;
import de.bandika.cms.configuration.Configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimerCache extends BaseCache {

    public static final String CACHEKEY = "cache|timer";
    private static TimerCache instance = null;

    public static TimerCache getInstance() {
        if (instance == null) {
            instance = new TimerCache();
        }
        return instance;
    }

    protected List<TimerTaskData> tasks = new ArrayList<>();
    protected TimerThread timerThread = null;

    public String getCacheKey() {
        return CACHEKEY;
    }

    public void startThread() {
        int interval = Configuration.getInstance().getTimerInterval();
        Log.log("setting timer interval to " + interval + " sec");
        timerThread = new TimerThread(interval);
        Log.log("timer thread state = " + (timerThread.isAlive() ? "alive" : "down"));
        if (timerThread.isAlive()) {
            restartThread();
            return;
        }
        Log.log("starting timer thread...");
        timerThread.startRunning();
        AppContextListener.registerThread(timerThread);
    }

    public void restartThread() {
        Log.log("restarting timer thread...");
        if (timerThread != null) {
            timerThread.stopRunning();
        }
        timerThread = null;
        int interval = Configuration.getInstance().getTimerInterval();
        timerThread = new TimerThread(interval);
        timerThread.startRunning();
        AppContextListener.registerThread(timerThread);
    }

    @Override
    public void load() {
        tasks.clear();
        List<TimerTaskData> list = TimerBean.getInstance().getAllTimerTasks();
        Date now = TimerBean.getInstance().getServerTime();
        for (TimerTaskData task : list) {
            if (task.initialize(now)) {
                tasks.add(task);
            }
        }
    }

    public List<TimerTaskData> getTasks() {
        checkDirty();
        return tasks;
    }

    public TimerTaskData getTaskCopy(int timerId) {
        checkDirty();
        for (TimerTaskData task : tasks) {
            if (task.getId() == timerId) {
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

    public void updateTask(TimerTaskData data) {
        checkDirty();
        Date now = TimerBean.getInstance().getServerTime();
        for (int i = 0; i < tasks.size(); i++) {
            TimerTaskData task = tasks.get(i);
            if (task.getId() == data.getId()) {
                tasks.set(i, data);
                data.setNextExecution(data.computeNextExecution(now));
                break;
            }
        }
    }
}
