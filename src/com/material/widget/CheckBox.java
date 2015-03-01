package com.material.widget;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Checkable;

import com.material.R;

public class CheckBox extends View implements Checkable{
	
    private static final int RING_WIDTH = 5;
    private static final int THUMB_ANIMATION_DURATION = 400;
    private static final ArgbEvaluator ARGB_EVALUATOR = new ArgbEvaluator();
    private static final Interpolator ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    // Color
//	private static final int DEFAULT_COLOR_CHECKED   = Color.parseColor("#ee03a9f4");
    private static final int DEFAULT_COLOR_CHECKED	 = R.color.green_400;
	private static final int DEFAULT_COLOR_UNCHECKED = R.color.black_disabled;
	private int mColorChecked	= getResources().getColor(DEFAULT_COLOR_CHECKED);
	private int mColorUnChecked	= getResources().getColor(DEFAULT_COLOR_UNCHECKED);
	// Animator
    private int mCircleColor;
    private float mSweepAngle;
    private AnimatorSet mAnimatorSet;
    // Check
    private boolean mChecked;
    private boolean mBroadcasting;
    private boolean mIsAttachWindow;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    // Paint
    private RectF mOval;
    private Paint mRingPaint;
    private Paint mCirclePaint;

    private int mRingWidth = RING_WIDTH;
    private float mCenterX, mCenterY;
    private boolean mCustomCircleRadius;
    private int mCircleRadius = -1;
	
    public CheckBox(Context context) {
        super(context);
        init(null, 0);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        boolean check = isChecked();
        
        if (attrs != null) {
            // Load attributes
            final TypedArray a = getContext().obtainStyledAttributes(
                    attrs, R.styleable.CheckBox, defStyle, 0);
            
            // get custom attributes
            mColorChecked = a.getColor(R.styleable.CheckBox_mColor, mColorChecked);
            check = a.getBoolean(R.styleable.CheckBox_mChecked, true);

            a.recycle();
        }
        // To check call performClick()
        setOnClickListener(null);

        // Refresh display with current params
        refreshDrawableState();

        // Init
        initPaint();
        initSize();
        initColor();

        // Init
	    setChecked(check);
    }
    
    
    private void initPaint() {
        if (mCirclePaint == null) {
            mCirclePaint = new Paint(ANTI_ALIAS_FLAG);
            mCirclePaint.setStyle(Paint.Style.FILL);
            mCirclePaint.setAntiAlias(true);
            mCirclePaint.setDither(true);
        }

        if (mRingPaint == null) {
            mRingPaint = new Paint();
            mRingPaint.setStrokeWidth(mRingWidth);
            mRingPaint.setStyle(Paint.Style.STROKE);
            mRingPaint.setStrokeJoin(Paint.Join.ROUND);
            mRingPaint.setStrokeCap(Paint.Cap.ROUND);
            mRingPaint.setAntiAlias(true);
            mRingPaint.setDither(true);
        }
    }

    private void initSize() {
        int w = getWidth();
        int h = getHeight();

        if (w == 0)
            w = getMeasuredWidth();
        if (h == 0)
            h = getMeasuredHeight();

        if (w > 0 && h > 0) {
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();

            int contentWidth = w - paddingLeft - paddingRight;
            int contentHeight = h - paddingTop - paddingBottom;

            int center = Math.min(contentHeight, contentWidth) / 2;
            int areRadius = center - (mRingWidth + 1) / 2;
                      
            mCenterX = center + paddingLeft;
            mCenterY = center + paddingTop;

            if (mOval == null) {
                mOval = new RectF(mCenterX - areRadius, mCenterY - areRadius, mCenterX + areRadius, mCenterY + areRadius);
            }
            else {
                mOval.set(mCenterX - areRadius, mCenterY - areRadius, mCenterX + areRadius, mCenterY + areRadius);
            }

			if (!mCustomCircleRadius)
                mCircleRadius = center - mRingWidth * 2;
            else
            if (mCircleRadius > center)
                mCircleRadius = center;

            // Refresh view
            if (!isInEditMode()) {
                invalidate();
            }
        }
    }

    private void initColor() {
        setCircleColor(isChecked() ? mColorChecked : mColorUnChecked);
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Init this Layout size
        initSize();
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Init this Layout size
        initSize();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCirclePaint.setColor(mCircleColor);
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadius, mCirclePaint);

        if (mOval != null) {
            mRingPaint.setColor(mColorUnChecked);
            canvas.drawArc(mOval, 225, 360, false, mRingPaint);
            mRingPaint.setColor(mColorChecked);
            canvas.drawArc(mOval, 225, mSweepAngle, false, mRingPaint);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachWindow = false;
    }
	
	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return mChecked;
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		setChecked(!mChecked);
	}
	
    @TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();

            // To Animator
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isAttachedToWindow() && isLaidOut())
                    || (mIsAttachWindow && mOval != null)) {
                animateThumbToCheckedState(checked);
            } else {
                // Immediately move the thumb to the new position.
                cancelPositionAnimator();
                setCircleColor(checked ? mColorChecked : mColorUnChecked);
                setSweepAngle(checked ? 360f : 0f);
            }

            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) {
                return;
            }
            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, checked);
            }
            mBroadcasting = false;
        }
    }

    /**
     * =============================================================================================
     * The Animate
     * =============================================================================================
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void animateThumbToCheckedState(boolean newCheckedState) {
        ObjectAnimator sweepAngleAnimator = ObjectAnimator.ofFloat(this, SWEEP_ANGLE, newCheckedState ? 360 : 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            sweepAngleAnimator.setAutoCancel(true);

        ObjectAnimator circleColorAnimator = newCheckedState ? 
        	ObjectAnimator.ofObject(this, CIRCLE_COLOR, ARGB_EVALUATOR, mColorUnChecked, mColorChecked) :
            ObjectAnimator.ofObject(this, CIRCLE_COLOR, ARGB_EVALUATOR, mColorChecked, mColorUnChecked);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            circleColorAnimator.setAutoCancel(true);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(
                sweepAngleAnimator,
                circleColorAnimator
        );
        // set Time
        mAnimatorSet.setDuration(THUMB_ANIMATION_DURATION);
        mAnimatorSet.setInterpolator(ANIMATION_INTERPOLATOR);
        mAnimatorSet.start();
    }

    private void cancelPositionAnimator() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }
    
    /**
     * =============================================================================================
     * The custom properties
     * =============================================================================================
     */

    private static final Property<CheckBox, Float> SWEEP_ANGLE = new Property<CheckBox, Float>(Float.class, "sweepAngle") {
        @Override
        public Float get(CheckBox object) {
            return object.mSweepAngle;
        }

        @Override
        public void set(CheckBox object, Float value) {
            object.setSweepAngle(value);
        }
    };
    
    private static final Property<CheckBox, Integer> CIRCLE_COLOR = new Property<CheckBox, Integer>(Integer.class, "circleColor") {
        @Override
        public Integer get(CheckBox object) {
            return object.mCircleColor;
        }

        @Override
        public void set(CheckBox object, Integer value) {
            object.setCircleColor(value);
        }
    };

	protected void setSweepAngle(Float value) {
		// TODO Auto-generated method stub
		mSweepAngle = value;
		invalidate();
	}

	protected void setCircleColor(Integer value) {
		// TODO Auto-generated method stub
		mCircleColor = value;
		invalidate();
	}
    
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param checkBox  The compound button view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(CheckBox checkBox, boolean isChecked);
    }

}
