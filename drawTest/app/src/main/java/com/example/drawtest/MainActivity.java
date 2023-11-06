package com.example.drawtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import okhttp3.FormBody;

public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private Spinner colorSpinner;
    private Button toggleEraser;

    private TextView printer;

    private String id;
    private boolean isStart;

    private MyWebSocketClient myWebSocketClient;
    private int isEraserActive = 1;

    private void jumpToGuess(){
        Intent intent = new Intent(this, GuessActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("isStart", isStart);
        startActivity(intent);
    }

    private final BroadcastReceiver socketMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 检查广播是否是你发送的特定广播
            if ("com.example.ACTION_SOCKET_MAIN".equals(action)) {
                String message = intent.getStringExtra("data");
                if (message.equals("gamestart")) {
                    isStart = true;
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    formBuilder.add("id", id);
                    String question = myWebSocketClient.sendPostMessage("getquestion",formBuilder);
                    printer.setText(question);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("你要画的是")
                            .setMessage(question)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {}
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else{
                    jumpToGuess();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.example.ACTION_SOCKET_START_GUESS");
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
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        isStart = intent.getBooleanExtra("isStart",false);
        myWebSocketClient = new MyWebSocketClient();
        setContentView(R.layout.activity_main);
        drawingView = findViewById(R.id.drawingView);
        colorSpinner = findViewById(R.id.colorSpinner);
        Button clearCanvas = findViewById(R.id.clearCanvas);
        toggleEraser = findViewById(R.id.toggleEraser);
        printer = findViewById(R.id.textView);
        Button doneButton = findViewById(R.id.done);

        setupColorSpinner();
        clearCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.clearCanvas();
            }
        });

        toggleEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEraserActive += 1;
                if (isEraserActive==4) isEraserActive = 1;
                drawingView.setErase(isEraserActive);
                switch (isEraserActive){
                    case 1:toggleEraser.setText("画笔");break;
                    case 2:toggleEraser.setText("橡皮");break;
                    case 3:toggleEraser.setText("大橡皮");break;
                }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStart) DoneDrawing();
            }
        });

        Button restButton = findViewById(R.id.rest);
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("id", id);
                String rest = myWebSocketClient.sendPostMessage("rest",formBuilder);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("别催啦")
                        .setMessage("还有"+rest+"个人在磨磨唧唧呢")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Button playersButton = findViewById(R.id.players);
        playersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("id", id);
                String players = myWebSocketClient.sendPostMessage("players",formBuilder);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("6")
                        .setMessage("现在有"+players+"在玩呢")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void setupColorSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.colors_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);
        colorSpinner.setSelection(0, false);
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedColor = adapterView.getItemAtPosition(position).toString();
                String set_color = "";
                if(selectedColor.equals("黑色")) set_color = "#000000";
                if(selectedColor.equals("红色")) set_color = "#FF0000";
                if(selectedColor.equals("绿色")) set_color = "#00FF00";
                if(selectedColor.equals("蓝色")) set_color = "#0000FF";
                if(selectedColor.equals("黄色")) set_color = "#FFFF00";
                if(selectedColor.equals("紫色")) set_color = "#FF00FF";
                if(selectedColor.equals("青色")) set_color = "#00FFFF";
                drawingView.setColor(set_color);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void DoneDrawing(){
        Bitmap bitmap = drawingView.getCanvasBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("id", id);
        formBuilder.add("bitmap", Arrays.toString(byteArray));
        String result = myWebSocketClient.sendPostMessage("bitmap",formBuilder);
    }
}