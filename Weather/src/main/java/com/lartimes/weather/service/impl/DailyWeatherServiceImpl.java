package com.lartimes.weather.service.impl;

import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lartimes.weather.common.FileTypeChecker;
import com.lartimes.weather.common.ReadExcelUtil;
import com.lartimes.weather.common.RestResponse;
import com.lartimes.weather.exception.WeatherException;
import com.lartimes.weather.mapper.DailyWeatherMapper;
import com.lartimes.weather.model.po.DailyWeather;
import com.lartimes.weather.service.DailyWeatherService;
import com.lartimes.weather.service.drivers.MysqlWeatherDriver;
import com.lartimes.weather.service.impo.ImportStrategy;
import lombok.SneakyThrows;
import org.apache.hadoop.io.IOUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

/**
 * <p>
 * 某一天天气 服务实现类
 * </p>
 *
 * @author lartimes
 * @since 2024-07-01
 */
@Service
public class DailyWeatherServiceImpl extends ServiceImpl<DailyWeatherMapper, DailyWeather> implements DailyWeatherService {

    private final ReadExcelUtil<DailyWeather> readExcelUtil;
    private final SqlSessionFactory sqlSessionFactory;
    private final MysqlWeatherDriver mysqlWeatherDriver;

    private final ImportStrategy dailyWeather;

    public DailyWeatherServiceImpl(ReadExcelUtil<DailyWeather> readExcelUtil, SqlSessionFactory sqlSessionFactory, MysqlWeatherDriver mysqlWeatherDriver, ImportStrategy dailyWeather) {
        this.readExcelUtil = readExcelUtil;
        this.sqlSessionFactory = sqlSessionFactory;
        this.mysqlWeatherDriver = mysqlWeatherDriver;
        this.dailyWeather = dailyWeather;
    }

    @Override
    public RestResponse importByFile(MultipartFile multipartFile, String strategy) {
        List weathers = null;
        try {
            if (FileTypeChecker.isTextFile(multipartFile)) {
                weathers = dailyWeather.weatherRegular.exec(multipartFile);
            } else {
                weathers = readExcelUtil.getExcelInfo(multipartFile, dailyWeather.weatherNRegular);
            }
        } catch (IOException e) {

            try {
                weathers = dailyWeather.weatherRegular.exec(multipartFile);
            } catch (IOException ex) {
                ex.printStackTrace();
                WeatherException.cast(ex.getMessage());
            }
        }
        if (weathers == null) {
            RestResponse.validfail("未能正确读取数据");
        }
        List<DailyWeather> arr = weathers.stream().filter(weather -> weather instanceof DailyWeather).map(weather -> weather).toList();
        return batchInsert(arr);
    }

    @SneakyThrows
    @Override
    public RestResponse readFileToDbByMp(MultipartFile multipartFile) {
        String fileName = "./" + UUID.randomUUID() + multipartFile.getOriginalFilename();
        try (OutputStream outputStream = new FileOutputStream(fileName)) {
            IOUtils.copyBytes(multipartFile.getInputStream(), outputStream, 1024);
        }
        File file = null;
        RestResponse response = null;
        try {
            file = new File(fileName);
            String uri = file.toURI().toString();
            response = mysqlWeatherDriver.start(new String[]{uri});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert file != null;
            file.delete();
            if (response != null) {
                return response;
            }
            return RestResponse.validfail("未成功");

        }
    }


    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public RestResponse batchInsert(List weathers) {
        MybatisBatch<DailyWeather> mybatisBatch = new MybatisBatch<DailyWeather>(sqlSessionFactory, weathers);
        MybatisBatch.Method<DailyWeather> method = new MybatisBatch.Method<>(DailyWeatherMapper.class);
        mybatisBatch.execute(method.insert());
        return RestResponse.success();
    }
}
