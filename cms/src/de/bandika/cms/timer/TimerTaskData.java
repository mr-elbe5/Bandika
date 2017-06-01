/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.timer;

import de.bandika.base.data.BaseIdData;
import de.bandika.base.log.Log;
import de.bandika.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Class TimerTaskData is the data class for timer tasks groups. <br>
 * Usage:
 */
public class TimerTaskData extends BaseIdData implements Cloneable {

    public static final int INTERVAL_TYPE_CONTINOUS = 0;
    public static final int INTERVAL_TYPE_MONTH = 1;
    public static final int INTERVAL_TYPE_DAY = 2;
    public static final int INTERVAL_TYPE_HOUR = 3;

    protected String name;
    protected String className;
    protected int intervalType = INTERVAL_TYPE_DAY;
    protected int day = 0;
    protected int hour = 0;
    protected int minute = 0;
    protected int second = 0;
    protected boolean noteExecution = false;
    protected LocalDateTime lastExecution = null;
    protected boolean active = true;
    protected TimerTask task = null;
    protected LocalDateTime nextExecution = null;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(int intervalType) {
        this.intervalType = intervalType;
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

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public boolean noteExecution() {
        return noteExecution;
    }

    public void setNoteExecution(boolean noteExecution) {
        this.noteExecution = noteExecution;
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

    public boolean initialize(LocalDateTime now) {
        try {
            Class cls = Object.class;
            if (className != null && !className.isEmpty()) {
                try {
                    cls = Class.forName(className);
                } catch (Exception e) {
                    Log.warn("could not load class " + className + "for task " + getName());
                }
            }
            task = (TimerTask) cls.newInstance();
            setNextExecution(computeNextExecution(now));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected LocalDateTime computeNextExecution(LocalDateTime now) {
        LocalDateTime next;
        switch (intervalType) {
            case INTERVAL_TYPE_CONTINOUS: {
                next=lastExecution.plusDays(getDay());
                next=next.plusHours(getHour());
                next=next.plusMinutes(getMinute());
                next=next.plusSeconds(getSecond());
                return next;
            }
            case INTERVAL_TYPE_MONTH: {
                next=now;
                next=next.withDayOfMonth(getDay());
                next=next.withHour(getHour());
                next=next.withMinute(getMinute());
                next=next.withSecond(getSecond());
                if (next.isAfter(now)) {
                    next=next.minusMonths(1);
                }
                if (!(lastExecution.isBefore(next))) {
                    next=next.plusMonths(1);
                }
                return next;
            }
            case INTERVAL_TYPE_DAY: {
                next=now;
                next=next.withHour(getHour());
                next=next.withMinute(getMinute());
                next=next.withSecond(getSecond());
                if (next.isAfter(now)) {
                    next=next.minusDays(1);
                }
                if (!(lastExecution.isBefore(next))) {
                    next=next.plusDays(1);
                }
                return next;
            }
            case INTERVAL_TYPE_HOUR: {
                next=now;
                next=next.withMinute(getMinute());
                next=next.withSecond(getSecond());
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
        if (task != null) {
            if (task.execute(nextExecution, now)) {
                setLastExecution(nextExecution);
                setNextExecution(computeNextExecution(now));
                if (noteExecution()) {
                    TimerBean.getInstance().updateExcecutionDate(this);
                }
            }
            return true;
        }
        return false;
    }

    public void readTimerTaskRequestData(HttpServletRequest request) {
        setIntervalType(RequestReader.getInt(request, "intervalType"));
        setDay(RequestReader.getInt(request, "month"));
        setHour(RequestReader.getInt(request, "hour"));
        setMinute(RequestReader.getInt(request, "minute"));
        setSecond(RequestReader.getInt(request, "second"));
        setActive(RequestReader.getBoolean(request, "active"));
        setNoteExecution(RequestReader.getBoolean(request, "noteExecution"));
    }

    @Override
    public boolean isComplete() {
        return (intervalType >= INTERVAL_TYPE_CONTINOUS && intervalType <= INTERVAL_TYPE_HOUR) && (day >= 1 || intervalType != INTERVAL_TYPE_MONTH) && (hour >= 0 && hour <= 24) && (minute >= 0 && minute <= 60) && (second >= 0 && second <= 60);
    }
}
