package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {
    private static final Map<String, String> MONTHS = Map.ofEntries(
            Map.entry("янв", "1"),
            Map.entry("фев", "2"),
            Map.entry("мар", "3"),
            Map.entry("апр", "4"),
            Map.entry("май", "5"),
            Map.entry("июн", "6"),
            Map.entry("июл", "7"),
            Map.entry("авг", "8"),
            Map.entry("сен", "9"),
            Map.entry("окт", "10"),
            Map.entry("ноя", "11"),
            Map.entry("дек", "12")
    );
    @Override
    public LocalDateTime parse(String parse) {
        return null;
    }
}
