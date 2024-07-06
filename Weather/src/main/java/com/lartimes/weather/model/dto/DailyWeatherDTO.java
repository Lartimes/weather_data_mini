package com.lartimes.weather.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * 某一天天气
 * </p>
 *
 * @author lartimes
 * @since 2024-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("daily_weather")
public class DailyWeatherDTO implements DBWritable, Writable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String day;

    private Integer highestTem;

    private Integer lowestTem;

    private String weatherConditions;

    private String windInfo;

    private String position;


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(day);
        out.writeInt(highestTem);
        out.writeInt(lowestTem);
        out.writeUTF(weatherConditions);
        out.writeUTF(windInfo);
        out.writeUTF(position);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.day = in.readUTF();
        this.highestTem = in.readInt();
        this.lowestTem = in.readInt();
        this.weatherConditions = in.readUTF();
        this.windInfo = in.readUTF();
        this.position = in.readUTF();
    }

    @Override
    public void write(PreparedStatement ps) throws SQLException {
        ps.setString(1, day);
        ps.setInt(2, highestTem);
        ps.setInt(3, lowestTem);
        ps.setString(4, weatherConditions);
        ps.setString(5, windInfo);
        ps.setString(6, position);
    }

    @Override
    public void readFields(ResultSet rs) throws SQLException {
        this.day = rs.getString(1);
        this.highestTem = rs.getInt(2);
        this.lowestTem = rs.getInt(3);
        this.weatherConditions = rs.getString(4);
        this.windInfo = rs.getString(5);
        this.position = rs.getString(6);
    }


}
