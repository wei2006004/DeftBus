package com.deft.bus.reciever;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Administrator on 2016/10/8.
 */
public class RecieverEntryTest {
    public interface ICall{
        void call(String arg);
    }

    class TestCall implements ICall{
        String text = "";
        @Override
        public void call(String arg) {
            text = arg;
        }
    }

    ActionRecieverEntry<TestCall> entry;
    TestCall testCall;

    @Before
    public void setUp() throws Exception {
        testCall = new TestCall();
        entry = new ActionRecieverEntry<TestCall>(testCall, new String[]{"1", "22", "ccc"});
    }

    @Test
    public void supportAction() throws Exception {
        assertThat(entry.supportAction("22")).isEqualTo(true);
        assertThat(entry.supportAction("ccc")).isEqualTo(true);
        assertThat(entry.supportAction("1")).isEqualTo(true);
        assertFalse(entry.supportAction("ddd"));
        assertFalse(entry.supportAction(""));
        assertFalse(entry.supportAction(null));

        GlobalRecieverEntry globalRecieverEntry = new GlobalRecieverEntry<TestCall>(testCall);
        assertTrue(globalRecieverEntry.supportAction("11"));
        assertTrue(globalRecieverEntry.supportAction(null));
        assertTrue(globalRecieverEntry.supportAction(""));
    }

    @Test
    public void addActions() throws Exception {
        entry.addActions(new String[]{"ttt", "kkk", "bbb", "22"});
        assertTrue(entry.supportAction("ttt"));
        assertTrue(entry.supportAction("22"));
        assertTrue(entry.supportAction("kkk"));
        assertTrue(entry.supportAction("bbb"));

        assertFalse(entry.supportAction("ddd"));
        assertFalse(entry.supportAction(""));
        assertFalse(entry.supportAction(null));
    }

    @Test
    public void isAvailable() throws Exception {
        assertTrue(entry.isAvailable());

        String string = "hhh";
        GlobalRecieverEntry globalRecieverEntry = new GlobalRecieverEntry<String>(string);
        assertTrue(globalRecieverEntry.isAvailable());

        TestCall testCall = new TestCall();
        globalRecieverEntry = new GlobalRecieverEntry<TestCall>(testCall);
        assertTrue(globalRecieverEntry.isAvailable());
        testCall = null;
        System.gc();
        assertFalse(globalRecieverEntry.isAvailable());
        assertThat(globalRecieverEntry.getReciever()).isEqualTo(null);
        assertTrue(entry.isAvailable());
    }

    @Test
    public void getReciever() throws Exception {
        assertThat(entry.getReciever().getClass()).isEqualTo(TestCall.class);
        assertThat(entry.getReciever()).isEqualTo(testCall);

        String string = "hhh";
        GlobalRecieverEntry globalRecieverEntry = new GlobalRecieverEntry<String>(string);
        assertThat(globalRecieverEntry.getReciever()).isEqualTo(string);
    }

    @Test
    public void handleSignal() throws Exception {
        Method method = ICall.class.getMethod("call", String.class);
        assertThat(testCall.text).isEmpty();
        entry.handleSignal(method, new Object[]{"4444"});
        assertThat(testCall.text).isEqualTo("4444");
        assertThat(entry.getReciever().text).isEqualTo("4444");
    }
}