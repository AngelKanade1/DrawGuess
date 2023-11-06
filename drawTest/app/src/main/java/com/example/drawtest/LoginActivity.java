package com.example.drawtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity {
    private OkHttpClient client;
    private WebSocketClient mWebSocketClient;
    private EditText editTextUserInput;
    private MyWebSocketClient myWebSocketClient;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        myWebSocketClient = new MyWebSocketClient();
        editTextUserInput = findViewById(R.id.editTextUserInput);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String input = editTextUserInput.getText().toString();
                id = input;
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("id", input);
                String loginResult = myWebSocketClient.sendPostMessage("login",formBuilder);
                if (loginResult != null && loginResult.equals("true")) {
                    connectWebSocket();
                    Context context = LoginActivity.this;
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("id", input);
                    intent.putExtra("isStart", false);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("登陆失败")
                            .setMessage("登陆失败")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void connectWebSocket() {
        Log.d("LoginActivity", "Connect");
        myWebSocketClient.socketConnect();
        myWebSocketClient.loginSocketMessage(id);
    }
}