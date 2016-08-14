package com.example.xh.customball;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
    private Paint progressPaint;
    private String centerText = "";
    private int centerTextColor;
    private float centerTextSize;
    private int ballColor;
    private int progressColor;
    private int progress = 50;
    private int currentProgress = 0;
    private int maxProgress = 100;
    private float radius;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Path path = new Path();
    private SingleTapThread singleTapThread;
    private GestureDetector detector;
    private int space=30;

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
        ballColor = typedArray.getColor(R.styleable.customBallView_ballColor, 0x3A8C6C);
        progressColor = typedArray.getColor(R.styleable.customBallView_progressColor, 0x00ff00);
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

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(progressColor);
        //取两层绘制交集。显示上层
        progressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));


        fontPaint.setTextSize(centerTextSize);
        fontPaint.setColor(centerTextColor);
        fontPaint.setAntiAlias(true);
        fontPaint.setFakeBoldText(true);

        bitmap = Bitmap.createBitmap((int) radius * 2, (int) radius * 2, Bitmap.Config.ARGB_8888);

        bitmapCanvas = new Canvas(bitmap);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int w;
        int h;
        if (widthMode == MeasureSpec.EXACTLY) {
            w = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            w = (int) Math.min(widthSize, radius * 2);
        } else {

            w = windowWidth;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            h = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            h = (int) Math.min(heightSize, radius * 2);
        } else {
            h = windowHeight;
        }
        setMeasuredDimension(w, h);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();
        bitmapCanvas.drawCircle(width / 2, height / 2, radius, roundPaint);

        path.reset();
        float y = (1 - (float) currentProgress / maxProgress) * radius * 2 + height / 2 - radius;
        path.moveTo(width, y);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, y);
        int count = (int) (radius + 1) * 2 / space;
        float d = (1 - (float) currentProgress / maxProgress) *space;
        for (int i = 0; i < count; i++) {
            path.rQuadTo(space, -d, space * 2, 0);
            path.rQuadTo(space, d, space * 2, 0);
        }
        path.close();
        bitmapCanvas.drawPath(path, progressPaint);
        String text = currentProgress + "%";
        float textWidth = fontPaint.measureText(centerText);
        Paint.FontMetrics fontMetrics = fontPaint.getFontMetrics();
        float x = width / 2 - textWidth / 2;
        float dy = -(fontMetrics.descent + fontMetrics.ascent) / 2;
        float y1 = height / 2 + dy;
        bitmapCanvas.drawText(text, x, y1, fontPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
        setClickable(true);
        if (detector==null){
            detector = new GestureDetector(new MyGestureDetector());
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return detector.onTouchEvent(event);
                }
            });

        }

    }


    public class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            getHandler().removeCallbacks(singleTapThread);
            singleTapThread=null;
            Snackbar.make(CustomBall.this, "暂停进度，是否重置进度？", Snackbar.LENGTH_LONG).setAction("重置", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentProgress=0;
                    invalidate();
                }
            }).show();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Snackbar.make(CustomBall.this, "单机了", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            startProgressAnimation();
            return super.onSingleTapConfirmed(e);
        }
    }

    private void startProgressAnimation() {
        if (singleTapThread == null) {
            singleTapThread = new SingleTapThread();
            getHandler().postDelayed(singleTapThread, 100);
        }
    }

    private class SingleTapThread implements Runnable {
        @Override
        public void run() {
            if (currentProgress < maxProgress) {
                invalidate();
                getHandler().postDelayed(singleTapThread, 100);
                currentProgress++;
            } else {
                getHandler().removeCallbacks(singleTapThread);
            }
        }
    }
}
