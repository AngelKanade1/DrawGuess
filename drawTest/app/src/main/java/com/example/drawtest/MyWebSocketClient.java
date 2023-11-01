package com.example.drawtest;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;

public class MyWebSocketClient extends WebSocketClient {

    public MyWebSocketClient(String serverUrl) throws URISyntaxException {
        super(new URI(serverUrl));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("WebSocket连接已打开");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("收到消息：" + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket连接已关闭");
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void main(String[] args) {
        try {
            String serverUrl = "ws://127.0.0.1:5000/socket"; // Flask WebSocket 服务器地址
            MyWebSocketClient client = new MyWebSocketClient(serverUrl);
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}