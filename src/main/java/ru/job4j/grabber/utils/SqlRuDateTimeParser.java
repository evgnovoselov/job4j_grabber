package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {
    private static final Map<String, String> MONTHS = Map.ofEntries(
            Map.entry("янв", "01"),
            Map.entry("фев", "02"),
            Map.entry("мар", "03"),
            Map.entry("апр", "04"),
            Map.entry("май", "05"),
            Map.entry("июн", "06"),
            Map.entry("июл", "07"),
            Map.entry("авг", "08"),
            Map.entry("сен", "09"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );

    @Override
    public LocalDateTime parse(String parse) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MM yy, HH:mm");
        String date = "";
        if (!parse.startsWith("сег") && !parse.startsWith("вч")) {
            String[] split = parse.split(" ");
            split[1] = MONTHS.get(split[1]);
            date = String.join(" ", split);
        }
        if (parse.startsWith("сег")) {
            String time = parse.split(" ")[1];
            String[] split = LocalDateTime.now().format(formatter).split(" ");
            split[3] = time;
            date = String.join(" ", split);
        }
        if (parse.startsWith("вч")) {
            String time = parse.split(" ")[1];
            String[] split = LocalDateTime.now().minusDays(1).format(formatter).split(" ");
            split[3] = time;
            date = String.join(" ", split);
        }
        return LocalDateTime.parse(date, formatter);
    }
}
