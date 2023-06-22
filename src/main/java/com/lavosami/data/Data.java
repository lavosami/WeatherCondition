package com.lavosami.data;

import java.util.HashMap;

public class Data {
    private String name;
    private String serial;
    private HashMap<String, Double> data;

    public Data() {
        name = "deviceName";
        serial = "deviceSerial";
        data = new HashMap<>();
    }

    public Data(String name, String serial) {
        setName(name);
        setSerial(serial);
        data = new HashMap<>();
    }

    public void add(String str, double val) { data.put(str, val); }

    public double getValue(String param) { return data.get(param); }

    public void setName(String name) { this.name = name; }

    public String getName() { return name; }

    public void setSerial(String serial) { this.serial = serial; }

    public String getSerial() { return serial; }
}
