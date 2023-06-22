package com.lavosami.data;

import java.util.Date;
import java.util.HashMap;

public class DataBase {
    private static HashMap<Date, Data> dataBase = new HashMap<>();

    public static void add(Date date, Data data) {
        dataBase.put(date, data);
    }
}
