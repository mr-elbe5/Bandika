package de.elbe5.timer.html;

import de.elbe5.base.Strings;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;
import de.elbe5.timer.TimerInterval;
import de.elbe5.timer.TimerTaskData;

public class EditTimerPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        TimerTaskData data = (TimerTaskData) rdata.getSessionObject("timerTaskData");
        String url = "/ctrl/timer/saveTimerTask";
        appendModalStart(Strings.getHtml("_taskSettings"));
        appendFormStart(sb, url, "taskform");
        appendModalBodyStart();
        append("""
                        <input type="hidden" name="timerName" value="{1}>"/>
                        """,
                data.getName()
        );
        appendTextLine(sb, Strings.getHtml("_name"), Strings.toHtml(data.getName()));
        appendTextInputLine(sb, "displayName", Strings.getHtml("_displayName"), Strings.toHtml(data.getDisplayName()));
        appendLineStart(sb, "", Strings.getHtml("_intervalType"), true);
        appendRadio(sb, "interval", Strings.getHtml("_continous"), TimerInterval.CONTINOUS.name(), data.getInterval() == TimerInterval.CONTINOUS);
        appendRadio(sb, "interval", Strings.getHtml("_monthly"), TimerInterval.MONTH.name(), data.getInterval() == TimerInterval.MONTH);
        appendRadio(sb, "interval", Strings.getHtml("_daily"), TimerInterval.DAY.name(), data.getInterval() == TimerInterval.DAY);
        appendRadio(sb, "interval", Strings.getHtml("_everyHour"), TimerInterval.HOUR.name(), data.getInterval() == TimerInterval.HOUR);
        appendLineEnd(sb);
        appendTextInputLine(sb, rdata.hasFormErrorField("day"), "day", Strings.getHtml("_day"), true, Integer.toString(data.getDay()));
        appendTextInputLine(sb, rdata.hasFormErrorField("hour"), "hour", Strings.getHtml("_hour"), true, Integer.toString(data.getHour()));
        appendTextInputLine(sb, rdata.hasFormErrorField("minute"), "minute", Strings.getHtml("_minute"), true, Integer.toString(data.getMinute()));

        appendLineStart(sb, "", Strings.getHtml("_active"), true);
        appendCheckbox(sb, "active", "", "true", data.isActive());
        appendLineEnd(sb);
        appendModalFooter(Strings.getHtml("_close"), Strings.getHtml("_save"));
        appendFormEnd(sb, url, "taskform", false, true, "");
        appendModalEnd();
    }
}
