package com.lartimes.weather.controller;


import com.lartimes.weather.common.RestResponse;
import com.lartimes.weather.service.DailyWeatherService;
import com.lartimes.weather.service.HDFSService;
import com.lartimes.weather.service.TransferDataService;
import com.lartimes.weather.service.impo.ImportStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 某一天天气 前端控制器
 * </p>
 *
 * @author lartimes
 * @since 2024-07-01
 */
@Slf4j
@RestController
@RequestMapping("/weather")
@Tag(name = "DailyWeatherController", description = "Weather controller")
public class DailyWeatherController {

    private final DailyWeatherService dailyWeatherService;
    private final HDFSService hdfsService;
    private final TransferDataService transferDataService;

    public DailyWeatherController(DailyWeatherService dailyWeatherService, HDFSService hdfsService, TransferDataService transferDataService) {
        this.dailyWeatherService = dailyWeatherService;
        this.hdfsService = hdfsService;
        this.transferDataService = transferDataService;
    }

    /**
     * 1.txt
     * 本地流
     * mapreduce
     * 2.xls,cvs...
     * 策略模式读取XLS写入DB
     *
     * @param multipartFile
     * @return
     */
    @Operation(summary = "上传文件,导入MYSQL")
    @PostMapping("/importFl")
    public RestResponse importByFile(@RequestParam("multipartFile") MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        System.out.println(originalFilename);
        return dailyWeatherService.importByFile(multipartFile, ImportStrategy.DAILY_WEATHER);
    }

    @Operation(summary = "上传文件,MapReduce 流式处理导入MYSQL")
    @PostMapping("/importMp")
    public RestResponse importByMp(@RequestParam("multipartFile") MultipartFile multipartFile) {
        System.out.println(multipartFile.getName());
        return dailyWeatherService.readFileToDbByMp(multipartFile);
    }

    @Operation(summary = "MYSQL <--> HDFS ")
    @GetMapping("/transfer/{startEnd}/{putStr}")
    public RestResponse transFromDBToHdfs(@PathVariable("startEnd") String start, @PathVariable("putStr") String putStr) {
        if ("default".equalsIgnoreCase(putStr)) {
            putStr = null;
        } else {
            putStr = putStr.replaceAll(",", "/");
        }
        if ("HDFS".equals(start)) {
            transferDataService.transferToDB(putStr);
        } else if ("DB".equals(start)) {
            hdfsService.deleteTablesDir();
            transferDataService.transferToHDFS(putStr);
        }
        return RestResponse.success();
    }

}
