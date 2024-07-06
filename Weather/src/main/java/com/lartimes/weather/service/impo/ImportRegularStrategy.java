package com.lartimes.weather.service.impo;

import com.lartimes.weather.model.po.DailyWeather;
import org.apache.hadoop.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @author Lartimes
 */


public enum ImportRegularStrategy implements RegularExec<List<Object>> {

    DailyWeather {
        @Override
        public List<Object> exec(MultipartFile multipartFile) throws IOException {
            ArrayList<Object> weathers = new ArrayList<>();
            String fileName = "./" + UUID.randomUUID() + multipartFile.getName();
            try (OutputStream outputStream = new FileOutputStream(fileName)) {
                IOUtils.copyBytes(multipartFile.getInputStream(), outputStream, 1024);
            }
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
                bufferedReader.lines().forEach(line -> {
//                        2024-07-01	30	21	小雨	北风 3级
                    String[] values = line.split(",");
                    com.lartimes.weather.model.po.DailyWeather weather = new DailyWeather();
                    for (int i = 0; i < values.length; i++) {
                        switch (i) {
                            case 0 -> weather.setDay(dateConverter.convert(values[0]));
                            case 1 -> weather.setHighestTem(Integer.parseInt(values[1]));
                            case 2 -> weather.setLowestTem(Integer.parseInt(values[2]));
                            case 3 -> weather.setWeatherConditions(values[3]);
                            case 4 -> weather.setWindInfo(values[4]);
                            case 5 -> weather.setPosition(values[5]);
                            default -> throw new IllegalArgumentException("Invalid day");
                        }
                    }
                    weathers.add(weather);
                });
            } finally {
                File file = new File(fileName);
                file.delete();
            }
            return weathers;
        }
    }, Others {
        @Override
        public List<Object> exec(MultipartFile multipartFile) {
            return null;
        }
    }


}





