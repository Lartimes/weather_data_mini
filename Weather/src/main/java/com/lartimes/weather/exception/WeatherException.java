package com.lartimes.weather.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/2/8 10:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherException extends RuntimeException {
    private String errMessage;

    public static void cast(CommonError commonError) {
        throw new WeatherException(commonError.getErrMessage());
    }

    public static void cast(String errMessage) {
        throw new WeatherException(errMessage);
    }
}
