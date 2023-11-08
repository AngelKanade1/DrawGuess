package com.example.drawtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import okhttp3.FormBody;

public class EndActivity extends AppCompatActivity {
    private MyWebSocketClient myWebSocketClient;
    private TextView printer;

    private JSONObject playerInfo;

    private String[][] show;
    private String[] playersIndex;

    private int round,nowIndex,nowProgress;

    private String id;
    private boolean isStart;

    private static JSONObject convertToJson(String input) throws JSONException {
        // 去除字符串中的单引号
        String cleanedInput = input.replace("'", "\"");

        JSONObject json;
        json = new JSONObject(cleanedInput);
        return json;
    }


    private void nextStep(){
        String set_text = "词语是"+show[nowIndex][nowProgress]+"\n"+"猜的结果是"+show[nowIndex][nowProgress+2];
        printer.setText(set_text);
        showBitmap(show[nowIndex][nowProgress+1]);
        nowProgress+=2;
        if (nowProgress>=round*2+1) {
            nowProgress = 0;
            nowIndex += 1;
        }
        if (nowIndex==playersIndex.length){
            AlertDialog.Builder builder = new AlertDialog.Builder(EndActivity.this);
            builder.setTitle("结束")
                    .setMessage("游戏结束")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void showBitmap(String stringArray){
        byte[] byteArray = stringArray.getBytes(StandardCharsets.UTF_8);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }

    private final BroadcastReceiver socketMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 检查广播是否是你发送的特定广播
            if ("com.example.ACTION_SOCKET_END_CHECK".equals(action)) {
                nextStep();
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
        nowIndex = 0;
        nowProgress = 0;
        setContentView(R.layout.end_activity);
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("id", id);
        String infoString = myWebSocketClient.sendPostMessage("getinfo",formBuilder);
        try {
            playerInfo = convertToJson(infoString);
            playersIndex = playerInfo.getString("index").split(",");
            int len = playersIndex.length;
            int temp = 0;
            round = playersIndex.length/2;
            show = new String[len][round*2+1];
            for (String index:playersIndex){
                JSONObject playerJson = (JSONObject) playerInfo.get(index);
                show[temp][0]=playerJson.getString("question");
                for (int r = 1;r<=round;r++){
                    if (temp-(2*r-1)+1<0){
                        show[temp-(2*r-1)+len+1][r*2-1]=playerJson.getString(r+"bitmap");
                    }else{
                        show[temp-(2*r-1)+1][r*2-1]=playerJson.getString(r+"bitmap");
                    }
                    if (temp-(2*r-1)<0){
                        show[len-(2*r-1)+temp][r*2] = playerJson.getString(r+"guess");
                    }
                    else{
                        show[temp-(2*r-1)][r*2]=playerJson.getString(r+"guess");
                    }
                }
                temp ++;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        printer = findViewById(R.id.printer);
        Button buttonLogin = findViewById(R.id.buttonSend);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("id", id);
                myWebSocketClient.sendPostMessage("checktimes",formBuilder);
            }
        });
    }
}
