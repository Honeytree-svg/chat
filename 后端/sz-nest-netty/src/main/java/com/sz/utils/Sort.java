package com.sz.utils;

import java.util.ArrayList;
import java.util.List;

public class Sort {

    public static void main(String []a){

        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(2);
        list.add(1);
        int high = 2;
        --high;
        System.out.println(list.get(high));
    }

    void QuickSort(List<Integer> list,int low, int high){

        if (low<high){
            int pivotpos = Partition(list,low,high);
        }
    }

    int Partition(List<Integer> list,int low,int high){

        Integer pivot = list.get(low);
        while(low<high){
            while(low<high && list.get(high)>=pivot) --high;
        }

        return low;
    }
}
