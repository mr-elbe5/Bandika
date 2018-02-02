/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.base.data.BaseData;
import de.bandika.webbase.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Class TimerTaskData is the data class for timer tasks groups. <br>
 * Usage:
 */
public abstract class TimerTask extends BaseData implements Cloneable {


    protected String displayName;
    protected TimerInterval interval = TimerInterval.CONTINOUS;
    protected int day = 0;
    protected int hour = 0;
    protected int minute = 0;
    protected boolean registerExecution = false;
    protected LocalDateTime lastExecution = null;
    protected boolean active = true;
    protected LocalDateTime nextExecution = null;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public abstract String getName();

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public TimerInterval getInterval() {
        return interval;
    }

    public void setInterval(TimerInterval interval) {
        this.interval = interval;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean registerExecution() {
        return registerExecution;
    }

    public void setRegisterExecution(boolean registerExecution) {
        this.registerExecution = registerExecution;
    }

    public LocalDateTime getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(LocalDateTime lastExecution) {
        if (lastExecution == null) {
            this.lastExecution = TimerBean.getInstance().getServerTime();
        } else {
            this.lastExecution = lastExecution;
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected LocalDateTime computeNextExecution(LocalDateTime now) {
        LocalDateTime next;
        if (lastExecution==null)
            lastExecution=LocalDateTime.now();
        switch (interval) {
            case CONTINOUS: {
                next=lastExecution.plusDays(getDay());
                next=next.plusHours(getHour());
                next=next.plusMinutes(getMinute());
                return next;
            }
            case MONTH: {
                next=now.withDayOfMonth(getDay());
                next=next.withHour(getHour());
                next=next.withMinute(getMinute());
                next=next.withSecond(0);
                if (next.isAfter(now)) {
                    next=next.minusMonths(1);
                }
                if (!(lastExecution.isBefore(next))) {
                    next=next.plusMonths(1);
                }
                return next;
            }
            case DAY: {
                next=now.withHour(getHour());
                next=next.withMinute(getMinute());
                next=next.withSecond(0);
                if (next.isAfter(now)) {
                    next=next.minusDays(1);
                }
                if (!(lastExecution.isBefore(next))) {
                    next=next.plusDays(1);
                }
                return next;
            }
            case HOUR: {
                next=now.withMinute(getMinute());
                next=next.withSecond(0);
                if (next.isAfter(now)) {
                    next=next.minusHours(1);
                }
                if (!(lastExecution.isBefore(next))) {
                    next=next.plusHours(1);
                }
                return next;
            }
        }
        return LocalDateTime.now();
    }

    public LocalDateTime getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(LocalDateTime nextExecution) {
        this.nextExecution = nextExecution;
    }

    public boolean execute(LocalDateTime now) {
        if (execute(nextExecution, now)) {
            setLastExecution(nextExecution);
            setNextExecution(computeNextExecution(now));
            if (registerExecution()) {
                TimerBean.getInstance().updateExcecutionDate(this);
            }
        }
        return true;
    }

    public void readTimerTaskRequestData(HttpServletRequest request) {
        setDisplayName(RequestReader.getString(request, "displayName"));
        setInterval(TimerInterval.valueOf(RequestReader.getString(request, "interval")));
        setDay(RequestReader.getInt(request, "day"));
        setHour(RequestReader.getInt(request, "hour"));
        setMinute(RequestReader.getInt(request, "minute"));
        setActive(RequestReader.getBoolean(request, "active"));
        setRegisterExecution(RequestReader.getBoolean(request, "registerExecution"));
    }

    public abstract boolean execute(LocalDateTime executionTime, LocalDateTime checkTime);

    @Override
    public boolean isComplete() {
        return interval==TimerInterval.CONTINOUS || (day>0 && (hour >= 0 && hour < 24) && (minute >= 0 && minute < 60));
    }
}
