package com.deft.bus;

/**
 * Created by Administrator on 2016/9/29.
 */
public class Main {

    public interface Signal1 {
        void call(String text);
    }

    public interface Signal2 {
        void run(int num, boolean flag);
    }

    static class Reciever1 implements Signal1 {
        @Override
        public void call(String text) {
            System.out.println("Reciever1(call) text:" + text);
        }
    }

    static class Reciever2 implements Signal2 {
        @Override
        public void run(int num, boolean flag) {
            System.out.println("Reciever2(run) num:" + num + " flag:" + flag);
        }
    }

    static class SenderMgr {
        private Signal1 sender1;
        private Signal2 sender2;

        public SenderMgr() {
            sender1 = SenderCreator.createSender(this, Signal1.class);
            sender2 = SenderCreator.createSender(this, Signal2.class);
        }

        public void send() {
            sender1.call("hello");
            sender2.run(10, true);
        }
    }

    public static void main(String[] args) {
        Reciever1 reciever1 = new Reciever1();
        Reciever2 reciever2 = new Reciever2();

        DBus.register(reciever1, Signal1.class);
        DBus.register(reciever2, Signal2.class, new Class[]{SenderMgr.class}, null);

        SenderMgr senderMgr = new SenderMgr();
        senderMgr.send();

        DBus.unInst();
    }
}
