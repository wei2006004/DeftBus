package com.deft.bus.reciever;

import java.util.Set;

/**
 * Created by Administrator on 2016/9/30.
 */
public class ActionRecieverEntry<T> extends WeakReferenceRecieverEntry<T> {
    private Set<String> mActions;

    public ActionRecieverEntry(T reciever, String[] actions) {
        super(reciever);
        addActions(actions);
    }

    @Override
    public boolean supportAction(String action) {
        return mActions.contains(action);
    }

    public void addActions(String[] actions){
        for (String action : actions){
            mActions.add(action);
        }
    }
}
