package com.lartimes.weather.service.impl;

import com.lartimes.weather.service.HDFSService;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/3 19:00
 */
@Service
public class HDFSServiceImpl implements HDFSService {

    private final DataSource dataSource;
    private final FileSystem fileSystem;
    @Value("${hadoop.hdfs.username}")
    private String usr;

    public HDFSServiceImpl(DataSource dataSource, FileSystem fileSystem) {
        this.dataSource = dataSource;
        this.fileSystem = fileSystem;
    }

    @Override
    public List<String> getAllTables() {
        List<String> tableNames = new ArrayList<>();
        String base = "/user/" + usr + "/";
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables("weather", null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tableNames.add(base + tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableNames;
    }

    @Override
    public void deleteTablesDir( ) {
        List<String> allTables = getAllTables();
        allTables.forEach(tableDir -> {
            try {
                fileSystem.delete(new Path(tableDir), true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
