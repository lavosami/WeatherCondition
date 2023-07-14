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

    public static TreeMap<Date, Double> averagedEffectiveTemperature(String name, int mode, Date startDate, Date endDate) {
        TreeMap<Date, Double> averaged = new TreeMap<>();

        TreeMap<Date, Double> t1;
        TreeMap<Date, Double> t2;

        switch (mode) {
            case 1 -> {
                t1 = averageByHour(name, "Temperature", startDate, endDate);
                t2 = averageByHour(name, "Humidity", startDate, endDate);
            }
            case 2 -> {
                t1 = averageByThreeHours(name, "Temperature", startDate, endDate);
                t2 = averageByThreeHours(name, "Humidity", startDate, endDate);
            }
            case 3 -> {
                t1 = averageBy24Hours(name, "Temperature", startDate, endDate);
                t2 = averageBy24Hours(name, "Humidity", startDate, endDate);
            }
            case 4 -> {
                t1 = calculateMinMaxValues(name, "Temperature", startDate, endDate);
                t2 = calculateMinMaxValues(name, "Humidity", startDate, endDate);
            }
            default -> {
                t1 = dataToDouble(name, "Temperature", startDate, endDate);
                t2 = dataToDouble(name, "Humidity", startDate, endDate);
            }
        }

        for (Map.Entry<Date, Double> entry : t1.entrySet()) {
            Date key = entry.getKey();
            Double t = entry.getValue();
            Double h = t2.get(key);

            double value = 1;
            if (h != null)
                value *= t - 0.4 * (t - 10) * (1 - h/100);
            averaged.put(key, value);
        }

        return averaged;
    }

    public static HashMap<Date, Double> averagedTemperatureSensing(String name, int mode, Date startDate, Date endDate) {
        TreeMap<Date, Double> averaged = averagedEffectiveTemperature(name, mode, startDate, endDate);

        HashMap<Date, Double> result = new HashMap<>();

        for (Map.Entry<Date, Double> entry : averaged.entrySet()) {
            Date key = entry.getKey();
            Double t = entry.getValue();

            if (t > 30) result.put(key, 8.0);
            if (t <= 30 && t > 24) result.put(key, 7.0);
            if (t <= 24 && t > 18) result.put(key, 6.0);
            if (t <= 18 && t > 12) result.put(key, 5.0);
            if (t <= 12 && t > 6) result.put(key, 4.0);
            if (t <= 6 && t > 0) result.put(key, 3.0);
            if (t <= 0 && t > -12) result.put(key, 2.0);
            if (t <= -12 && t > -24) result.put(key, 1.0);
            if (t <= -24 && t > -30) result.put(key, 0.0);
        }

        return result;
    }

    /**
     * @param name - key of values
     * @param key - name of the value to retrieve
     * @param mode - 1: hour, 2: three hours, 3: 24 hours, 4: min/max
     * @param startDate - start date of the time range
     * @param endDate - end date of the time range
     * @return - TreeMap with the required averaged values
     */
    public static TreeMap<Date, Double> averagedValue(String name, String key, int mode, Date startDate, Date endDate) {
        TreeMap<Date, Double> averaged;

        switch (mode) {
            case 1 -> averaged = averageByHour(name, key, startDate, endDate);
            case 2 -> averaged = averageByThreeHours(name, key, startDate, endDate);
            case 3 -> averaged = averageBy24Hours(name, key, startDate, endDate);
            case 4 -> averaged = calculateMinMaxValues(name, key, startDate, endDate);
            default -> averaged = dataToDouble(name, key, startDate, endDate);
        }

        return averaged;
    }

    private static TreeMap<Date, Double> averageByHour(String name, String key, Date startDate, Date endDate) {
        TreeMap<Date, Double> averagedMap = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Map<Date, List<Double>> hourlyValues = new HashMap<>();

        TreeMap<Date, Double> temp = dataToDouble(name, key, startDate, endDate);

        for (Map.Entry<Date, Double> entry : temp.entrySet()) {
            Date date = entry.getKey();
            Double value = entry.getValue();

            if (value != null && !Double.isNaN(value) && date.after(startDate) && date.before(endDate)) {
                calendar.setTime(date);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date hourStart = calendar.getTime();

                hourlyValues.computeIfAbsent(hourStart, k -> new ArrayList<>()).add(value);
            }
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

    private static TreeMap<Date, Double> averageByThreeHours(String name, String key, Date startDate, Date endDate) {
        TreeMap<Date, Double> averagedMap = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Map<Date, List<Double>> threeHourlyValues = new HashMap<>();

        TreeMap<Date, Double> temp = dataToDouble(name, key, startDate, endDate);

        for (Map.Entry<Date, Double> entry : temp.entrySet()) {
            Date date = entry.getKey();
            double value = entry.getValue();

            if (!Double.isNaN(value) && date.after(startDate) && date.before(endDate)) {
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int threeHourBlock = hour / 3;

                calendar.set(Calendar.HOUR_OF_DAY, threeHourBlock * 3);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date threeHourBlockStart = calendar.getTime();

                threeHourlyValues.computeIfAbsent(threeHourBlockStart, k -> new ArrayList<>()).add(value);
            }
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

    private static TreeMap<Date, Double> averageBy24Hours(String name, String key, Date startDate, Date endDate) {
        TreeMap<Date, Double> averagedMap = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Map<Date, List<Double>> dailyValues = new HashMap<>();

        TreeMap<Date, Double> temp = dataToDouble(name, key, startDate, endDate);

        for (Map.Entry<Date, Double> entry : temp.entrySet()) {
            Date date = entry.getKey();
            double value = entry.getValue();

            if (!Double.isNaN(value) && date.after(startDate) && date.before(endDate)) {
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date dayStart = calendar.getTime();

                dailyValues.computeIfAbsent(dayStart, k -> new ArrayList<>()).add(value);
            }
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

    private static TreeMap<Date, Double> calculateMinMaxValues(String name, String key, Date startDate, Date endDate) {
        TreeMap<Date, Double> minMaxValues = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Date previousDate = null;
        Date minTime = null;
        Date maxTime = null;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        TreeMap<Date, Double> temp = dataToDouble(name, key, startDate, endDate);

        for (Map.Entry<Date, Double> entry : temp.entrySet()) {
            Date currentDate = entry.getKey();
            double value = entry.getValue();

            if (!Double.isNaN(value) && currentDate.after(startDate) && currentDate.before(endDate)) {
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
        }

        if (previousDate != null && minTime != null && maxTime != null) {
            minMaxValues.put(minTime, minValue);
            minMaxValues.put(maxTime, maxValue);
        }

        return minMaxValues;
    }

    private static TreeMap<Date, Double> dataToDouble(String name, String key, Date startDate, Date endDate) {
        TreeMap<Date, Double> doubleTreeMap = new TreeMap<>();

        dataBase.forEach((k, v) -> {
            try {
                if (v.getName().equals(name) && k.after(startDate) && k.before(endDate)) {
                    double value = v.getValue(key);
                    doubleTreeMap.put(k, value);
                }
            } catch (Exception ignored) {}
        });

        return doubleTreeMap;
    }
}
