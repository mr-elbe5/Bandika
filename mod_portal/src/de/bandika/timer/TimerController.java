/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.timer;

import de.bandika.application.AppConfiguration;
import de.bandika.application.GeneralRightsData;
import de.bandika.application.GeneralRightsProvider;
import de.bandika.data.Log;
import de.bandika.data.StringCache;
import de.bandika.servlet.*;
import de.bandika.user.UserController;

import java.util.List;

public class TimerController extends Controller {

    public static final int LINKID_TIMERS = 106;

    private static TimerController instance = null;

    public static void setInstance(TimerController instance) {
        TimerController.instance = instance;
    }

    public static TimerController getInstance() {
        if (instance == null) {
            instance = new TimerController();
        }
        return instance;
    }

    protected TimerThread timerThread = null;

    public String getKey(){
        return "timer";
    }

    public void initialize() {
        startThread();
    }

    public void startThread() {
        int interval = AppConfiguration.getInstance().getInt("timerInterval");
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
        if (timerThread != null)
            timerThread.stopRunning();
        timerThread = null;
        int interval = AppConfiguration.getInstance().getInt("timerInterval");
        timerThread = new TimerThread(interval);
        timerThread.startRunning();
        AppContextListener.registerThread(timerThread);
    }

    public Response doAction(String action, RequestData rdata, SessionData sdata)
            throws Exception {
        if (!sdata.isLoggedIn())
            return UserController.getInstance().openLogin();
        if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL, GeneralRightsData.RIGHT_APPLICATION_ADMIN)) {
            if (action.equals("openEditTimerTasks")) return openEditTimerTasks(sdata);
            if (action.equals("openEditTimerTask")) return openEditTimerTask(rdata, sdata);
            if (action.equals("saveTimerTask")) return saveTimerTask(rdata, sdata);
        }
        return noAction(rdata, sdata, MasterResponse.TYPE_USER);
    }

    protected Response showEditAll(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/timer/editAllTimerTasks.jsp", StringCache.getString("portal_timers", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    protected Response showEdit(SessionData sdata) {
        return new JspResponse("/WEB-INF/_jsp/timer/editTimerTask.jsp", StringCache.getString("portal_timer", sdata.getLocale()), MasterResponse.TYPE_ADMIN);
    }

    public Response openEditTimerTasks(SessionData sdata) throws Exception {
        return showEditAll(sdata);
    }

    public Response openEditTimerTask(RequestData rdata, SessionData sdata)
            throws Exception {
        List<String> names = rdata.getStringList("tname");
        if (names.size() == 0) {
            addError(rdata, StringCache.getHtml("webapp_noSelection",sdata.getLocale()));
            return openEditTimerTasks(sdata);
        }
        if (names.size() > 1) {
            addError(rdata, StringCache.getHtml("webapp_singleSelection",sdata.getLocale()));
            return openEditTimerTasks(sdata);
        }
        String name = names.get(0);
        TimerTaskData data = TimerCache.getInstance().getTaskCopy(name);
        sdata.put("timerTaskData", data);
        return showEdit(sdata);
    }

    public Response saveTimerTask(RequestData rdata, SessionData sdata)
            throws Exception {
        TimerTaskData data = (TimerTaskData) sdata.get("timerTaskData");
        if (data == null)
            return noData(rdata, sdata, MasterResponse.TYPE_ADMIN);
        if (!readTimerTaskRequestData(data, rdata, sdata))
            return showEdit(sdata);
        TimerBean ts = TimerBean.getInstance();
        ts.updateTaskData(data);
        TimerCache.getInstance().replaceTask(data);
        rdata.setMessageKey("portal_taskSaved", sdata.getLocale());
        return openEditTimerTasks(sdata);
    }

    public boolean readTimerTaskRequestData(TimerTaskData data, RequestData rdata, SessionData sdata) {
        data.setIntervalType(rdata.getInt("intervalType"));
        data.setDay(rdata.getInt("month"));
        data.setHour(rdata.getInt("hour"));
        data.setMinute(rdata.getInt("minute"));
        data.setSecond(rdata.getInt("second"));
        data.setActive(rdata.getBoolean("active"));
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
            return false;
        }
        return true;
    }

}
