package com.deft.bus.reciever;

/**
 * Created by Administrator on 2016/9/30.
 */
public class GlobalRecieverEntry<T> extends WeakReferenceRecieverEntry<T>{

    public GlobalRecieverEntry(T reciever) {
        super(reciever);
    }

    @Override
    public boolean supportAction(String actions) {
        return true;
    }
}
