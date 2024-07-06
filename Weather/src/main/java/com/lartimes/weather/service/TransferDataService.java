package com.lartimes.weather.service;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/2 23:56
 */
public interface TransferDataService {

    /***
     * 从HDFS导入MySQL
     * @param input
     */
    void transferToDB(String input);

    /**
     * 从MySQL导入HDFS
     * @param output
     */
    void transferToHDFS(String output );

}
