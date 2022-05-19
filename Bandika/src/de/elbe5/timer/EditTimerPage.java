package de.elbe5.timer;

import de.elbe5.base.Strings;
import de.elbe5.html.Form;
import de.elbe5.html.ModalPage;
import de.elbe5.request.RequestData;

public class EditTimerPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        TimerTaskData data = (TimerTaskData) rdata.getSessionObject("timerTaskData");
        String url = "/ctrl/timer/saveTimerTask";
        appendModalStart(sb, Strings.getHtml("_taskSettings"));
        Form.appendFormStart(sb, url, "taskform");
        appendModalBodyStart(sb);
        sb.append(Strings.format("""
                        <input type="hidden" name="timerName" value="{1}>"/>
                        """,
                data.getName()
        ));
        Form.appendTextLine(sb, Strings.getHtml("_name"), Strings.toHtml(data.getName()));
        Form.appendTextInputLine(sb, "displayName", Strings.getHtml("_displayName"), Strings.toHtml(data.getDisplayName()));
        Form.appendLineStart(sb, "", Strings.getHtml("_intervalType"), true);
        Form.appendRadio(sb, "interval", Strings.getHtml("_continous"), TimerInterval.CONTINOUS.name(), data.getInterval() == TimerInterval.CONTINOUS);
        Form.appendRadio(sb, "interval", Strings.getHtml("_monthly"), TimerInterval.MONTH.name(), data.getInterval() == TimerInterval.MONTH);
        Form.appendRadio(sb, "interval", Strings.getHtml("_daily"), TimerInterval.DAY.name(), data.getInterval() == TimerInterval.DAY);
        Form.appendRadio(sb, "interval", Strings.getHtml("_everyHour"), TimerInterval.HOUR.name(), data.getInterval() == TimerInterval.HOUR);
        Form.appendLineEnd(sb);
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("day"), "day", Strings.getHtml("_day"), true, Integer.toString(data.getDay()));
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("hour"), "hour", Strings.getHtml("_hour"), true, Integer.toString(data.getHour()));
        Form.appendTextInputLine(sb, rdata.hasFormErrorField("minute"), "minute", Strings.getHtml("_minute"), true, Integer.toString(data.getMinute()));

        Form.appendLineStart(sb, "", Strings.getHtml("_active"), true);
        Form.appendCheckbox(sb, "active", "", "true", data.isActive());
        Form.appendLineEnd(sb);
        appendModalFooter(sb, Strings.getHtml("_close"), Strings.getHtml("_save"));
        Form.appendFormEnd(sb, url, "taskform", false, true, "");
        appendModalEnd(sb);
    }
}
