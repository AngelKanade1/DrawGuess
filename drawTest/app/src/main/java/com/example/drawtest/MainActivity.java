package com.example.drawtest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private DrawingView drawingView;
    private Spinner colorSpinner;
    private Button toggleEraser;
    private int isEraserActive = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawingView = findViewById(R.id.drawingView);
        colorSpinner = findViewById(R.id.colorSpinner);
        Button clearCanvas = findViewById(R.id.clearCanvas);
        toggleEraser = findViewById(R.id.toggleEraser);

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
}