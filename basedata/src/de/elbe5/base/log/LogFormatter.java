package de.elbe5.base.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        String srcClass = record.getSourceClassName();
        String srcMethod = record.getSourceMethodName();
        if (record.getLevel() == Level.INFO) {
            sb.append("INFO    ");
        } else if (record.getLevel() == Level.WARNING) {
            sb.append("WARNING ");
        } else if (record.getLevel() == Level.SEVERE) {
            sb.append("ERROR   ");
        }
        sb.append(formatter.format(new Date(record.getMillis())));
        if (srcClass.length() > 0) {
            sb.append('\n');
            sb.append(srcClass);
            if (srcMethod.length() > 0) {
                sb.append('.');
                sb.append(srcMethod);
            }
        }
        sb.append(" - ");
        sb.append(record.getMessage());
        @SuppressWarnings("ThrowableResultOfMethodCallIgnored") Throwable t = record.getThrown();
        if (t != null) {
            sb.append("\n> Exception: ");
            sb.append(t.getMessage());
            sb.append(" at:");
            for (StackTraceElement ste : t.getStackTrace()) {
                sb.append(String.format("\n> %s line %d", ste.getClassName(), ste.getLineNumber()));
            }
        }
        sb.append('\n');
        return sb.toString();
    }
}
