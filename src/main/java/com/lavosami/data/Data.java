package com.lavosami.data;

import java.util.HashMap;

public class Data {
    private String name;
    private HashMap<String, Double> data;

    public Data(String name) {
        setName(name);
        data = new HashMap<>();
    }

    public void add(String str, double val) {
        data.put(str, val);
    }

    public double getValue(String param) {
        return data.get(param);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void print() {
        data.forEach((key, value) -> System.out.println(key + " " + value));
    }
}
