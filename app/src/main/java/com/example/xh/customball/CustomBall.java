package com.example.xh.customball;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by xiehui on 2016/8/10.
 */
public class CustomBall extends View {
    private int width;
    private int height;

    private int windowWidth;
    private int windowHeight;
    private Paint roundPaint;
    private Paint fontPaint;
    private String centerText = "";
    private int centerTextColor;
    private float centerTextSize;
    private int ballColor;
    private float radius;

    public CustomBall(Context context) {
        this(context, null);
    }

    public CustomBall(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        windowWidth = getResources().getDisplayMetrics().widthPixels;
        windowHeight = getResources().getDisplayMetrics().heightPixels;
        getCustomAttribute(context, attrs, defStyleAttr);
        initPaint();
    }


    /**
     * 获取自定义属性值
     */

    private void getCustomAttribute(Context context, AttributeSet attrs, int defStyleAttr) {

        /**
         * 获取自定义属性
         */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.customBallView);
        centerText = typedArray.getString(R.styleable.customBallView_centerText);
        Log.e("TAG", "centerText" + centerText);
        centerTextSize = typedArray.getDimension(R.styleable.customBallView_centerTextSize, 24f);
        centerTextColor = typedArray.getColor(R.styleable.customBallView_centerTextColor, 0xFFFFFF);
        ballColor = typedArray.getColor(R.styleable.customBallView_ballColor, 0xFF4081);
        radius = typedArray.getDimension(R.styleable.customBallView_ballRadius, 260f);
        radius = Math.min(Math.min(windowWidth / 2, windowHeight / 2), radius);
        typedArray.recycle();

    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        roundPaint = new Paint();
        roundPaint.setColor(ballColor);
        roundPaint.setAntiAlias(true);
        fontPaint = new Paint();
        fontPaint.setTextSize(centerTextSize);
        fontPaint.setColor(centerTextColor);
        fontPaint.setAntiAlias(true);
        fontPaint.setFakeBoldText(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width=widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width=(int)Math.min(widthSize,radius*2);
        } else {

            width=windowWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height=heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height=(int)Math.min(heightSize,radius*2);
        } else {
            height=windowHeight;
        }
        setMeasuredDimension(width,height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();
        canvas.drawCircle(width / 2, height / 2, radius, roundPaint);
        float textWidth = fontPaint.measureText(centerText);
        float x = width / 2 - textWidth / 2;
        Paint.FontMetrics fontMetrics = fontPaint.getFontMetrics();
        float dy = -(fontMetrics.descent + fontMetrics.ascent) / 2;
        float y = height / 2 + dy;
        canvas.drawText(centerText, x, y, fontPaint);


    }

}