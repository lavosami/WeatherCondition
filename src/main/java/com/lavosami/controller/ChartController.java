package com.lavosami.controller;

import com.lavosami.data.Data;
import com.lavosami.data.DataBase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class ChartController {

    @GetMapping("/chart")
    public String showChart(Model model) {
        TreeMap<Date, Data> dataBase = DataBase.dataBase;

        // Подготовка данных для графика
        StringBuilder dates = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Map.Entry<Date, Data> entry : dataBase.entrySet()) {
            Date date = entry.getKey();
            Data data = entry.getValue();

            dates.append("'").append(date.toString()).append("',");
            values.append(data.getValue("system_Serial")).append(",");
        }

        model.addAttribute("dates", dates.toString());
        model.addAttribute("values", values.toString());

        return "chart";
    }
}
