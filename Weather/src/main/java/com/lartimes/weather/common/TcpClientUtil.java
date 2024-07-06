package com.lartimes.weather.common;

import com.lartimes.weather.exception.WeatherException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class TcpClientUtil {
    private static final int PORT = 12345;


    public static void request(Map<String, String> map , String host) {
        try (Socket socket = new Socket(host, PORT);
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream())) {
            outputStream.writeObject(map);
        } catch (IOException e) {
            WeatherException.cast("连接失败");
        }
    }

}
