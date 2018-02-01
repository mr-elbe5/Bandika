/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.webbase.application.AppContextListener;
import de.bandika.base.log.Log;
import de.bandika.cms.configuration.Configuration;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TimerController {

    private static TimerController instance = null;

    public static TimerController getInstance() {
        if (instance == null) {
            instance = new TimerController();
        }
        return instance;
    }

    protected Map<String, TimerTask> tasks = new HashMap<>();
    protected TimerThread timerThread = null;

    public void registerTimerTask(TimerTask task){
        TimerBean.getInstance().assertTimerTask(task);
        tasks.put(task.getName(),task);
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

    public void loadTasks() {
        LocalDateTime now = TimerBean.getInstance().getServerTime();
        for (TimerTask task : tasks.values()) {
            TimerBean.getInstance().readTimerTask(task);
            task.setNextExecution(task.computeNextExecution(now));
        }
    }

    public void loadTask(String name) {
        TimerTask task=tasks.get(name);
        TimerBean.getInstance().readTimerTask(task);
        LocalDateTime now = TimerBean.getInstance().getServerTime();
        task.setNextExecution(task.computeNextExecution(now));
    }

    public Map<String, TimerTask> getTasks() {
        return tasks;
    }

    public TimerTask getTaskCopy(String name) {
        TimerTask task = tasks.get(name);
        try {
            return (TimerTask) task.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
