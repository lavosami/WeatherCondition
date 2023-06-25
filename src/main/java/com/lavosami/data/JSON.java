package com.lavosami.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class JSON {
    public static void parseFile(Path path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        // JSONArray array = (JSONArray) parser.parse(new FileReader(path.toFile()));
        JSONObject object = (JSONObject) parser.parse(String.valueOf(path));
        JSONObject take = (JSONObject) object;
        String name = (String) take.get("uName");
        System.out.println(name);
    }

}
