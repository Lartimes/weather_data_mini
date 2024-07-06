package com.lartimes.weather.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

enum SQOOP implements Exec {
    EXPORT, IMPORTALLTABLES

}

interface Exec {
    default void exec(String str) throws IOException {
        File file = new File("tmp.sh");
        if (!file.exists()) {
            file.createNewFile();
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(str.getBytes());
            outputStream.flush();
        }
        String path = file.getPath();
        Runtime.getRuntime().exec("sh " + path);
    }

}

public class TcpServer {
    private static final int PORT = 12345;

    private static final Map<String, Exec> map = new HashMap<>();

    static {
        map.put("EXPORT", SQOOP.EXPORT);
        map.put("IMPORTALLTABLES", SQOOP.IMPORTALLTABLES);
    }

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Connected to " + clientSocket.getInetAddress().getHostAddress());
                    // 读取数据
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    Map<String, String> map = (Map<String, String>) inputStream.readObject();
                    String key = map.keySet().stream().toList().get(0);
                    Exec exec = TcpServer.map.getOrDefault(key, SQOOP.EXPORT);
                    exec.exec(map.get(key));
                    System.out.println("开始执行");
                } catch (Exception ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
