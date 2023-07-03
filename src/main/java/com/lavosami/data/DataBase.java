package com.lavosami.data;

import java.util.*;

public class DataBase {
    public static TreeMap<Date, Data> dataBase = new TreeMap<>();

    public static void add(Date date, Data data) {
        dataBase.put(date, data);
    }

    public static void print() {
        dataBase.forEach((key, value) -> System.out.println(key + " " + value));
    }

    /**
     * @param key - key of values
     * @param mode - 1: hour, 2: three hours, 3: 24 hours, 4: min/max
     * @return - treemap w/ required averaged values
     */
    public static TreeMap<Date, Double> averagedValue(String key, int mode) {
        JSON.filesParse();
        TreeMap<Date, Double> averaged;

        switch (mode) {
            case 1 -> averaged = averageByHour(key);

            case 2 -> averaged = averageByThreeHours(key);

            case 3 -> averaged = averageBy24Hours(key);

            case 4 -> averaged = calculateMinMaxValues(key);

            default -> averaged = dataToDouble(key);
        }

        return averaged;
    }

    private static TreeMap<Date, Double> averageByHour(String key) {
        TreeMap<Date, Double> averagedMap = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Map<Date, List<Double>> hourlyValues = new HashMap<>();

        TreeMap<Date, Double> temp = dataToDouble(key);

        for (Map.Entry<Date, Double> entry : temp.entrySet()) {
            Date date = entry.getKey();
            Double value = entry.getValue();

            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date hourStart = calendar.getTime();

            hourlyValues.computeIfAbsent(hourStart, k -> new ArrayList<>()).add(value);
        }

        for (Map.Entry<Date, List<Double>> entry : hourlyValues.entrySet()) {
            Date hourStart = entry.getKey();
            List<Double> values = entry.getValue();

            double sum = 0.0;
            for (double value : values) {
                sum += value;
            }

            double average = sum / values.size();
            averagedMap.put(hourStart, average);
        }

        return averagedMap;
    }

    private static TreeMap<Date, Double> averageByThreeHours(String key) {
        TreeMap<Date, Double> averagedMap = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Map<Date, List<Double>> threeHourlyValues = new HashMap<>();

        TreeMap<Date, Double> temp = dataToDouble(key);

        for (Map.Entry<Date, Double> entry : temp.entrySet()) {
            Date date = entry.getKey();
            double value = entry.getValue();

            calendar.setTime(date);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int threeHourBlock = hour / 3;

            calendar.set(Calendar.HOUR_OF_DAY, threeHourBlock * 3);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date threeHourBlockStart = calendar.getTime();

            threeHourlyValues.computeIfAbsent(threeHourBlockStart, k -> new ArrayList<>()).add(value);
        }

        for (Map.Entry<Date, List<Double>> entry : threeHourlyValues.entrySet()) {
            Date threeHourBlockStart = entry.getKey();
            List<Double> values = entry.getValue();

            double sum = 0.0;
            for (double value : values) {
                sum += value;
            }

            double average = sum / values.size();
            averagedMap.put(threeHourBlockStart, average);
        }

        return averagedMap;
    }

    private static TreeMap<Date, Double> averageBy24Hours(String key) {
        TreeMap<Date, Double> averagedMap = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Map<Date, List<Double>> dailyValues = new HashMap<>();

        TreeMap<Date, Double> temp = dataToDouble(key);

        for (Map.Entry<Date, Double> entry : temp.entrySet()) {
            Date date = entry.getKey();
            double value = entry.getValue();

            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date dayStart = calendar.getTime();

            dailyValues.computeIfAbsent(dayStart, k -> new ArrayList<>()).add(value);
        }

        for (Map.Entry<Date, List<Double>> entry : dailyValues.entrySet()) {
            Date dayStart = entry.getKey();
            List<Double> values = entry.getValue();

            double sum = 0.0;
            for (double value : values) {
                sum += value;
            }

            double average = sum / values.size();
            averagedMap.put(dayStart, average);
        }

        return averagedMap;
    }

    private static TreeMap<Date, Double> calculateMinMaxValues(String key) {
        TreeMap<Date, Double> minMaxValues = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date previousDate = null;
        Date minTime = null;
        Date maxTime = null;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        TreeMap<Date, Double> temp = dataToDouble(key);

        for (Map.Entry<Date, Double> entry : temp.entrySet()) {
            Date currentDate = entry.getKey();
            double value = entry.getValue();

            calendar.setTime(currentDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date currentDay = calendar.getTime();

            if (previousDate != null && !currentDay.equals(previousDate)) {
                if (minTime != null && maxTime != null) {
                    minMaxValues.put(minTime, minValue);
                    minMaxValues.put(maxTime, maxValue);
                }
                minValue = Double.MAX_VALUE;
                maxValue = Double.MIN_VALUE;
                minTime = null;
                maxTime = null;
            }

            if (value < minValue) {
                minValue = value;
                minTime = currentDate;
            }
            if (value > maxValue) {
                maxValue = value;
                maxTime = currentDate;
            }

            previousDate = currentDay;
        }

        // Добавление минимума и максимума для последнего дня
        if (previousDate != null && minTime != null && maxTime != null) {
            minMaxValues.put(minTime, minValue);
            minMaxValues.put(maxTime, maxValue);
        }

        return minMaxValues;
    }


    private static TreeMap<Date, Double> dataToDouble(String key) {
        TreeMap<Date, Double> doubleTreeMap = new TreeMap<>();

        dataBase.forEach((k, v) -> {
            try {
                doubleTreeMap.put(k, v.getValue(key));
            } catch (Exception ignored) {}
        });

        return doubleTreeMap;
    }

    public static void main(String[] args) {
        TreeMap<Date, Double> averaged = averagedValue("weather_pressure", 3);
        System.out.println(averaged);
    }
}
