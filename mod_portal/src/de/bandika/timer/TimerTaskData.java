/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.timer;

import de.bandika.data.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Class TimerTaskData is the data class for timer tasks groups. <br>
 * Usage:
 */
public class TimerTaskData implements Cloneable {

    public static final int INTERVAL_TYPE_CONTINOUS = 0;
    public static final int INTERVAL_TYPE_MONTH = 1;
    public static final int INTERVAL_TYPE_WEEK = 2;
    public static final int INTERVAL_TYPE_DAY = 3;
    public static final int INTERVAL_TYPE_HOUR = 4;
    public static final int INTERVAL_TYPE_MINUTE = 5;

    protected String name;
    protected String className;
    protected int intervalType = INTERVAL_TYPE_DAY;
    protected int day = 0;
    protected int hour = 0;
    protected int minute = 0;
    protected int second = 0;
    protected boolean noteExecution = false;
    protected Date lastExecution = null;
    protected boolean active = true;

    protected TimerTask task = null;
    protected Date nextExecution = null;

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

    public Date getLastExecution() {
        return lastExecution;
    }

    public java.sql.Timestamp getSqlLastExecution() {
        return new java.sql.Timestamp(lastExecution.getTime());
    }

    public void setLastExecution(Date lastExecution) {
        if (lastExecution == null)
            this.lastExecution = TimerBean.getInstance().getServerTime();
        else
            this.lastExecution = lastExecution;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean initialize(Date now) {
        try {
            Class cls = Object.class;
            if (className != null && !className.equals("")) {
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

    protected Date computeNextExecution(Date now) {
        Calendar cal = new GregorianCalendar();
    /* cal = lastExecution | now >> lastExpected >> nextExecution  */
        switch (intervalType) {
            case INTERVAL_TYPE_CONTINOUS: {
                cal.setTime(lastExecution);
                cal.add(Calendar.DATE, getDay());
                cal.add(Calendar.HOUR, getHour());
                cal.add(Calendar.MINUTE, getMinute());
                cal.add(Calendar.SECOND, getSecond());
            }
            break;
            case INTERVAL_TYPE_MONTH: {
                cal.setTime(now);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, getDay());
                cal.set(Calendar.HOUR, getHour());
                cal.set(Calendar.MINUTE, getMinute());
                cal.set(Calendar.SECOND, getSecond());
                if (cal.getTime().after(now))
                    cal.add(Calendar.MONTH, -1);
                if (!(lastExecution.before(cal.getTime())))
                    cal.add(Calendar.MONTH, 1);
            }
            break;
            case INTERVAL_TYPE_WEEK: {
                cal.setTime(now);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.HOUR, getHour());
                cal.set(Calendar.MINUTE, getMinute());
                cal.set(Calendar.SECOND, getSecond());
                cal.set(Calendar.DAY_OF_WEEK, getDay());
                if (cal.getTime().after(now))
                    cal.add(Calendar.DATE, -7);
                if (!(lastExecution.before(cal.getTime())))
                    cal.add(Calendar.DATE, 7);
            }
            break;
            case INTERVAL_TYPE_DAY: {
                cal.setTime(now);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.HOUR, getHour());
                cal.set(Calendar.MINUTE, getMinute());
                cal.set(Calendar.SECOND, getSecond());
                if (cal.getTime().after(now))
                    cal.add(Calendar.DATE, -1);
                if (!(lastExecution.before(cal.getTime())))
                    cal.add(Calendar.DATE, 1);
            }
            break;
            case INTERVAL_TYPE_HOUR: {
                cal.setTime(now);
                cal.set(Calendar.MINUTE, getMinute());
                cal.set(Calendar.SECOND, getSecond());
                cal.set(Calendar.MILLISECOND, 0);
                if (cal.getTime().after(now))
                    cal.add(Calendar.HOUR, -1);
                if (!(lastExecution.before(cal.getTime())))
                    cal.add(Calendar.HOUR, 1);
            }
            break;
            case INTERVAL_TYPE_MINUTE: {
                cal.setTime(now);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, getSecond());
                if (cal.getTime().after(now))
                    cal.add(Calendar.MINUTE, -1);
                if (!(lastExecution.before(cal.getTime())))
                    cal.add(Calendar.MINUTE, 1);
            }
            break;
        }
        return cal.getTime();
    }

    public Date getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(Date nextExecution) {
        this.nextExecution = nextExecution;
    }

    public boolean execute(Date now) {
        if (task != null) {
            if (task.execute(nextExecution, now)) {
                setLastExecution(nextExecution);
                setNextExecution(computeNextExecution(now));
                if (noteExecution())
                    TimerBean.getInstance().updateExcecutionDate(this);
            }
            return true;
        }
        return false;
    }

    public boolean isComplete() {
        return (intervalType >= INTERVAL_TYPE_CONTINOUS && intervalType <= INTERVAL_TYPE_MINUTE) &&
                (day >= 1 || intervalType != INTERVAL_TYPE_MONTH) &&
                (hour >= 0 && hour <= 24) &&
                (minute >= 0 && minute <= 60) &&
                (second >= 0 && second <= 60);
    }

}
