package com.example.drawtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.StandardCharsets;

import okhttp3.FormBody;

public class GuessActivity extends AppCompatActivity {
    private MyWebSocketClient myWebSocketClient;
    private EditText editTextUserInput;

    private String id;
    private boolean isStart;

    private void jumpToDraw(){
        Intent intent = new Intent(this, GuessActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isStart", isStart);
        startActivity(intent);
    }

    private void jumpToEnd(){
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isStart", isStart);
        startActivity(intent);
    }

    private final BroadcastReceiver socketMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 检查广播是否是你发送的特定广播
            if ("com.example.ACTION_SOCKET_START_DRAW".equals(action)) {
                String message = intent.getStringExtra("data");
                if (message.equals("startdraw")) {
                    jumpToDraw();
                } else{
                    jumpToEnd();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.example.ACTION_SOCKET_START_DRAW");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(socketMessageReceiver, filter);
    }

    // 取消注册广播接收器
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(socketMessageReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_activity);
        editTextUserInput = findViewById(R.id.editTextUserInput);
        Button buttonLogin = findViewById(R.id.buttonSend);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String input = editTextUserInput.getText().toString();
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("id", id);
                formBuilder.add("guess", input);
                String loginResult = myWebSocketClient.sendPostMessage("guess",formBuilder);
            }
        });
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("id", id);
        String stringArray = myWebSocketClient.sendPostMessage("getbitmap",formBuilder);
        byte[] byteArray = stringArray.getBytes(StandardCharsets.UTF_8);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }

}
