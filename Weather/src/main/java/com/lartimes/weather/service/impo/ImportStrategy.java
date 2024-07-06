package com.lartimes.weather.service.impo;

import org.springframework.stereotype.Component;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/2 10:15
 */
@Component
public class ImportStrategy {

    public static final String DAILY_WEATHER = ImportRegularStrategy.DailyWeather.name();
    public ImportRegularStrategy weatherRegular = ImportRegularStrategy.DailyWeather;
    public ImportRegularStrategy othersR = ImportRegularStrategy.Others;
    public ImportNregularStrategy weatherNRegular = ImportNregularStrategy.DailyWeather;
    public ImportNregularStrategy othersNR = ImportNregularStrategy.Others;


}
