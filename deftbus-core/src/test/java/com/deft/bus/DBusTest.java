package com.deft.bus;

import com.deft.bus.log.LogUtils;
import com.deft.bus.reciever.RecieverEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Administrator on 2016/10/8.
 */
public class DBusTest {
    @Before
    public void setUp() throws Exception {
        LogUtils.setDebug(true);
    }

    @After
    public void tearDown() throws Exception {
        LogUtils.setDebug(false);
    }

    @Test
    public void register() throws Exception {
        SignalHandler handler = SignalHandler.getInstance();
        Map<Class, List<RecieverEntry>> map = getHandlerData(handler);
        assertThat(map).isEmpty();

        TestCall testCall1 = new TestCall();
        TestCall testCall2 = new TestCall();
        TestCall testCall3 = new TestCall();
        TestCall testCall4 = new TestCall();
        TestCall testCall5 = new TestCall();

        DBus.register(testCall1, ICall.class);
        DBus.register(testCall2, ICall.class, null, null);
        DBus.register(testCall3, ICall.class, new Class[]{}, new String[]{});
        DBus.register(testCall4, ICall.class, new Class[]{String.class, Integer.class}, new String[]{"aa", "bb"});
        DBus.register(testCall5, ICall.class, new Class[]{Boolean.class, Byte.class}, new String[]{"aa", "bb"});

        DBus.register(testCall3, ICall.class, new Class[]{String.class, Boolean.class}, new String[]{"cc", "add"});
        DBus.register(testCall4, ICall.class, new Class[]{String.class, Integer.class}, new String[]{"55", "22"});
        DBus.register(testCall5, ICall.class, new Class[]{Boolean.class, Byte.class}, new String[]{"aa", "8"});

        assertThat(map.size()).isEqualTo(1);
        assertThat(map.get(ICall.class).size()).isEqualTo(5);

        DBus.unInst();
    }

    @SuppressWarnings("unchecked")
    private static Map<Class, List<RecieverEntry>> getHandlerData(SignalHandler handler) throws Exception {
        Field field = SignalHandler.class.getDeclaredField("mMap");
        field.setAccessible(true);
        return (Map<Class, List<RecieverEntry>>) field.get(handler);
    }

}