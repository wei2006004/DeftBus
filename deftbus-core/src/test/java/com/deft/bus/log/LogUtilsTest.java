package com.deft.bus.log;

import org.junit.Test;

import static com.deft.bus.log.LogUtils.log;

/**
 * Created by Administrator on 2016/10/8.
 */
public class LogUtilsTest {

    @Test
    public void logtest() throws Exception {
        log("before");
        log("LogUtilsTest", "before");
        LogUtils.setDebug(true);
        log("middle");
        log("LogUtilsTest", "middle");
        log(this, "middle");
        LogUtils.setDebug(false);
        log("after");
        log("LogUtilsTest", "after");
    }
}