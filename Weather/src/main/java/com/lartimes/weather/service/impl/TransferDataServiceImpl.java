package com.lartimes.weather.service.impl;

import com.lartimes.weather.common.TcpClientUtil;
import com.lartimes.weather.service.TransferDataService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Lartimes
 * @version 1.0
 * @description:
 * @since 2024/7/3 0:05
 */
@Service
@Slf4j
@Configuration
@ConfigurationProperties("hadoop.hdfs.transfer")
public class TransferDataServiceImpl implements TransferDataService {



    @Value("${spring.datasource.url}")
    private String url;
    @Value("${hadoop.hdfs.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${hadoop.hdfs.transfer.table}")
    private String table;
    @Setter
    @Getter
    private List<String> columns;
    @Value("${hadoop.hdfs.transfer.dest}")
    private String dest;



    @Override
    public void transferToDB(String input) {
        StringBuilder sb = new StringBuilder();
        StringBuilder columnStr = new StringBuilder();
        for (String column : columns) {
            columnStr.append(column);
            columnStr.append(",");
        }
        String cols = columnStr.substring(0, columnStr.length() - 1);
        String tmp = url.split("\\?")[0];
        sb.append("sqoop export --connect '").append(tmp).append("?userSSL=false' ")
                .append("--username '").append(username).append("' ")
                .append("--password '")
                .append(password).append("' ")
                .append("--table '").append(table).append("' ")
                .append("--num-mappers '1' ")
                .append("--columns '").append(cols).append("' ");
        if (StringUtils.hasText(input)) {
            sb.append("\t").append("--export-dir '").append(input).append("' ");
        }
        TcpClientUtil.request(Map.of("EXPORT", sb.toString()), dest);
    }

    @Override
    public void transferToHDFS(String output) {
        StringBuilder sb = new StringBuilder();
        String tmp = url.split("\\?")[0];
        sb.append("sqoop import-all-tables --connect ")
                .append(tmp)
                .append('\t')
                .append("--username ").append(username).append('\t')
                .append("--password ").append(password).append('\t')
                .append("--m 1 ");
        if (StringUtils.hasText(output)) {
            sb.append("--target-dir ").append(output);
        }
        TcpClientUtil.request(Map.of("IMPORTALLTABLES", sb.toString()), dest);
    }


}
