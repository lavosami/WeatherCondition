package com.lavosami.data;

import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CSV {

    private static List<String[]> readAllLines(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath, Charset.forName("CP1251"))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                return csvReader.readAll();
            }
        }
    }

    public static void parse(String path) throws Exception {
        List<String[]> list = readAllLines(Path.of(path));

        String[] array = Arrays.toString(list.get(0)).split(";");

        String name = array[1];

        array = Arrays.toString(list.get(1)).split(";");

        String[] values;
        for (int i = 2; i < list.size(); ++i) {
            Data data = new Data(name);
            values = Arrays.toString(list.get(i)).split(";");

            for (int j = 1; j < values.length-1; ++j) {
                double value = 0;

                try {
                    value = Double.parseDouble(values[j]);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }

                data.add(array[j], value);
            }

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            values[0] = values[0].replace("[", "");
            Date date = formatter.parse(values[0]);

            DataBase.add(date, data);
        }
    }

    public static void main(String[] args) throws Exception {
        parse("/Users/lavosami/Downloads/CSV/POCA_K-2_(1).csv");
        DataBase.print();
    }
}
