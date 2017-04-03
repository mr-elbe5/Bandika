/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.timer;

import de.bandika._base.Logger;
import de.bandika._base.BaseThread;
import de.bandika.cluster.ClusterController;

import java.util.Date;

public class TimerThread extends BaseThread {

  protected int interval = 60;

  public TimerThread(int interval) {
    super("o5imsTimer");
    this.interval = interval * 1000;
  }

  public void run() {
    Logger.info(null, "timer started");
    while (running) {
      try {
        sleep(interval);
        checkTasks();
      } catch (InterruptedException e) {
        break;
      }
    }
    Logger.info(null, "timer stopped");
    running = false;
  }

  protected void checkTasks() {
    Date now = TimerBean.getInstance().getServerTime();
    boolean isMaster = ClusterController.getInstance().isMaster();
    for (TimerTaskData task : TimerCache.getInstance().getTasks()) {
      try {
        if (task.isActive())
          checkTask(task, now, isMaster);
      } catch (Exception e) {
        Logger.error(getClass(), "could not execute timer task", e);
      }
    }
  }

  protected void checkTask(TimerTaskData data, Date now, boolean isMaster) {
    if (now == null)
      return;
    Date next = data.getNextExecution();
    if (now.before(next))
      return;
    data.execute(now, isMaster);
  }

}
