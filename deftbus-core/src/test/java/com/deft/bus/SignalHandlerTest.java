package com.deft.bus;

import com.deft.bus.log.LogUtils;
import com.deft.bus.reciever.ActionRecieverEntry;
import com.deft.bus.reciever.GlobalRecieverEntry;
import com.deft.bus.reciever.RecieverEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Administrator on 2016/9/30.
 */
public class SignalHandlerTest {

    @Before
    public void setUp() throws Exception {
        LogUtils.setDebug(true);
    }

    @After
    public void tearDown() throws Exception {
        LogUtils.setDebug(false);
    }

    @Test
    public void registerReciever() throws Exception {
        SignalHandler handler = SignalHandler.getInstance();
        Map<Class, List<RecieverEntry>> map = getHandlerData(handler);
        assertThat(map).isEmpty();

        String string = "hhh";
        Integer integer = 2;
        Boolean bool = false;
        Byte byte1 = 3;
        Byte byte2 = 4;

        handler.registerReciever(string, String.class, null);
        assertThat(map.get(String.class).get(0).getClass()).isEqualTo(GlobalRecieverEntry.class);

        handler.registerReciever(integer, Integer.class, new String[]{});
        assertThat(map.get(Integer.class).get(0).getClass()).isEqualTo(GlobalRecieverEntry.class);

        handler.registerReciever(bool, Boolean.class, new String[]{""});
        ActionRecieverEntry entry = (ActionRecieverEntry) map.get(Boolean.class).get(0);
        assertTrue(entry.supportAction(""));
        assertFalse(entry.supportAction("dd"));

        handler.registerReciever(byte1, Byte.class, new String[]{"11", "22", "33"});
        ActionRecieverEntry entry1 = (ActionRecieverEntry) map.get(Byte.class).get(0);
        assertTrue(entry1.supportAction("11"));
        assertTrue(entry1.supportAction("22"));
        assertTrue(entry1.supportAction("33"));
        assertFalse(entry1.supportAction("dd"));
        assertFalse(entry1.supportAction(""));

        handler.registerReciever(byte2, Byte.class, new String[]{"11", "22", "33", "dd"});
        assertThat(map.get(Byte.class).size()).isEqualTo(2);
        ActionRecieverEntry entry2 = (ActionRecieverEntry) map.get(Byte.class).get(0);
        if (entry1 == entry2) {
            entry2 = (ActionRecieverEntry) map.get(Byte.class).get(1);
        }
        assertTrue(entry2.supportAction("11"));
        assertTrue(entry2.supportAction("22"));
        assertTrue(entry2.supportAction("33"));
        assertTrue(entry2.supportAction("dd"));
        assertFalse(entry2.supportAction(""));
        assertFalse(entry2.supportAction("aa"));

        handler.registerReciever((byte) 4, Byte.class, new String[]{"aa", "bb", "33", "dd"});
        assertThat(map.get(Byte.class).size()).isEqualTo(2);
        assertTrue(entry2.supportAction("11"));
        assertTrue(entry2.supportAction("22"));
        assertTrue(entry2.supportAction("33"));
        assertTrue(entry2.supportAction("dd"));
        assertTrue(entry2.supportAction("aa"));
        assertTrue(entry2.supportAction("bb"));
        assertFalse(entry2.supportAction(""));
        assertFalse(entry2.supportAction("123"));

        TestCall testCall1 = new TestCall();
        TestCall testCall2 = new TestCall();
        TestCall testCall3 = new TestCall();
        TestCall testCall4 = new TestCall();
        TestCall testCall5 = new TestCall();

        handler.registerReciever(testCall1, ICall.class, new String[]{""});
        handler.registerReciever(testCall2, ICall.class, new String[]{"aa", "cc", "ad", "dcd"});
        handler.registerReciever(testCall3, ICall.class, null);
        handler.registerReciever(testCall4, ICall.class, new String[]{"sd", "ccs"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"sec", "ffa"});
        assertThat(map.get(ICall.class).size()).isEqualTo(5);

        handler.registerReciever(testCall3, ICall.class, new String[]{"sd", "ccs"});
        handler.registerReciever(testCall4, ICall.class, null);
        handler.registerReciever(testCall4, ICall.class, new String[]{"we", "1212"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"255", "dd"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"we", "1212"});
        assertThat(map.get(ICall.class).size()).isEqualTo(5);

        SignalHandler.unInst();
    }

