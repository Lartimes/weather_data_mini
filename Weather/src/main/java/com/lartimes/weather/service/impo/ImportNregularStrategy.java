package com.lartimes.weather.service.impo;

import com.lartimes.weather.model.po.DailyWeather;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;



public enum ImportNregularStrategy implements NregularExec<Object> {
    DailyWeather {
        @Override
        public Object exec(Row row, int totalCells) {
            DailyWeather weather = new DailyWeather();
            for (int c = 0; c < totalCells - 1; c++) {
                Cell cell = row.getCell(c);
                if (null != cell) {
                    if (c == 0) {           //第一列
                        weather.setId((long) cell.getNumericCellValue());//将单元格数据赋值给user
                    } else if (c == 1) {
                        weather.setDay(dateConverter.convert(cell.getStringCellValue()));
                    } else if (c == 2) {
                        weather.setHighestTem((int) cell.getNumericCellValue());
                    } else if (c == 3) {
                        weather.setLowestTem((int) cell.getNumericCellValue());
                    } else if (c == 4) {
                        weather.setWeatherConditions(cell.getStringCellValue());
                    } else if (c == 5) {
                        weather.setWindInfo(cell.getStringCellValue());
                    } else {
                        weather.setPosition(cell.getStringCellValue());
                    }
                }
            }
            return weather;
        }
    }, Others {
        @Override
        public Object exec(Row row, int totalCells) {
            return null;
        }
    }


}





