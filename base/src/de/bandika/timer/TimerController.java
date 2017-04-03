/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.timer;

import de.bandika._base.*;
import de.bandika._base.JspResponse;
import de.bandika._base.Response;
import de.bandika._base.MasterResponse;
import de.bandika.application.Configuration;
import de.bandika.application.StringCache;
import de.bandika.user.UserController;
import de.bandika.application.ApplicationContextListener;

import java.util.ArrayList;

public class TimerController extends Controller {

  public static final String LINKKEY_TIMERS = "link|timers";

  private static TimerController instance = null;

  public static TimerController getInstance() {
    if (instance == null) {
      instance = new TimerController();
      instance.initialize();
    }
    return instance;
  }

  protected TimerThread timerThread = null;

  public void initialize() {
    startThread();
  }

  public void startThread() {
    int interval = Integer.parseInt(Configuration.getConfigs().get("timerInterval"));
    Logger.info(null, "setting timer interval to " + interval + " sec");
    timerThread = new TimerThread(interval);
    Logger.info(null, "timer thread state = " + (timerThread.isAlive() ? "alive" : "down"));
    if (timerThread.isAlive()) {
      restartThread();
      return;
    }
    Logger.info(null, "starting timer thread...");
    timerThread.startRunning();
    ApplicationContextListener.registerThread(timerThread);
  }

  public void restartThread() {
    Logger.info(null, "restarting timer thread...");
    if (timerThread != null)
      timerThread.stopRunning();
    timerThread = null;
    int interval = Integer.parseInt(Configuration.getConfigs().get("timerInterval"));
    timerThread = new TimerThread(interval);
    timerThread.startRunning();
    ApplicationContextListener.registerThread(timerThread);
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    if (!sdata.isLoggedIn())
      return UserController.getInstance().openLogin();
    if (sdata.hasBackendLinkRight(LINKKEY_TIMERS)) {
      if (method.equals("openEditTimerTasks")) return openEditTimerTasks();
      if (method.equals("openEditTimerTask")) return openEditTimerTask(rdata, sdata);
      if (method.equals("saveTimerTask")) return saveTimerTask(rdata, sdata);
    }
    return noRight(rdata, MasterResponse.TYPE_USER);
  }

  protected Response showEditAll() {
    return new JspResponse("/_jsp/timer/editAllTimerTasks.jsp", StringCache.getString("timers"), MasterResponse.TYPE_ADMIN);
  }

  protected Response showEdit() {
    return new JspResponse("/_jsp/timer/editTimerTask.jsp", StringCache.getString("timer"), MasterResponse.TYPE_ADMIN);
  }

  public Response openEditTimerTasks() throws Exception {
    return showEditAll();
  }

  public Response openEditTimerTask(RequestData rdata, SessionData sdata)
    throws Exception {
    ArrayList<String> names = rdata.getParamStringList("tname");
    if (names.size() == 0) {
      addError(rdata, StringCache.getHtml("noSelection"));
      return openEditTimerTasks();
    }
    if (names.size() > 1) {
      addError(rdata, StringCache.getHtml("singleSelection"));
      return openEditTimerTasks();
    }
    String name = names.get(0);
    TimerTaskData data = TimerCache.getInstance().getTaskCopy(name);
    sdata.setParam("timerTaskData", data);
    return showEdit();
  }

  public Response saveTimerTask(RequestData rdata, SessionData sdata)
    throws Exception {
    TimerTaskData data = (TimerTaskData) sdata.getParam("timerTaskData");
    if (data == null)
      return noData(rdata, MasterResponse.TYPE_ADMIN);
    if (!readTimerTaskRequestData(data, rdata))
      return showEdit();
    TimerBean ts = TimerBean.getInstance();
    ts.updateTaskData(data);
    TimerCache.getInstance().replaceTask(data);
    rdata.setMessageKey("taskSaved");
    return openEditTimerTasks();
  }

  public boolean readTimerTaskRequestData(TimerTaskData data, RequestData rdata) {
    data.setIntervalType(rdata.getParamInt("intervalType"));
    data.setDay(rdata.getParamInt("month"));
    data.setHour(rdata.getParamInt("hour"));
    data.setMinute(rdata.getParamInt("minute"));
    data.setSecond(rdata.getParamInt("second"));
    data.setActive(rdata.getParamBoolean("active"));
    if (!data.isComplete()) {
      RequestError err = new RequestError();
      err.addErrorString(StringCache.getHtml("notComplete"));
      rdata.setError(err);
      return false;
    }
    return true;
  }

}
