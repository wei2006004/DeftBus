package com.deft.bus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 2016/9/29.
 */
public class SenderCreator {

    public static <T> T createSender(Object registerObject, Class<T> recieverClass) {
        return createSenderByAction(recieverClass, registerObject.getClass().getName());
    }

    public static <T> T createSender(Class<T> recieverClass) {
        return createSenderByAction(recieverClass, (String) null);
    }

    public static <T> T createSenderByClass(Class registerClass, Class<T> recieverClass) {
        return createSenderByAction(recieverClass, recieverClass.getName());
    }

    public static <T> T createSenderByAction(Class<T> recieverClass, String registerAction) {
        assertRecieverClass(recieverClass);
        return createSenderProxy(recieverClass, registerAction);
    }

    @SuppressWarnings("unchecked")
    private static <T> T createSenderProxy(Class<T> recieverClass, String registerAction) {
        return (T) Proxy.newProxyInstance(
                recieverClass.getClassLoader(),
                new Class[]{recieverClass},
                new SenderProxyInvocationHandler(recieverClass, registerAction));
    }

    private static void assertRecieverClass(Class recieverClass) {
        if (!recieverClass.isInterface()) {
            throw new IllegalArgumentException("RecieverClass must be interface. Error recieverClass:" + recieverClass);
        }
    }

    private static class SenderProxyInvocationHandler implements InvocationHandler {
        private Class mClass;
        private String mAction;

        SenderProxyInvocationHandler(Class recieverClass, String registerAction) {
            mAction = registerAction;
            mClass = recieverClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getReturnType() != void.class) {
                throw new IllegalStateException("Reciever inteface method must return void. inteface: "
                        + method.getDeclaringClass()
                        + ", method: " + method.getName()
                        + ", retrunType: " + method.getReturnType());
            }
            SignalHandler.getInstance().handleSignal(mClass, mAction, method, args);
            return null;
        }
    }
}
