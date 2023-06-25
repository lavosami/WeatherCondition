package com.lavosami.data;

import java.util.Date;
import java.util.TreeMap;

public class DataBase {
    private static TreeMap<Date, Data> dataBase = new TreeMap<>();

    public static void add(Date date, Data data) {
        dataBase.put(date, data);
    }

    public static void print() {
        dataBase.forEach((key, value) -> System.out.println(key + " " + value));
    }
}
