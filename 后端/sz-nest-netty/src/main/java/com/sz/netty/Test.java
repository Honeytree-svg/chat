package com.sz.netty;

import java.util.*;

public class Test {

    public static void main(String []a){
        HashMap<String, ArrayList<String>> map=new HashMap<String, ArrayList<String>>();
        ArrayList<String> temp=map.get("key");
        if (temp==null){
            temp = new ArrayList<String>();
        }
        temp.add("123");
        temp.add("456");
        map.put("key",temp);
//        map.forEach((k,v)-> {
//            System.out.println(k+"->"+v);
//        });
        for (HashMap.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            System.out.println("UserId: " + entry.getKey());
            ArrayList<String> strings = entry.getValue();
            for (String t: strings) {
                System.out.print(t+",");
            }
        }

        temp = map.get("key");
        temp.remove("123");
        for (String t: temp) {
            System.out.print(t+",");
        }
    }
}
