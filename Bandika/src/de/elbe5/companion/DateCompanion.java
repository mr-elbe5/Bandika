package de.elbe5.companion;

import de.elbe5.data.LocalizedStrings;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public interface DateCompanion {

    default Date asDate(LocalDate localDate) {
        if (localDate==null)
            return null;
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    default Date asDate(LocalDateTime localDateTime) {
        if (localDateTime==null)
            return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    default LocalDate asLocalDate(Date date) {
        if (date==null)
            return null;
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    default LocalDateTime asLocalDateTime(Date date) {
        if (date==null)
            return null;
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    default long asMillis(LocalDate localDate) {
        if (localDate==null)
            return 0;
        return asDate(localDate).getTime();
    }

    default long asMillis(LocalDateTime localDateTime) {
        if (localDateTime==null)
            return 0;
        return asDate(localDateTime).getTime();
    }

    default LocalDate asLocalDate(long millis) {
        if (millis==0)
            return LocalDate.MIN;
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    default LocalDateTime asLocalDateTime(long millis) {
        if (millis==0)
            return LocalDateTime.MIN;
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    default String getDatePattern(){
        return LocalizedStrings.getString("system.datePattern");
    }

    default String getDateTimePattern(){
        return LocalizedStrings.getString("system.dateTimePattern");
    }

    default String getTimePattern(){
        return LocalizedStrings.getString("system.timePattern");
    }

    default String toHtmlDate(LocalDate date) {
        if (date==null)
            return "";
        return date.format(DateTimeFormatter.ofPattern(getDatePattern()));
    }

    default String toHtmlDate(LocalDateTime date) {
        if (date==null)
            return "";
        return date.format(DateTimeFormatter.ofPattern(getDatePattern()));
    }

    default LocalDate fromDate(String s) {
        if (s.isEmpty())
            return LocalDate.MIN;
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(getDatePattern()));
    }

    default String toHtmlTime(LocalTime date) {
        if (date==null)
            return "";
        return date.format(DateTimeFormatter.ofPattern(getTimePattern()));
    }

    default LocalTime fromTime(String s) {
        if (s.isEmpty())
            return LocalTime.MIN;
        return LocalTime.parse(s, DateTimeFormatter.ofPattern(getTimePattern()));
    }

    default String toHtmlDateTime(LocalDateTime date) {
        if (date==null)
            return "";
        return date.format(DateTimeFormatter.ofPattern(getDateTimePattern()));
    }

    default LocalDateTime fromDateTime(String s) {
        if (s.isEmpty())
            return LocalDateTime.MIN;
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(getDateTimePattern()));
    }
}