    @SuppressWarnings("unchecked")
    private static Map<Class, List<RecieverEntry>> getHandlerData(SignalHandler handler) throws Exception {
        Field field = SignalHandler.class.getDeclaredField("mMap");
        field.setAccessible(true);
        return (Map<Class, List<RecieverEntry>>) field.get(handler);
    }

    @Test
    public void clearNotAvailableRecieverEntry() throws Exception {
        SignalHandler handler = SignalHandler.getInstance();
        Map<Class, List<RecieverEntry>> map = getHandlerData(handler);
        assertThat(map).isEmpty();

        Method method = SignalHandler.class.getDeclaredMethod("clearNotAvailableRecieverEntry");
        method.setAccessible(true);

        TestCall testCall1 = new TestCall();
        TestCall testCall2 = new TestCall();
        TestCall testCall3 = new TestCall();
        TestCall testCall4 = new TestCall();
        TestCall testCall5 = new TestCall();

        handler.registerReciever(testCall1, ICall.class, new String[]{""});
        handler.registerReciever(testCall2, ICall.class, new String[]{"aa", "cc", "ad", "dcd"});
        handler.registerReciever(testCall3, ICall.class, null);
        handler.registerReciever(testCall4, ICall.class, new String[]{"sd", "ccs"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"sec", "ffa"});
        assertThat(map.get(ICall.class).size()).isEqualTo(5);

        testCall1 = null;
        testCall2 = null;
        testCall3 = null;
        testCall4 = null;
        System.gc();
        method.invoke(handler);
        assertThat(map.get(ICall.class).size()).isEqualTo(1);

        testCall5 = null;
        System.gc();
        method.invoke(handler);
        assertThat(map).isEmpty();

        SignalHandler.unInst();
    }

    @Test
    public void unregisterReciever() throws Exception {
        SignalHandler handler = SignalHandler.getInstance();
        Map<Class, List<RecieverEntry>> map = getHandlerData(handler);
        assertThat(map).isEmpty();

        initPlainRecievers(handler);
        assertThat(map.size()).isEqualTo(4);
        assertThat(map.get(Byte.class).size()).isEqualTo(4);

        handler.unregisterReciever((byte) 1, Byte.class);
        assertThat(map.size()).isEqualTo(4);
        assertThat(map.get(Byte.class).size()).isEqualTo(3);

        handler.unregisterReciever("hh", String.class);
        assertThat(map.size()).isEqualTo(4);

        handler.unregisterReciever("hhh", String.class);
        assertThat(map.size()).isEqualTo(3);

        handler.unregisterReciever("hhh", String.class);
        assertThat(map.size()).isEqualTo(3);

        handler.unregisterReciever(5, Integer.class);
        assertThat(map.size()).isEqualTo(2);

        handler.unregisterReciever((byte) 5, Byte.class);
        assertThat(map.size()).isEqualTo(2);
        assertThat(map.get(Byte.class).size()).isEqualTo(3);

        handler.unregisterReciever((byte) 2, Byte.class);
        assertThat(map.size()).isEqualTo(2);
        assertThat(map.get(Byte.class).size()).isEqualTo(2);

        handler.unregisterReciever((byte) 3, Byte.class);
        assertThat(map.size()).isEqualTo(2);
        assertThat(map.get(Byte.class).size()).isEqualTo(1);

        handler.unregisterReciever((byte) 4, Byte.class);
        assertThat(map.size()).isEqualTo(1);

        SignalHandler.unInst();
    }

