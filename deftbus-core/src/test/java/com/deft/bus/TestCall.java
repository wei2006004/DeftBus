package com.deft.bus;

/**
 * Created by Administrator on 2016/10/8.
 */
public class TestCall implements ICall{
    public String text = "";

    @Override
    public void call(String arg) {
        text = arg;
    }
}
