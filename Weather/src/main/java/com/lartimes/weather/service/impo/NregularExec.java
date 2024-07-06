package com.lartimes.weather.service.impo;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/2 0:27
 */
public interface NregularExec<T> {

    Converter<String, LocalDate> dateConverter = new Converter<>() {
        @Override
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    };

    T exec(Row cell, int totalCells);
}
