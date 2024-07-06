package com.lartimes.weather.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 某一天天气
 * </p>
 *
 * @author lartimes
 * @since 2024-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DailyWeather implements Serializable {


    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 日期
     */
    private LocalDate day;

    /**
     * 最高温度
     */
    private Integer highestTem;

    /**
     * 最低温度
     */
    private Integer lowestTem;

    /**
     * 天气状况
     */
    private String weatherConditions;

    /**
     * 风力信息
     */
    private String windInfo;

    /**
     * 位置
     */
    private String position;


}
