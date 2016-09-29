package com.deft.bus;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/29.
 */
class RecieverMap {
    private Map<Class, ActionMap> mMap = new HashMap<>();

    static void unInst() {
        if (INSTANCE != null) {
            synchronized (mLock) {
                INSTANCE = null;
            }
        }
    }

    private static RecieverMap INSTANCE;
    private final static Object mLock = new Object();

    static RecieverMap getInstance() {
        if (INSTANCE == null) {
            synchronized (mLock) {
                if (INSTANCE == null) {
                    INSTANCE = new RecieverMap();
                }
            }
        }
        return INSTANCE;
    }

    synchronized <T> void registerReciever(T recieverObject, Class<T> recieverClass, String[] actions) {

    }

    synchronized <T> void unregisterReciever(T recieverObject, Class<T> recieverClass) {

    }

    synchronized <T> List<T> findRecievers(Class<T> recieverClass, String action) {
        if (mMap.containsKey(recieverClass)) {
            ActionMap<T> actionMap = mMap.get(recieverClass);
            return actionMap.getRecievers(action);
        }
        return new LinkedList<T>();
    }

    static class ActionMap<T> {
        private Map<String, List<WeakReference<T>>> mActions = new HashMap<>();

        void addReciever(String action, T reciever) {
            if (!mActions.containsKey(action)) {
                mActions.put(action, new LinkedList<>());
            }
            List<WeakReference<T>> list = mActions.get(action);
            for (WeakReference<T> ref : list) {
                T value = ref.get();
                if (value == reciever) {
                    return;
                }
            }
            list.add(0, new WeakReference<>(reciever));
        }

        void deleteReciever(String action, T reciever) {
            if (mActions.containsKey(action)) {
                List<WeakReference<T>> list = mActions.get(action);
                for (WeakReference<T> ref : list) {
                    T value = ref.get();
                    if (value == null || value == reciever) {
                        list.remove(ref);
                    }
                }
            }
        }

        List<T> getRecievers(String action) {
            if (!mActions.containsKey(action)) {
                return new LinkedList<T>();
            }
            List<WeakReference<T>> list = mActions.get(action);
            List<T> result = new LinkedList<>();
            for (WeakReference<T> ref : list) {
                T value = ref.get();
                if (value == null) {
                    list.remove(ref);
                } else {
                    result.add(value);
                }
            }
            return result;
        }
    }
}
