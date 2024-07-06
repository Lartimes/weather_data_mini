package com.lartimes.weather.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lartimes.weather.model.po.DailyWeather;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 某一天天气 Mapper 接口
 * </p>
 *
 * @author lartimes
 * @since 2024-07-01
 */
@Mapper
public interface DailyWeatherMapper extends BaseMapper<DailyWeather> {

}
