package com.deft.bus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/29.
 */
public class DBus {

    private DBus() {
    }

    public static <T> void register(T recieverObject, Class<T> recieverClass) {
        innerRegister(recieverObject, recieverClass, null);
    }

    public static <T> void register(T recieverObject, Class<T> recieverClass, Class[] senders, String[] actions) {
        if (senders == null || senders.length == 0) {
            innerRegister(recieverObject, recieverClass, actions);
        }
        List list = new ArrayList<String>();
        for (Class clazz : senders) {
            list.add(clazz.getName());
        }
        if (actions != null) {
            for (String action : actions) {
                list.add(action);
            }
        }
        innerRegister(recieverObject, recieverClass, list.isEmpty() ? null : (String[]) list.toArray());
    }

    private static <T> void innerRegister(T recieverObject, Class<T> recieverClass, String[] actions) {
        assertRecieverClass(recieverClass);
        SignalHandler.getInstance().registerReciever(recieverObject, recieverClass, actions);
    }

    public static <T> void unregister(T recieverObject, Class<T> recieverClass) {
        assertRecieverClass(recieverClass);
        SignalHandler.getInstance().unregisterReciever(recieverObject, recieverClass);
    }

    private static void assertRecieverClass(Class recieverClass) {
        if (!recieverClass.isInterface()) {
            throw new IllegalArgumentException("RecieverClass must be interface. Error recieverClass:" + recieverClass);
        }
    }

    public static void unInst(){
        SignalHandler.unInst();
    }
}
