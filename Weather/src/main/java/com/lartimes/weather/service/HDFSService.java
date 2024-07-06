package com.lartimes.weather.service;

import java.util.List;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/3 18:46
 */
public interface HDFSService {


    List<String> getAllTables();


    void deleteTablesDir();


}
