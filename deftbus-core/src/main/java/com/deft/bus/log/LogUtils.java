package com.deft.bus.log;

/**
 * Created by Administrator on 2016/10/8.
 */
public class LogUtils {
    public static Boolean DEBUG = false;
    private static Logger mLogger = new DefaultLogger();

    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static void setLogger(Logger logger) {
        mLogger = logger;
    }

    public static void log(String text) {
        if (DEBUG) {
            mLogger.log(text);
        }
    }

    public static void log(String tag, String text) {
        if (DEBUG) {
            mLogger.log(tag, text);
        }
    }

    public static void log(Object tagObject, String text) {
        if (DEBUG) {
            mLogger.log(tagObject.getClass().getSimpleName(), text);
        }
    }

    private static class DefaultLogger implements Logger {

        @Override
        public void log(String text) {
            System.out.println(text);
        }

        @Override
        public void log(String tag, String text) {
            System.out.println(tag + ": " + text);
        }
    }
}
