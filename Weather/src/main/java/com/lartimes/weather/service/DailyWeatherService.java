package com.lartimes.weather.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lartimes.weather.common.RestResponse;
import com.lartimes.weather.model.po.DailyWeather;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 某一天天气 服务类
 * </p>
 *
 * @author lartimes
 * @since 2024-07-01
 */
public interface DailyWeatherService extends IService<DailyWeather> {

    /**
     * 根据文件格式 读取内容导入DB
     *
     * @param multipartFile
     * @return
     */
    RestResponse importByFile(MultipartFile multipartFile, String strategy);


    /**
     * MapReduce 流式处理
     * @param multipartFile
     * @return
     */
    RestResponse readFileToDbByMp(MultipartFile multipartFile);


    /**
     * 批量插入Weathe
     *
     * @param weathers
     * @return
     */
    RestResponse batchInsert(List weathers);

}
