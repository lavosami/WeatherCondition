package com.lavosami.data;

import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class CSV {

    static public List<String[]> readAllLines(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath, Charset.forName("CP1251"))) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                return csvReader.readAll();
            }
        }
    }

    static List<String[]> list;

    static {
        try {
            list = readAllLines(Path.of("/Users/lavosami/Downloads/last_export.csv"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(list.get(0)));
    }
}