    private static void initPlainRecievers(SignalHandler handler) {
        String string = "hhh";
        Integer integer = 5;
        Boolean bool = false;
        Byte byte1 = 1;
        Byte byte2 = 2;
        Byte byte3 = 3;
        Byte byte4 = 4;

        handler.registerReciever(string, String.class, null);
        handler.registerReciever(integer, Integer.class, new String[]{});
        handler.registerReciever(bool, Boolean.class, new String[]{""});
        handler.registerReciever(byte1, Byte.class, new String[]{"11", "bb", "33"});
        handler.registerReciever(byte2, Byte.class, new String[]{"11", "cc", "33", "dd"});
        handler.registerReciever(byte3, Byte.class, new String[]{"aa", "ll", "kk"});
        handler.registerReciever(byte4, Byte.class, new String[]{"11", "hh", "jj", "dd"});
        handler.registerReciever((byte) 4, Byte.class, new String[]{"aa", "bb", "33", "dd"});
        handler.registerReciever((byte) 4, Byte.class, new String[]{"11", "99", "33", "55"});
    }

    @Test
    public void handleSignal() throws Exception {
        SignalHandler handler = SignalHandler.getInstance();
        Map<Class, List<RecieverEntry>> map = getHandlerData(handler);
        assertThat(map).isEmpty();

        initPlainRecievers(handler);
        assertThat(map.size()).isEqualTo(4);
        assertThat(map.get(Byte.class).size()).isEqualTo(4);

        TestCall testCall1 = new TestCall();
        TestCall testCall2 = new TestCall();
        TestCall testCall3 = new TestCall();
        TestCall testCall4 = new TestCall();
        TestCall testCall5 = new TestCall();

        handler.registerReciever(testCall1, ICall.class, new String[]{""});
        handler.registerReciever(testCall2, ICall.class, new String[]{"aa", "cc", "ad", "dcd"});
        handler.registerReciever(testCall3, ICall.class, null);
        handler.registerReciever(testCall4, ICall.class, new String[]{"sd", "ccs"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"sec", "ffa"});
        assertThat(map.get(ICall.class).size()).isEqualTo(5);

        handler.registerReciever(testCall3, ICall.class, new String[]{"aa", "ccs"});
        handler.registerReciever(testCall4, ICall.class, null);
        handler.registerReciever(testCall4, ICall.class, new String[]{"we", "1212"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"255", "dd"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"aa", "1212"});
        assertThat(map.get(ICall.class).size()).isEqualTo(5);
        assertThat(testCall1.text).isEmpty();
        assertThat(testCall2.text).isEmpty();
        assertThat(testCall3.text).isEmpty();
        assertThat(testCall4.text).isEmpty();
        assertThat(testCall5.text).isEmpty();

        Method method = ICall.class.getMethod("call", String.class);
        handler.handleSignal(ICall.class, "dfdsafd", method, new Object[]{"999"});
        handler.handleSignal(ICall.class, "efecdes", method, new Object[]{"000"});
        assertThat(testCall1.text).isEmpty();
        assertThat(testCall2.text).isEmpty();
        assertThat(testCall3.text).isEqualTo("000");
        assertThat(testCall4.text).isEqualTo("000");
        assertThat(testCall5.text).isEmpty();

        handler.handleSignal(ICall.class, "", method, new Object[]{"111"});
        assertThat(testCall1.text).isEqualTo("111");
        assertThat(testCall2.text).isEmpty();
        assertThat(testCall3.text).isEqualTo("111");
        assertThat(testCall4.text).isEqualTo("111");
        assertThat(testCall5.text).isEmpty();

        handler.handleSignal(ICall.class, "aa", method, new Object[]{"222"});
        assertThat(testCall1.text).isNotEqualTo("222");
        assertThat(testCall2.text).isEqualTo("222");
        assertThat(testCall3.text).isEqualTo("222");
        assertThat(testCall4.text).isEqualTo("222");
        assertThat(testCall5.text).isEqualTo("222");

        handler.handleSignal(ICall.class, "1212", method, new Object[]{"333"});
        assertThat(testCall1.text).isNotEqualTo("333");
        assertThat(testCall2.text).isNotEqualTo("333");
        assertThat(testCall3.text).isEqualTo("333");
        assertThat(testCall4.text).isEqualTo("333");
        assertThat(testCall5.text).isEqualTo("333");

        SignalHandler.unInst();
    }

}