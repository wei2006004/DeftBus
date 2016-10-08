package com.deft.bus;

import com.deft.bus.reciever.ActionRecieverEntry;
import com.deft.bus.reciever.GlobalRecieverEntry;
import com.deft.bus.reciever.RecieverEntry;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.deft.bus.log.LogUtils.log;
import static com.deft.bus.utils.TextUtils.convertStringArrayToText;

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
        if (!mMap.containsKey(recieverClass)) {
            mMap.put(recieverClass, Collections.synchronizedList(new LinkedList<>()));
            log(this, "[registerReciever] add class( " + recieverClass + " ) to map.");
        }
        List<RecieverEntry> entries = mMap.get(recieverClass);
        RecieverEntry<T> recieverEntry = null;
        for (RecieverEntry<T> entry : entries) {
            T value = entry.getReciever();
            if (value != null && value == recieverObject) {
                recieverEntry = entry;
                break;
            }
        }
        if (actions == null || actions.length == 0) {
            if (recieverEntry != null){
                entries.remove(recieverEntry);
            }
            entries.add(new GlobalRecieverEntry<>(recieverObject));
            log(this, "[registerReciever] add global entry, class:" + recieverClass
                    + ", actions:" + convertStringArrayToText(actions)
                    + ", object:" + recieverObject);
            return;
        }
        if (recieverEntry == null) {
            entries.add(0, new ActionRecieverEntry<>(recieverObject, actions));
            log(this, "[registerReciever] add action entry, class:" + recieverClass
                    + ", actions:" + convertStringArrayToText(actions)
                    + ", object:" + recieverObject);
        } else {
            if (recieverEntry instanceof ActionRecieverEntry) {
                ((ActionRecieverEntry) recieverEntry).addActions(actions);
                log(this, "[registerReciever] add actions, class:" + recieverClass
                        + ", actions:" + convertStringArrayToText(actions)
                        + ", object:" + recieverObject);
            } else {
                log(this, "[registerReciever] add actions when global, do nothing, class:" + recieverClass
                        + ", actions:" + convertStringArrayToText(actions)
                        + ", object:" + recieverObject);
            }
        }
    }

    <T> void unregisterReciever(T recieverObject, Class<T> recieverClass) {
        if (!mMap.containsKey(recieverClass)) {
            return;
        }
        List<RecieverEntry> entries = mMap.get(recieverClass);
        for (RecieverEntry<T> entry : entries) {
            T value = entry.getReciever();
            if (value != null && value == recieverObject) {
                entries.remove(entry);
                log(this, "[unregisterReciever] remove reciver class, class:" + recieverClass
                        + ", object:" + recieverObject);
                break;
            }
        }
        clearNotAvailableRecieverEntry();
    }

    private void clearNotAvailableRecieverEntry() {
        for (Map.Entry<Class, List<RecieverEntry>> classEntry : mMap.entrySet()) {
            List<RecieverEntry> entries = classEntry.getValue();
            Iterator<RecieverEntry> iterator = entries.iterator();
            while (iterator.hasNext()){
                RecieverEntry entry = iterator.next();
                if (!entry.isAvailable()){
                    iterator.remove();
                }
            }
            if (entries.isEmpty()) {
                mMap.remove(classEntry.getKey());
                log(this, "[clearNotAvailableRecieverEntry] remove reciver class, class:" + classEntry.getKey());
            }
        }
    }

    <T> void handleSignal(Class<T> recieverClass, String action, Method method, Object[] args) throws Exception {
        if (!mMap.containsKey(recieverClass)) {
            return;
        }
        boolean isGrobal = (action == null) ? true : false;
        List<RecieverEntry> entries = mMap.get(recieverClass);
        for (RecieverEntry entry : entries) {
            if (isGrobal) {
                if (entry instanceof GlobalRecieverEntry) {
                    entry.handleSignal(method, args);
                    log(this, "[handleSignal] global entry handle, class:" + recieverClass
                            + ", action:" + action
                            + ", method:" + method.getName()
                            + ", args:" + args
                            + ", reciverObject:" + entry.getReciever());
                }
                continue;
            }
            if (entry.supportAction(action)) {
                entry.handleSignal(method, args);
                log(this, "[handleSignal] action entry handle, class:" + recieverClass
                        + ", action:" + action
                        + ", method:" + method.getName()
                        + ", args:" + args
                        + ", reciverObject:" + entry.getReciever());
            }
        }
    }
}
