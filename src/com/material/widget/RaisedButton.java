package com.material.widget;


import com.material.R;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;


@SuppressLint("NewApi")
public class RaisedButton extends Button {
	
    private static final long ANIMATION_TIME = 500;
    private static ArgbEvaluator argbEvaluator = new ArgbEvaluator();  
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();  
	
    private Paint backgroundPaint;
    private int startColor	= getResources().getColor(R.color.black_disabled);
    private int endColor	= getResources().getColor(R.color.transparent);
    private float paintX, paintY, radius;
    private boolean isCatchFocus = true;

    public RaisedButton(Context context) {
        super(context);
        init(null, 0);
    }

    public RaisedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RaisedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RaisedButton, defStyle, 0);
        startColor 	= a.getColor(R.styleable.RaisedButton_startColor, 	getResources().getColor(R.color.black_disabled));
        endColor 	= a.getColor(R.styleable.RaisedButton_endColor, 	getResources().getColor(R.color.transparent));
        isCatchFocus = a.getBoolean(R.styleable.RaisedButton_isCatchFocus, true);
        a.recycle();

        this.backgroundPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(paintX, paintY, radius, backgroundPaint);
        canvas.restore();
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //记录坐标
            paintX = event.getX();
            paintY = event.getY();
            //启动动画
            startMoveAnimator();
        }
        this.getParent().requestDisallowInterceptTouchEvent(false);
        super.onTouchEvent(event);
        return isCatchFocus;
    }
    
    private void startMoveAnimator() {
        long time = ANIMATION_TIME;
        float start, end, height, width, speed = 0.3f;
        
        //Height Width
        height = getHeight();
        width = getWidth();
  
        //Start End
        if (height < width) {
            start = height;
            end = width;
        } else {  
            start = width;  
            end = height;  
        }  
        
        start =  paintY < start / 2  ? start - paintY : paintY;  
        end = end * 0.8f / 2f;
        
        //If the approximate square approximate square  
        if (start > end) {  
            start = end * 0.6f;  
            end = end / 0.8f;  
            time = (long) (time * 0.65);  
            speed = 1f;  
        }
        
        //PaintX  
        ObjectAnimator aPaintX = ObjectAnimator.ofFloat(this, mPaintXProperty, paintX, width / 2);  
        aPaintX.setDuration(time);  
        //PaintY  
        ObjectAnimator aPaintY = ObjectAnimator.ofFloat(this, mPaintYProperty, paintY, height / 2);  
        aPaintY.setDuration((long) (time * speed));  
        //Radius  
        ObjectAnimator aRadius = ObjectAnimator.ofFloat(this, mRadiusProperty, start, end);  
        aRadius.setDuration(time);  
        //Background  
        @SuppressWarnings("unchecked")
		ObjectAnimator aBackground = ObjectAnimator.ofObject(this, mBackgroundColorProperty, new ArgbEvaluator(), startColor, endColor);  
        aBackground.setDuration(time);  
  
        //AnimatorSet  
        AnimatorSet set = new AnimatorSet();  
        set.playTogether(aPaintX, aPaintY, aRadius, aBackground);  
        set.setInterpolator(ANIMATION_INTERPOLATOR);  
        set.start();
    }

//    private void startAnimator() {
//
//        //计算半径变化区域
//        int start, end;
//
//        if (getHeight() < getWidth()) {
//            start = getHeight();
//            end = getWidth();
//        } else {
//            start = getWidth();
//            end = getHeight();
//        }
//
//        float startRadius = (start / 2 > paintY ? start - paintY : paintY) * 1.15f;
//        float endRadius = (end / 2 > paintX ? end - paintX : paintX) * 0.85f;
//
//        //新建动画
//        AnimatorSet set = new AnimatorSet();
//        //添加变化属性
//        set.playTogether(
//                //半径变化
//                ObjectAnimator.ofFloat(this, mRadiusProperty, startRadius, endRadius),
//                //颜色变化 黑色到透明
//                ObjectAnimator.ofObject(this, mBackgroundColorProperty, new ArgbEvaluator(), Color.GRAY, Color.TRANSPARENT)
//        );
//        // 设置时间
//        set.setDuration((long) (1200 / end * endRadius));
//        //先快后慢
//        set.setInterpolator(new DecelerateInterpolator());
//        set.start();
//    }

    private Property<RaisedButton, Float> mPaintXProperty = new Property<RaisedButton, Float>(Float.class, "paintX") {  
        @Override  
        public Float get(RaisedButton object) {  
            return object.paintX;  
        }  
  
        @Override  
        public void set(RaisedButton object, Float value) {  
            object.paintX = value;  
        }  
    };  
  
    private Property<RaisedButton, Float> mPaintYProperty = new Property<RaisedButton, Float>(Float.class, "paintY") {  
        @Override  
        public Float get(RaisedButton object) {  
            return object.paintY;  
        }  
  
        @Override  
        public void set(RaisedButton object, Float value) {  
            object.paintY = value;  
        }  
    };  

    
    private Property<RaisedButton, Float> mRadiusProperty = new Property<RaisedButton, Float>(Float.class, "radius") {
        @Override
        public Float get(RaisedButton object) {
            return object.radius;
        }

        @Override
        public void set(RaisedButton object, Float value) {
            object.radius = value;
            //刷新Canvas
            invalidate();
        }
    };

    private Property<RaisedButton, Integer> mBackgroundColorProperty = new Property<RaisedButton, Integer>(Integer.class, "bg_color") {
        @Override
        public Integer get(RaisedButton object) {
            return object.backgroundPaint.getColor();
        }

        @Override
        public void set(RaisedButton object, Integer value) {
            object.backgroundPaint.setColor(value);
        }
    };
}