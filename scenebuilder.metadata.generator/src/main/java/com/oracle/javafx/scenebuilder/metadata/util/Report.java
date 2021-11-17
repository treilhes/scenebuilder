package com.oracle.javafx.scenebuilder.metadata.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Report {

    private static Logger logger = LoggerFactory.getLogger(Report.class);

    private static List<Log> logs = new ArrayList<>();

    private enum InternalLevel {
        Error, Info, Warn
    }

    private static class Log {
        private InternalLevel level;
        private String cls;
        private String message;
        private Throwable throwable;

        public Log(InternalLevel level, String cls, String message, Throwable throwable) {
            super();
            this.level = level;
            this.cls = cls;
            this.message = message;
            this.throwable = throwable;
        }
    }

    public static void error(Class<?> cls, String message) {
        logs.add(new Log(InternalLevel.Error, cls.getName(), message, null));
    }

    public static void error(String message, Throwable throwable) {
        logs.add(new Log(InternalLevel.Error, (String) null, message, null));
    }

    public static void error(Class<?> cls, String message, Throwable throwable) {
        logs.add(new Log(InternalLevel.Error, cls.getName(), message, throwable));
    }

    public static void error(String cls, String message, Throwable throwable) {
        logs.add(new Log(InternalLevel.Error, cls, message, throwable));
    }

    public static void info(Class<?> cls, String message) {
        logs.add(new Log(InternalLevel.Info, cls.getName(), message, null));
    }

    public static void warn(Class<?> cls, String message) {
        logs.add(new Log(InternalLevel.Warn, cls.getName(), message, null));
    }

    public static void warn(Class<?> cls, String message, Throwable throwable) {
        logs.add(new Log(InternalLevel.Warn, cls.getName(), message, throwable));
    }

    public static boolean flush(boolean debug) {
        boolean hasError = false;
        List<Log> l = new ArrayList<>(logs);
        logs.clear();
        for (Log log : l) {
            String cls = log.cls == null ? "NONE" : log.cls;
            switch (log.level) {
            case Error:
                if (debug) {
                    logger.error("{} : {}" ,cls, log.message, log.throwable);
                } else {
                    String error = log.throwable != null
                            ? log.throwable.getMessage() != null ? log.throwable.getMessage()
                                    : log.throwable.getCause().getMessage()
                            : null;
                    logger.error("{} : {}{}", cls, log.message, (error != null ? " : " + error : ""));
                }

                hasError = true;
                break;
            case Warn:
                if (debug) {
                    logger.warn("{} : {}" ,cls, log.message, log.throwable);
                } else {
                    String error = log.throwable != null
                            ? log.throwable.getMessage() != null ? log.throwable.getMessage()
                                    : log.throwable.getCause().getMessage()
                            : null;
                    logger.warn("{} : {}{}", cls, log.message, (error != null ? " : " + error : ""));
                }
                break;
            default:
                logger.info("{} : {}" ,cls, log.message);
                break;
            }
        }

        return hasError;
    }
}
