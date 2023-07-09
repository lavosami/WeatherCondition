package com.lavosami.controllers;

import com.lavosami.data.Data;
import com.lavosami.data.DataBase;
import com.lavosami.data.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.lavosami.data.DataBase.dataBase;

@Controller
public class ChartController {
    @GetMapping("/chart")
    public String getChartData(Model model) {
        JSON.filesParse();

        // --------------------------------------------
        List<String> items = new ArrayList<>();
        for (Data data : dataBase.values()) {
            if (!items.contains(data.getName())) {
                items.add(data.getName());
            }
        }
        Collections.sort(items);
        model.addAttribute("chartItems", items);
        // --------------------------------------------

        // --------------------------------------------
        List<String> values = new ArrayList<>();
        values.add("Temperature");
        values.add("Pressure");
        values.add("Humidity");
        model.addAttribute("chartValues", values);
        // --------------------------------------------

        // --------------------------------------------
        Map<String, Object> chartData = new HashMap<>();

        Map<Date, Double> data = DataBase.averagedValue("Hydra-L (04)", "Temperature", 3);

        List<String> xData = data.keySet().stream()
                .filter(date -> !Double.isNaN(data.get(date)))
                .map(date -> new SimpleDateFormat("yyyy-MM-dd").format(date))
                .toList();

        List<String> yData = data.values().stream()
                .filter(value -> !Double.isNaN(value))
                .map(String::valueOf)
                .toList();

        chartData.put("xData", xData.toString());
        chartData.put("yData", yData.toString());

        model.addAttribute("chartData", chartData);

        return "chart";
        // --------------------------------------------
    }

}
