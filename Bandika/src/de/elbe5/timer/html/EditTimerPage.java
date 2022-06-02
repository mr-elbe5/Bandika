package de.elbe5.timer.html;

import de.elbe5.response.IFormBuilder;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.timer.TimerInterval;
import de.elbe5.timer.TimerTaskData;

import java.util.Map;

public class EditTimerPage extends ModalPage implements IFormBuilder {

    @Override
    public void appendHtml(RequestData rdata) {
        TimerTaskData data = (TimerTaskData) rdata.getSessionObject("timerTaskData");
        String url = "/ctrl/timer/saveTimerTask";
        appendModalStart(getHtml("_taskSettings"));
        appendFormStart(sb, url, "taskform");
        appendModalBodyStart();
        appendHiddenField(sb, "timerName", data.getName());
        appendTextLine(sb, getHtml("_name"), toHtml(data.getName()));
        appendTextInputLine(sb, "displayName", getHtml("_displayName"), toHtml(data.getDisplayName()));
        appendLineStart(sb, "", getHtml("_intervalType"), true);
        appendRadio(sb, "interval", getHtml("_continous"), TimerInterval.CONTINOUS.name(), data.getInterval() == TimerInterval.CONTINOUS);
        appendRadio(sb, "interval", getHtml("_monthly"), TimerInterval.MONTH.name(), data.getInterval() == TimerInterval.MONTH);
        appendRadio(sb, "interval", getHtml("_daily"), TimerInterval.DAY.name(), data.getInterval() == TimerInterval.DAY);
        appendRadio(sb, "interval", getHtml("_everyHour"), TimerInterval.HOUR.name(), data.getInterval() == TimerInterval.HOUR);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("day"), "day", getHtml("_day"), true, Integer.toString(data.getDay()));
        appendTextInputLine(sb, rdata.hasFormErrorField("hour"), "hour", getHtml("_hour"), true, Integer.toString(data.getHour()));
        appendTextInputLine(sb, rdata.hasFormErrorField("minute"), "minute", getHtml("_minute"), true, Integer.toString(data.getMinute()));

        appendLineStart(sb, "", getHtml("_active"), true);
        appendCheckbox(sb, "active", "", "true", data.isActive());
        appendLineEnd(sb);
        appendModalFooter(getHtml("_close"), getHtml("_save"));
        appendFormEnd(sb, url, "taskform", false, true, "");
        appendModalEnd();
    }
}
