package com.lartimes.weather.service.mapper;

import com.lartimes.weather.model.dto.DailyWeatherDTO;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/2 13:32
 */

public class MysqlWriterMapper extends Mapper<LongWritable, Text, NullWritable, DailyWeatherDTO> {

    private static final NullWritable NULL_WRITABLE = NullWritable.get();

    private final DailyWeatherDTO dailyWeather = new DailyWeatherDTO();


    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, NullWritable, DailyWeatherDTO>.Context context) throws IOException, InterruptedException {
        String[] values = value.toString().split(",");
        for (int i = 0; i < values.length; i++) {
            switch (i) {
                case 0 -> dailyWeather.setDay(values[0]);
                case 1 -> dailyWeather.setHighestTem(Integer.parseInt(values[1]));
                case 2 -> dailyWeather.setLowestTem(Integer.parseInt(values[2]));
                case 3 -> dailyWeather.setWeatherConditions(values[3]);
                case 4 -> dailyWeather.setWindInfo(values[4]);
                case 5 -> dailyWeather.setPosition(values[5]);
                default -> throw new IllegalStateException("Unexpected value: " + i);
            }
        } context.write(NULL_WRITABLE, dailyWeather);
    }
}
