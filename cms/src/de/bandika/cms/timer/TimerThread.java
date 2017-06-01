/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.base.log.Log;
import de.bandika.base.thread.BaseThread;

import java.time.LocalDateTime;

public class TimerThread extends BaseThread {

    protected int interval = 60;

    public TimerThread(int interval) {
        super("CMSTaskTimer");
        this.interval = interval * 1000;
    }

    @Override
    public void run() {
        Log.log("timer started");
        while (running) {
            try {
                sleep(interval);
                checkTasks();
            } catch (InterruptedException e) {
                break;
            }
        }
        Log.log("timer stopped");
        running = false;
    }

    protected void checkTasks() {
        LocalDateTime now = TimerBean.getInstance().getServerTime();
        for (TimerTask task : TimerCache.getInstance().getTasks().values()) {
            try {
                if (task.isActive()) {
                    checkTask(task, now);
                }
            } catch (Exception e) {
                Log.error("could not execute timer task", e);
            }
        }
    }

    protected void checkTask(TimerTask data, LocalDateTime now) {
        if (now == null) {
            return;
        }
        LocalDateTime next = data.getNextExecution();
        if (now.isAfter(next)) {
            data.execute(now);
        }
    }
}
