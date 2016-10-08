package com.deft.bus.log;

/**
 * Created by Administrator on 2016/10/8.
 */
public interface Logger {
    void log(String text);

    void log(String tag, String text);
}
