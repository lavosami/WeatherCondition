package com.lavosami.controller;

import com.lavosami.data.DataBase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class ChartController {
    @GetMapping("/chart")
    public String getChartData(Model model) {
        // Создайте экземпляр класса Map для хранения данных графика
        Map<String, Object> chartData = new HashMap<>();

        // Создайте примерный набор данных
        Map<Date, Double> data = DataBase.averagedValue("Hydra-L", "BME280_temp", 1);
        // Заполните Map с вашими данными

        // Преобразуйте ключи Date в строки с помощью SimpleDateFormat
        List<String> xData = data.keySet().stream()
                .filter(date -> !Double.isNaN(data.get(date)))
                .map(date -> new SimpleDateFormat("yyyy-MM-dd").format(date))
                .toList();

        // Получите значения из Map, исключая NaN
        List<String> yData = data.values().stream()
                .filter(value -> !Double.isNaN(value))
                .map(String::valueOf)
                .toList();

        // Заполните данные графика
        chartData.put("xData", xData.toString());
        chartData.put("yData", yData.toString());

        model.addAttribute("chartData", chartData);

        return "chart";
    }

}
