package com.sz;

import com.sz.netty.ChatHandler;
import com.sz.netty.UserChannelRel;
import com.sz.pojo.Users;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String []arg) throws ClassNotFoundException {


        List<String> list = new ArrayList<>(3200);
        for (int i = 0; i < 3200; i++) {

            list.add(String.valueOf(i));
        }

        int length = list.size();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 800; i++) {

                    System.out.println(list.get(i));
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 800; i < 1600; i++) {

                    System.out.println(list.get(i));
                }
            }
        });

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1600; i < 2400; i++) {

                    System.out.println(list.get(i));
                }
            }
        });

        Thread thread4 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 2400; i < 3200; i++) {

                    System.out.println(list.get(i));
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();





//        List<Channel> channels = UserChannelRel.get("210330BR73MF3TR4");
//        Channel channel = channels.get(0);
//        System.out.println(channel.id().asLongText());
//        UserChannelRel.delete("210330BR73MF3TR4",channel.id());
//        channels = UserChannelRel.get("210330BR73MF3TR4");
//        for (Channel c :
//                channels) {
//            System.out.println(c.id().asLongText());
//        }
//        UserChannelRel.output();
        String s="string";
        String s1=new String("string");
        //System.out.println(s1);

//        Integer a=1;
//        System.out.println();
//        System.out.println(a.hashCode());

//        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//        Class<com.sz.pojo.Users> aClass = (Class<Users>) systemClassLoader.loadClass("com.sz.pojo.Users");
//        try {
//            Users users = aClass.newInstance();
//            users.setId("sdfs");
//            System.out.println(users);
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

    }
}
