package com.lartimes.weather.common;

import org.apache.hadoop.fs.FileSystem;
import org.springframework.stereotype.Component;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/1 22:35
 */
@Component
public class HDFSUtils {

    private final FileSystem fileSystem;

    public HDFSUtils(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

//    TODO:增删改查



}
