package com.deft.bus;

import com.deft.bus.log.LogUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Administrator on 2016/10/8.
 */
public class SenderCreatorTest {
    @Before
    public void setUp() throws Exception {
        LogUtils.setDebug(true);
    }

    @After
    public void tearDown() throws Exception {
        LogUtils.setDebug(false);
    }

    @Test
    public void createSenderByAction() throws Exception {
        SignalHandler handler = SignalHandler.getInstance();

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

        handler.registerReciever(testCall3, ICall.class, new String[]{"aa", "ccs"});
        handler.registerReciever(testCall4, ICall.class, null);
        handler.registerReciever(testCall4, ICall.class, new String[]{"we", "1212"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"255", "dd"});
        handler.registerReciever(testCall5, ICall.class, new String[]{"aa", "1212"});

        assertThat(testCall1.text).isEmpty();
        assertThat(testCall2.text).isEmpty();
        assertThat(testCall3.text).isEmpty();
        assertThat(testCall4.text).isEmpty();
        assertThat(testCall5.text).isEmpty();

        ICall sender1 = SenderCreator.createSenderByAction(ICall.class, "");
        ICall sender2 = SenderCreator.createSenderByAction(ICall.class, "aa");
        ICall sender3 = SenderCreator.createSenderByAction(ICall.class, "1212");

        sender1.call("111");
        assertThat(testCall1.text).isEqualTo("111");
        assertThat(testCall2.text).isEmpty();
        assertThat(testCall3.text).isEqualTo("111");
        assertThat(testCall4.text).isEqualTo("111");
        assertThat(testCall5.text).isEmpty();

        sender2.call("222");
        assertThat(testCall1.text).isNotEqualTo("222");
        assertThat(testCall2.text).isEqualTo("222");
        assertThat(testCall3.text).isEqualTo("222");
        assertThat(testCall4.text).isEqualTo("222");
        assertThat(testCall5.text).isEqualTo("222");

        sender3.call("333");
        assertThat(testCall1.text).isNotEqualTo("333");
        assertThat(testCall2.text).isNotEqualTo("333");
        assertThat(testCall3.text).isEqualTo("333");
        assertThat(testCall4.text).isEqualTo("333");
        assertThat(testCall5.text).isEqualTo("333");

        SignalHandler.unInst();
    }
}