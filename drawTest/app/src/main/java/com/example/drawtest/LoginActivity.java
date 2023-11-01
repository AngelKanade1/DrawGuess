package com.example.drawtest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity {
    private OkHttpClient client;
    private WebSocketClient mWebSocketClient;
    private EditText editTextUserInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        client = new OkHttpClient();
        editTextUserInput = findViewById(R.id.editTextUserInput);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String input = editTextUserInput.getText().toString();
                sendPostRequest(input);
                connectWebSocket();
            }
        });
    }

    void sendPostRequest(String input) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), input);
        Request request = new Request.Builder()
                .url("http://127.0.0.1:5000/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Log.d("MainActivity", response.body().string());
            }
        });
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://127.0.0.1:5000/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("WebSocket", "Session is starting");
            }

            @Override
            public void onMessage(String s) {
                Log.d("WebSocket", "Message received: " + s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("WebSocket", "Closed ");
            }

            @Override
            public void onError(Exception e) {
                Log.d("WebSocket", "Error: " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}