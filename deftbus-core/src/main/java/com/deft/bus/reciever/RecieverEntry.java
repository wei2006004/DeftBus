package com.deft.bus.reciever;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/9/30.
 */
public interface RecieverEntry<T> {
    boolean isAvailable();

    T getReciever();

    boolean supportAction(String actions);

    void handleSignal(Method method, Object[] args) throws Exception;
}
