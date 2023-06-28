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

public class JSON {
    private static void parse(String path) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(path)) {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(path));
            for (Object obj : jsonObject.values()) {
                JSONObject o = (JSONObject) obj;
                Data data = new Data((String) o.get("uName"));

                JSONObject values = (JSONObject) o.get("data");
                String[] keys = (String[]) (values.keySet()).toArray(new String[values.keySet().size()]);

                for (String key : keys) {
                    try {
                        data.add(key, Double.parseDouble((String) values.get(key)));
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

    public static void filesParse() throws Exception {
        File dir = new File("src/main/resources/templates/JSON");
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
