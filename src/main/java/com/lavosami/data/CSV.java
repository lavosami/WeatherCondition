package com.lavosami.data;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CSV {

    private static List<String[]> readAllLines(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath, Charset.forName("CP1251"))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                return csvReader.readAll();
            }
        }
    }

    private static void parse(String path) throws Exception {
        List<String[]> list = readAllLines(Path.of(path));

        String[] array = Arrays.toString(list.get(0)).split(";");

        String name = array[1];

        array = Arrays.toString(list.get(1)).split(";");

        String[] values;
        for (int i = 2; i < list.size(); ++i) {
            Data data = new Data(name);
            values = Arrays.toString(list.get(i)).split(";");

            for (int j = 1; j < values.length-1; ++j) {
                double value = Double.parseDouble(values[j]);

                if (data.getName().contains("Тест Студии") || data.getName().contains("Hydra-L") || data.getName().contains("Hydra-L1") || data.getName().contains("Тест воздуха"))
                    try {
                        if (Objects.equals(array[j], "BME280_temp"))
                            data.add("Temperature", value);
                        if (Objects.equals(array[j], "BME280_pressure"))
                            data.add("Pressure", value);
                        if (Objects.equals(array[j], "BME280_humidity"))
                            data.add("Humidity", value);
                    } catch (NumberFormatException ignored) {}

                if (data.getName().contains("Паскаль") || data.getName().contains("Опорный барометр"))
                    try {
                        if (Objects.equals(array[j], "weather_temp"))
                            data.add("Temperature", value);
                        if (Objects.equals(array[j], "weather_pressure"))
                            data.add("Pressure", value);
                    } catch (NumberFormatException ignored) {}

                if (data.getName().contains("Тест СБ"))
                    try {
                        if (Objects.equals(array[j], "weather_temp"))
                            data.add("Temperature", value);
                        if (Objects.equals(array[j], "weather_humidity"))
                            data.add("Humidity", value);
                    } catch (NumberFormatException ignored) {}

                if (data.getName().contains("РОСА К-2"))
                    try {
                        if (Objects.equals(array[j], "weather_temp"))
                            data.add("Temperature", value);
                        if (Objects.equals(array[j], "weather_pressure"))
                            data.add("Pressure", value);
                        if (Objects.equals(array[j], "weather_humidity"))
                            data.add("Humidity", value);
                    } catch (NumberFormatException ignored) {}
            }

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            values[0] = values[0].replace("[", "");
            Date date = formatter.parse(values[0]);

            DataBase.add(date, data);
        }
    }

    public static void filesParse() throws Exception {
        File dir = new File("src/main/resources/templates/CSV");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                parse(child.getPath());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        filesParse();
        DataBase.print();
    }
}
