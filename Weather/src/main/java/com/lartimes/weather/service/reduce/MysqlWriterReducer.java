package com.lartimes.weather.service.reduce;

import com.lartimes.weather.model.dto.DailyWeatherDTO;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/2 13:32
 */
public class MysqlWriterReducer extends Reducer<NullWritable, DailyWeatherDTO, DailyWeatherDTO, NullWritable> {
    private static final NullWritable NULL_WRITABLE = NullWritable.get();

    @Override
    protected void reduce(NullWritable key, Iterable<DailyWeatherDTO> values, Reducer<NullWritable, DailyWeatherDTO, DailyWeatherDTO, NullWritable>.Context context) throws IOException, InterruptedException {
        for (DailyWeatherDTO value : values) {
            context.write(value, NULL_WRITABLE);
        }
    }
}
