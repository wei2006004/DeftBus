package com.deft.bus.reciever;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/9/30.
 */
public abstract class WeakReferenceRecieverEntry<T> implements RecieverEntry<T>{
    private WeakReference<T> mReciever;

    public WeakReferenceRecieverEntry(T reciever){
        mReciever = new WeakReference<>(reciever);
    }

    @Override
    public boolean isAvailable() {
        return mReciever.get() != null;
    }

    @Override
    public T getReciever() {
        return mReciever.get();
    }

    @Override
    public void handleSignal(Method method, Object[] args) throws Throwable {
        T reciever = mReciever.get();
        if (reciever != null){
            method.invoke(reciever, args);
        }
    }
}
