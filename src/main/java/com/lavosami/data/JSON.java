package com.lavosami.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class JSON {
    private static void parse(String path) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(path));
            for (Object obj : jsonObject.values()) {
                JSONObject o = (JSONObject) obj;
                Data data = new Data(o.get("uName") + " (" + o.get("serial") + ")");

                JSONObject values = (JSONObject) o.get("data");
                String[] keys = (String[]) (values.keySet()).toArray(new String[values.keySet().size()]);

                for (String key : keys) {
                    if (data.getName().contains("Тест Студии") || data.getName().contains("Hydra-L") || data.getName().contains("Hydra-L1") || data.getName().contains("Тест воздуха"))
                        try {
                            if (Objects.equals(key, "BME280_temp"))
                                data.add("Temperature", Double.parseDouble((String) values.get(key)));
                            if (Objects.equals(key, "BME280_pressure"))
                                data.add("Pressure", Double.parseDouble((String) values.get(key)));
                            if (Objects.equals(key, "BME280_humidity"))
                                data.add("Humidity", Double.parseDouble((String) values.get(key)));
                        } catch (NumberFormatException ignored) {}

                    if (data.getName().contains("Паскаль") || data.getName().contains("Опорный барометр"))
                        try {
                            if (Objects.equals(key, "weather_temp"))
                                data.add("Temperature", Double.parseDouble((String) values.get(key)));
                            if (Objects.equals(key, "weather_pressure"))
                                data.add("Pressure", Double.parseDouble((String) values.get(key)));
                        } catch (NumberFormatException ignored) {}

                    if (data.getName().contains("Тест СБ"))
                        try {
                            if (Objects.equals(key, "weather_temp"))
                                data.add("Temperature", Double.parseDouble((String) values.get(key)));
                            if (Objects.equals(key, "weather_humidity"))
                                data.add("Humidity", Double.parseDouble((String) values.get(key)));
                        } catch (NumberFormatException ignored) {}

                    if (data.getName().contains("РОСА К-2"))
                        try {
                            if (Objects.equals(key, "weather_temp"))
                                data.add("Temperature", Double.parseDouble((String) values.get(key)));
                            if (Objects.equals(key, "weather_pressure"))
                                data.add("Pressure", Double.parseDouble((String) values.get(key)));
                            if (Objects.equals(key, "weather_humidity"))
                                data.add("Humidity", Double.parseDouble((String) values.get(key)));
                        } catch (NumberFormatException ignored) {}
                }

                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = formatter.parse((String) o.get("Date"));

                DataBase.add(date, data);
            }
        } catch (IOException | ParseException | java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void filesParse() {
        File dir = new File("src/main/resources/templates/JSON");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                parse(child.getPath());
            }
        }
    }

    public static void main(String[] args) {
        filesParse();
        DataBase.print();
    }

}
