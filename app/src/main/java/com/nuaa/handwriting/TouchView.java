package com.nuaa.handwriting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nuaa.handwriting.model.PointBean;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/*
 *  自定义 View 类
 * */
public class TouchView extends View {

    private float currentX;
    private float currentY;
    private float currentPress;
    private Paint paint;
    private Path path;
    private ArrayList<PointBean> mPointList = new ArrayList<>();

    private CountDownTimer countDownTimer;

    private boolean isStart = false;

    private Timer mTimer = new Timer();

    private MyTimerTask mTask;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        path = new Path();
        countDownTimer = new CountDownTimer(3 * 1000, 20) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Log.e("xupeng", "mills" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
//                Log.e("xupeng", "3s结束");
//                String temp;
                mTask.cancel();
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = event.getX();
        currentY = event.getY();
        currentPress = event.getPressure();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isStart) {
                    isStart = true;
                    mTask = new MyTimerTask();
                    mTimer.schedule(mTask, 0, 20);
                    countDownTimer.start();
                }
                path.moveTo(currentX, currentY);
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                path.lineTo(currentX, currentY);
                break;
        }
        postInvalidate();
        return true;
    }

    public void reset() {
        if (path != null) {
            path.reset();
        }
        isStart = false;
        countDownTimer.onFinish();
        countDownTimer.cancel();
        mPointList.clear();
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            PointBean pointBean = new PointBean(currentX, currentY, currentPress);
            mPointList.add(pointBean);
        }
    }

    public ArrayList<PointBean> getPointList() {
        return mPointList;
    }

    public TouchView(Context context) {
        super(context);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
