package com.lartimes.weather.common;

import com.lartimes.weather.model.po.DailyWeather;
import com.lartimes.weather.service.impo.ImportNregularStrategy;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/6/23 9:08
 */
@Component
public class ReadExcelUtil<T> {

    //总行数
    private static int totalRows = 0;
    //总条数
    private static int totalCells = 0;

    /**
     * 验证EXCEL文件
     *
     * @param filePath
     * @return
     */
    public static boolean validateExcel(String filePath) {
        return filePath != null && (isExcel2003(filePath) || isExcel2007(filePath));
    }

    // @描述：是否是2003的excel，返回true是2003
    public static boolean isExcel2003(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    //@描述：是否是2007的excel，返回true是2007
    public static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 读EXCEL文件，获取信息集合
     *
     * @return
     */
    public List<T> getExcelInfo(MultipartFile mFile, ImportNregularStrategy importNregularStrategy) {
        String fileName = mFile.getOriginalFilename();//获取文件名
        try {
            if (!validateExcel(fileName)) {// 验证文件名是否合格
                return null;
            }
            boolean isExcel2003 = true;// 根据文件名判断文件是2003版本还是2007版本
            assert fileName != null;
            if (isExcel2007(fileName)) {
                isExcel2003 = false;
            }
            String name = importNregularStrategy.name();
            return createExcel(mFile.getInputStream(), isExcel2003, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据excel里面的内容读取客户信息
     *
     * @param is          输入流
     * @param isExcel2003 excel是2003还是2007版本
     * @return
     * @throws IOException
     */
    public List<T> createExcel(InputStream is, boolean isExcel2003, String name) {
        try {
            Workbook wb = null;
            if (isExcel2003) {// 当excel是2003时,创建excel2003
                wb = new HSSFWorkbook(is);
            } else {// 当excel是2007时,创建excel2007
                wb = new XSSFWorkbook(is);
            }
            return readExcelValue(wb, name);// 读取Excel里面客户的信息
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取Excel里面客户的信息
     *
     * @param wb
     * @return
     */
    private List<T> readExcelValue(Workbook wb, String name) {
        List results;
        if ("DailyWeather".equalsIgnoreCase(name)) {
            results = new ArrayList<DailyWeather>();
        } else {
            results = new ArrayList<Object>();
        }
        //默认会跳过第一行标题
        // 得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        // 得到Excel的行数
        totalRows = sheet.getPhysicalNumberOfRows();
        // 得到Excel的列数(前提是有行数)
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }


//        策略
        // 循环Excel行数
        boolean flag = "DailyWeather".equalsIgnoreCase(name);
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
//            row
            if (flag) {
                results.add(ImportNregularStrategy.DailyWeather.exec(row, totalCells));
            } else {
                results.add(ImportNregularStrategy.Others.exec(row, totalCells));
            }
        }
        return results;
    }
}
