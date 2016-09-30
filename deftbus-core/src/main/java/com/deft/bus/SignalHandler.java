package com.deft.bus;

import com.deft.bus.reciever.ActionRecieverEntry;
import com.deft.bus.reciever.GlobalRecieverEntry;
import com.deft.bus.reciever.RecieverEntry;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2016/9/30.
 */
class SignalHandler {

    private Map<Class, List<RecieverEntry>> mMap = new ConcurrentHashMap<>();

    static void unInst() {
        if (INSTANCE != null) {
            synchronized (mLock) {
                INSTANCE = null;
            }
        }
    }

    private static SignalHandler INSTANCE;
    private final static Object mLock = new Object();

    static SignalHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (mLock) {
                if (INSTANCE == null) {
                    INSTANCE = new SignalHandler();
                }
            }
        }
        return INSTANCE;
    }

    <T> void registerReciever(T recieverObject, Class<T> recieverClass, String[] actions) {
        clearNotAvailableRecieverEntry();
        if (!mMap.containsKey(recieverClass)){
            mMap.put(recieverClass, Collections.synchronizedList(new LinkedList<>()));
        }
        List<RecieverEntry> entries = mMap.get(recieverClass);
        if (actions == null || actions.length == 0){
            entries.clear();
            entries.add(new GlobalRecieverEntry<>(recieverObject));
            return;
        }
        if (entries.get(0) instanceof GlobalRecieverEntry){
            return;
        }
        RecieverEntry<T> recieverEntry = null;
        for (RecieverEntry<T> entry : entries) {
            T value = entry.getReciever();
            if (value != null && value == recieverObject){
                recieverEntry = entry;
                break;
            }
        }
        if (recieverEntry == null){
            entries.add(0, new ActionRecieverEntry<>(recieverObject, actions));
        } else {
            if (recieverEntry instanceof ActionRecieverEntry){
                ((ActionRecieverEntry)recieverEntry).addActions(actions);
            }
        }
    }

    <T> void unregisterReciever(T recieverObject, Class<T> recieverClass) {
        if (!mMap.containsKey(recieverClass)){
            return;
        }
        List<RecieverEntry> entries = mMap.get(recieverClass);
        for (RecieverEntry<T> entry : entries) {
            T value = entry.getReciever();
            if (value != null && value == recieverObject){
                entries.remove(entry);
                break;
            }
        }
        clearNotAvailableRecieverEntry();
    }

    private void clearNotAvailableRecieverEntry() {
        for(Map.Entry<Class, List<RecieverEntry>> classEntry : mMap.entrySet()){
            List<RecieverEntry> entries = classEntry.getValue();
            entries.stream().filter(entry -> !entry.isAvailable()).forEach(entries::remove);
            if (entries.isEmpty()){
                mMap.remove(classEntry.getKey());
            }
        }
    }

    <T> void handleSignal(Class<T> recieverClass, String action, Method method, Object[] args) throws Throwable{
        if (!mMap.containsKey(recieverClass)){
            return;
        }
        List<RecieverEntry> entries = mMap.get(recieverClass);
        for (RecieverEntry entry : entries){
            if (entry.supportAction(action)){
                entry.handleSignal(method, args);
            }
        }
    }
}
