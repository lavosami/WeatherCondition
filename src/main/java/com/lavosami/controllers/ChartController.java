package com.lavosami.controllers;

import com.lavosami.data.CSV;
import com.lavosami.data.Data;
import com.lavosami.data.DataBase;
import com.lavosami.data.JSON;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.lavosami.data.DataBase.dataBase;

@Controller
public class ChartController {
    private String parserMode = "JSON";

    private List<String> xData = new ArrayList<>();
    private List<Double> yData = new ArrayList<>();

    @RequestMapping(value = "/chart", method = RequestMethod.POST)
    public String modifyChart(
            @RequestParam(name = "item") String selectedDevice,
            @RequestParam(name = "value") String selectedValue,
            @RequestParam(name = "parse") String selectedParser,
            @RequestParam(name = "averaging") String selectedAveraging,
            @RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            Model model) {

        int mode;
        switch (selectedAveraging) {
            case "Average by hour" -> mode = 1;
            case "Average by 3 hours" -> mode = 2;
            case "Average by 24 hours" -> mode = 3;
            case "Min/Max values in every day" -> mode = 4;
            default -> mode = 0;
        }

        parserMode = selectedParser;

        if (!Objects.equals(selectedValue, "Temperature sensing")) {
            Map<Date, Double> data;
            if (Objects.equals(selectedValue, "Effective temperature")) {
                data = DataBase.averagedEffectiveTemperature(selectedDevice, mode, startDate, endDate);
            } else {
                data = DataBase.averagedValue(selectedDevice, selectedValue, mode, startDate, endDate);
            }

            Map<Date, Double> finalData = data;
            xData = data.keySet().stream()
                    .filter(date -> !Double.isNaN(finalData.get(date)))
                    .map(date -> new SimpleDateFormat("yyyy-MM-dd").format(date))
                    .toList();

            yData = data.values().stream()
                    .filter(value -> !Double.isNaN(value))
                    .toList();

            model.addAttribute("xData", xData);
            model.addAttribute("yData", yData);
        } else {
            Map<Date, Double> data = DataBase.averagedTemperatureSensing(selectedDevice, mode, startDate, endDate);
            xData = data.keySet().stream()
                    .map(date -> new SimpleDateFormat("yyyy-MM-dd").format(date))
                    .toList();

            yData = data.values().stream()
                    .filter(value -> !Double.isNaN(value))
                    .toList();

            model.addAttribute("xData", xData);
            model.addAttribute("yData", yData);
        }
        return "chart";
    }

    @RequestMapping(value = "/chart", method = RequestMethod.GET)
    public String getChartData(Model model) {

        // --------------------------------------------
        List<String> values = new ArrayList<>();
        values.add("Temperature");
        values.add("Pressure");
        values.add("Humidity");
        values.add("Effective temperature");
        values.add("Temperature sensing");
        model.addAttribute("chartValues", values);
        // --------------------------------------------

        // --------------------------------------------
        List<String> parse = new ArrayList<>();
        parse.add("JSON");
        parse.add("CSV");
        model.addAttribute("chartParser", parse);
        // --------------------------------------------

        // --------------------------------------------
        List<String> averaging = new ArrayList<>();
        averaging.add("All values");
        averaging.add("Average by hour");
        averaging.add("Average by 3 hours");
        averaging.add("Average by 24 hours");
        averaging.add("Min/Max values in every day");
        model.addAttribute("chartAveraging", averaging);
        // --------------------------------------------

        if (Objects.equals(parserMode, "JSON")) {
            JSON.filesParse();
        } else {
            try {
                CSV.filesParse();
            } catch (Exception ignored) {
                JSON.filesParse();
            }
        }

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
        model.addAttribute("xData", xData);
        model.addAttribute("yData", yData);

        return "chart";
        // --------------------------------------------
    }

    @GetMapping("/getXData")
    @ResponseBody
    public List<String> getXData() {
        return xData;
    }

    @GetMapping("/getYData")
    @ResponseBody
    public List<Double> getYData() {
        return yData;
    }
}
