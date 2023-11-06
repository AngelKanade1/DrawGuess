package com.example.drawtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawingView extends View {
    private Path drawPath;
    private Paint drawPaint, canvasPaint,clearPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float startX, startY, endX, endY;
    private boolean isBigErase;

    public Bitmap getCanvasBitmap(){
        return canvasBitmap;
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        setupDrawing();
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        if(isBigErase){
            drawCanvas.drawRect(startX, startY, endX, endY, clearPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isBigErase) drawPath.lineTo(touchX, touchY);
                else {
                    drawPath.reset();
                    float rectLeft = Math.min(startX, touchX);
                    float rectTop = Math.min(startY, touchY);
                    float rectRight = Math.max(startX, touchX);
                    float rectBottom = Math.max(startY, touchY);
                    drawPath.addRect(rectLeft, rectTop, rectRight, rectBottom, Path.Direction.CW);
                }
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                endY = event.getY();
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                if (isBigErase)invalidate();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        drawPaint.setColor(Color.parseColor(newColor));
    }

    public void setErase(int isErase) {
        if (isErase == 2) {
            isBigErase = false;
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else if (isErase == 1){
            isBigErase = false;
            drawPaint.setXfermode(null);
        } else if (isErase == 3) {
            isBigErase = true;
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }

    public void clearCanvas() {
        drawCanvas.drawColor(Color.WHITE);
        invalidate();
    }
}