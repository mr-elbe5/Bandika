package de.elbe5.companion;

import de.elbe5.data.LocalizedStrings;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public interface DateCompanion {

    default @NotNull Date asDate(@NotNull LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    default @NotNull Date asDate(@NotNull LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    default @NotNull LocalDate asLocalDate(@NotNull Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    default @NotNull LocalDateTime asLocalDateTime(@NotNull Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    default long asMillis(@NotNull LocalDate localDate) {
        return asDate(localDate).getTime();
    }

    default long asMillis(@NotNull LocalDateTime localDateTime) {
        return asDate(localDateTime).getTime();
    }

    default @NotNull LocalDate asLocalDate(long millis) {
        if (millis==0)
            return LocalDate.MIN;
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    default @NotNull LocalDateTime asLocalDateTime(long millis) {
        if (millis==0)
            return LocalDateTime.MIN;
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    default @NotNull String getDatePattern(){
        return LocalizedStrings.getString("system.datePattern");
    }

    default @NotNull String getDateTimePattern(){
        return LocalizedStrings.getString("system.dateTimePattern");
    }

    default @NotNull String getTimePattern(){
        return LocalizedStrings.getString("system.timePattern");
    }

    default @NotNull String toHtmlDate(@NotNull LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(getDatePattern()));
    }

    default @NotNull String toHtmlDate(@NotNull LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(getDatePattern()));
    }

    default LocalDate fromDate(@NotNull String s) {
        if (s.isEmpty())
            return LocalDate.MIN;
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(getDatePattern()));
    }

    default @NotNull String toHtmlTime(@NotNull LocalTime date) {
        return date.format(DateTimeFormatter.ofPattern(getTimePattern()));
    }

    default @NotNull LocalTime fromTime(@NotNull String s) {
        if (s.isEmpty())
            return LocalTime.MIN;
        return LocalTime.parse(s, DateTimeFormatter.ofPattern(getTimePattern()));
    }

    default @NotNull String toHtmlDateTime(@NotNull LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(getDateTimePattern()));
    }

    default @NotNull LocalDateTime fromDateTime(@NotNull String s) {
        if (s.isEmpty())
            return LocalDateTime.MIN;
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(getDateTimePattern()));
    }
}
