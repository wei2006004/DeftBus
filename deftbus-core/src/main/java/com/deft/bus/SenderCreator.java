package com.deft.bus;

/**
 * Created by Administrator on 2016/9/29.
 */
public class SenderCreator {

    public static <T> T createSender(Object registerObject, Class<T> recieverClass) {
        return createSender(registerObject.getClass().getName(), recieverClass);
    }

    public static <T> T createSender(Class registerClass, Class<T> recieverClass) {
        return createSender(recieverClass.getName(), recieverClass);
    }

    public static <T> T createSender(Class<T> recieverClass) {
        return createSender("", recieverClass);
    }

    public static <T> T createSender(String registerAction, Class<T> recieverClass) {
        assertRecieverClass(recieverClass);
        return null;
    }

    private static void assertRecieverClass(Class recieverClass) {
        if (!recieverClass.isInterface()) {
            throw new IllegalArgumentException("RecieverClass must be interface. Error recieverClass:" + recieverClass);
        }
    }
}
