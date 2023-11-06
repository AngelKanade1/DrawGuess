package com.example.drawtest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class MyWebSocketClient extends AppCompatActivity {
    private Socket socket;
    private String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        listenSocketMessage();
    }

    public void listenSocketMessage() {
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 处理接收到的消息
                String message = (String) args[0];

                switch (message){
                    case "gamestart" :sendBroadcastMessage("com.example.ACTION_SOCKET_MAIN", "gamestart");break;
                    case "startguess":sendBroadcastMessage("com.example.ACTION_SOCKET_MAIN", "startguess");break;
                    case "startdraw":sendBroadcastMessage("com.example.ACTION_SOCKET_START_DRAW", "startdraw");break;
                    case "endgame":sendBroadcastMessage("com.example.ACTION_SOCKET_START_DRAW", "endgame");break;
                }
            }
        });
    }

    private void sendBroadcastMessage(String action, String data) {
        Intent intent = new Intent(action);
        intent.putExtra("data", data);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public void socketConnect() {
        {
            try {
                socket = IO.socket("http://10.28.69.186:5000/test");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
            // now connect to socket io
            socket.connect();
        }
    }

    public void sendSocketMessage(String message) {
        socket.emit("message", message);
    }

    public void loginSocketMessage(String message) {
        socket.emit("loginbind", message);
    }

    public String sendPostMessage(String route,FormBody.Builder formBuilder) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://10.28.69.186:5000/"+route)
                .post(formBuilder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    result = "-1";
                } else {
                    // parse response
                    String responseData = response.body().string();
                    result = responseData;
                }
            }
        });
        return result;
    }
}