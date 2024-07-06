package com.lartimes.weather.service.drivers;

import com.lartimes.weather.common.RestResponse;
import com.lartimes.weather.model.dto.DailyWeatherDTO;
import com.lartimes.weather.model.po.DailyWeather;
import com.lartimes.weather.service.mapper.MysqlWriterMapper;
import com.lartimes.weather.service.reduce.MysqlWriterReducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/2 13:42
 */
@Service
public class MysqlWeatherDriver extends Configured implements Tool {
    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClass;
    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/weather?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&allowPublicKeyRetrieval=true}")
    private String url;
    @Value("${spring.datasource.username:root}")
    private String usr;
    @Value("${spring.datasource.password:307314}")
    private String pwd;


    public MysqlWeatherDriver(@Value("${spring.datasource.driver-class-name}") String driverClass, @Value("${spring.datasource.url}") String url, @Value("${spring.datasource.username}") String usr, @Value("${spring.datasource.password}") String pwd) {
        this.driverClass = driverClass;
        this.url = url;
        this.usr = usr;
        this.pwd = pwd;
    }

    public RestResponse start(String[] dirs) throws Exception {
        Configuration conf = new Configuration();
        if (dirs.length == 0) {
            return RestResponse.validfail("请正确上传文件");
        }
        conf.set("mapreduce.framework.name", "local");
        int status = ToolRunner.run(conf, this, dirs);
        return RestResponse.success(status);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        DBConfiguration.configureDB(conf, driverClass, url, usr, pwd);
        Job job = Job.getInstance(conf, this.getClass().getSimpleName());
        job.setJarByClass(MysqlWeatherDriver.class);
        URI uri = URI.create(args[0]);
        FileInputFormat.setInputPaths(job, new Path(uri));
        job.setMapperClass(MysqlWriterMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(DailyWeatherDTO.class);
        job.setReducerClass(MysqlWriterReducer.class);
        job.setOutputKeyClass(DailyWeather.class);
        job.setOutputValueClass(NullWritable.class);
        job.setOutputFormatClass(DBOutputFormat.class);
        DBOutputFormat.setOutput(job, "daily_weather_hdfs",
                 "day", "highest_tem", "lowest_tem", "weather_conditions", "wind_info", "position");
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
