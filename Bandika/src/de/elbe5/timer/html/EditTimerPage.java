package de.elbe5.timer.html;

import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.timer.TimerInterval;
import de.elbe5.timer.TimerTaskData;

public class EditTimerPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        TimerTaskData data = (TimerTaskData) rdata.getSessionObject("timerTaskData");
        String url = "/ctrl/timer/saveTimerTask";
        appendModalStart(getString("_taskSettings"));
        appendFormStart(sb, url, "taskform");
        appendModalBodyStart();
        appendHiddenField(sb, "timerName", data.getName());
        appendTextLine(sb, getString("_name"), data.getName());
        appendTextInputLine(sb, "displayName", getString("_displayName"), data.getDisplayName());
        appendLineStart(sb, "", getString("_intervalType"), true);
        appendRadio(sb, "interval", getString("_continous"), TimerInterval.CONTINOUS.name(), data.getInterval() == TimerInterval.CONTINOUS);
        appendRadio(sb, "interval", getString("_monthly"), TimerInterval.MONTH.name(), data.getInterval() == TimerInterval.MONTH);
        appendRadio(sb, "interval", getString("_daily"), TimerInterval.DAY.name(), data.getInterval() == TimerInterval.DAY);
        appendRadio(sb, "interval", getString("_everyHour"), TimerInterval.HOUR.name(), data.getInterval() == TimerInterval.HOUR);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("day"), "day", getString("_day"), true, Integer.toString(data.getDay()));
        appendTextInputLine(sb, rdata.hasFormErrorField("hour"), "hour", getString("_hour"), true, Integer.toString(data.getHour()));
        appendTextInputLine(sb, rdata.hasFormErrorField("minute"), "minute", getString("_minute"), true, Integer.toString(data.getMinute()));

        appendLineStart(sb, "", getString("_active"), true);
        appendCheckbox(sb, "active", "", "true", data.isActive());
        appendLineEnd(sb);
        appendModalFooter(getString("_close"), getString("_save"));
        appendFormEnd(sb, url, "taskform", false, true, "");
        appendModalEnd();
    }
}
