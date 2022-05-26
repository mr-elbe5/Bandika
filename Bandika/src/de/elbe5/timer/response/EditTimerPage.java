package de.elbe5.timer.response;

import de.elbe5.base.Strings;
import de.elbe5.response.FormHtml;
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
        FormHtml.appendFormStart(sb, url, "taskform");
        appendModalBodyStart();
        append("""
                        <input type="hidden" name="timerName" value="{1}>"/>
                        """,
                data.getName()
        );
        FormHtml.appendTextLine(sb, Strings.getHtml("_name"), Strings.toHtml(data.getName()));
        FormHtml.appendTextInputLine(sb, "displayName", Strings.getHtml("_displayName"), Strings.toHtml(data.getDisplayName()));
        FormHtml.appendLineStart(sb, "", Strings.getHtml("_intervalType"), true);
        FormHtml.appendRadio(sb, "interval", Strings.getHtml("_continous"), TimerInterval.CONTINOUS.name(), data.getInterval() == TimerInterval.CONTINOUS);
        FormHtml.appendRadio(sb, "interval", Strings.getHtml("_monthly"), TimerInterval.MONTH.name(), data.getInterval() == TimerInterval.MONTH);
        FormHtml.appendRadio(sb, "interval", Strings.getHtml("_daily"), TimerInterval.DAY.name(), data.getInterval() == TimerInterval.DAY);
        FormHtml.appendRadio(sb, "interval", Strings.getHtml("_everyHour"), TimerInterval.HOUR.name(), data.getInterval() == TimerInterval.HOUR);
        FormHtml.appendLineEnd(sb);
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("day"), "day", Strings.getHtml("_day"), true, Integer.toString(data.getDay()));
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("hour"), "hour", Strings.getHtml("_hour"), true, Integer.toString(data.getHour()));
        FormHtml.appendTextInputLine(sb, rdata.hasFormErrorField("minute"), "minute", Strings.getHtml("_minute"), true, Integer.toString(data.getMinute()));

        FormHtml.appendLineStart(sb, "", Strings.getHtml("_active"), true);
        FormHtml.appendCheckbox(sb, "active", "", "true", data.isActive());
        FormHtml.appendLineEnd(sb);
        appendModalFooter(Strings.getHtml("_close"), Strings.getHtml("_save"));
        FormHtml.appendFormEnd(sb, url, "taskform", false, true, "");
        appendModalEnd();
    }
}
