package com.material.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.material.R;
import com.material.util.MetricsConverter;

public class FloatingActionButton extends View{
	
	private static final String TAG = "FloatingActionButton";
	
	private TYPE  type  = TYPE.DEFAULT;
	private STATE state = STATE.NORMAL;
	
	private int colorUnPressed	= getResources().getColor(R.color.blue_500);
	private int colorPressed	= getResources().getColor(R.color.blue_800);
	private int shadowColor		= getResources().getColor(R.color.grey_600);

	private float shadowRadius  = MetricsConverter.dpToPx(getContext(), 2.0f);
	private float shadowXOffset = MetricsConverter.dpToPx(getContext(), 1.0f);
	private float shadowYOffset = MetricsConverter.dpToPx(getContext(), 2.0f);

	private float strokeWidth	= 0.0f;
	private int strokeColor		= Color.BLACK;
	
	private Drawable image;
	private float imageSize = MetricsConverter.dpToPx(getContext(), 24.0f);

	private Animation animationOnShow = Animations.load(getContext(), R.anim.fab_jump_from_down);
	private Animation animationOnHide = Animations.load(getContext(), R.anim.fab_jump_to_down);

	protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	
	public FloatingActionButton(Context context) {
		super(context);
		initFloatingActionButton();
	}

	public FloatingActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFloatingActionButton(context, attrs, 0, 0);
	}

	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initFloatingActionButton(context, attrs, defStyleAttr, 0);
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initFloatingActionButton(context, attrs, defStyleAttr, defStyleRes);
	}
	
	private void initFloatingActionButton() {
		initLayerType();
	}
	
	private void initFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		initLayerType();
		TypedArray attributes = context.getTheme().obtainStyledAttributes(
				attrs, R.styleable.FloatingActionButton,defStyleAttr, defStyleRes);
		try {
			initType(attributes);
			initColorUnPressed(attributes);
			initColorPressed(attributes);
			initShadowRadius(attributes);
			initShadowXOffset(attributes);
			initShadowYOffset(attributes);
			initShadowColor(attributes);
			initStrokeWidth(attributes);
			initStrokeColor(attributes);
			initImage(attributes);
			initImageSize(attributes);
			initShowAnimation(attributes);
			initHideAnimation(attributes);
		} catch (Exception e) {
			Log.e(TAG, "Unable to read attr", e);
		} finally {
			attributes.recycle();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initLayerType() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(LAYER_TYPE_SOFTWARE, paint);
		}
	}
	
	private void initType(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_type)) {
			final int id = attrs.getInteger(R.styleable.FloatingActionButton_type, type.getId());
			type = TYPE.forId(id);
		}
	}
	
	private void initColorUnPressed(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_button_color)) {
			colorUnPressed = attrs.getColor(R.styleable.FloatingActionButton_button_color, colorUnPressed);
		}
	}
	
	private void initColorPressed(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_button_colorPressed)) {
			colorPressed = attrs.getColor(R.styleable.FloatingActionButton_button_colorPressed, colorPressed);
		}
	}

	private void initShadowRadius(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_shadow_radius)) {
			shadowRadius = attrs.getDimension(R.styleable.FloatingActionButton_shadow_radius, shadowRadius);
		}
	}
	
	private void initShadowXOffset(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_shadow_xOffset)) {
			shadowXOffset = attrs.getDimension(R.styleable.FloatingActionButton_shadow_xOffset, shadowXOffset);
		}
	}

	private void initShadowYOffset(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_shadow_yOffset)) {
			shadowYOffset = attrs.getDimension(R.styleable.FloatingActionButton_shadow_yOffset, shadowYOffset);
		}
	}

	private void initShadowColor(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_shadow_color)) {
			shadowColor = attrs.getColor(R.styleable.FloatingActionButton_shadow_color, shadowColor);
		}
	}

	private void initStrokeWidth(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_stroke_width)) {
			strokeWidth = attrs.getDimension(R.styleable.FloatingActionButton_stroke_width, strokeWidth);
		}
	}

	private void initStrokeColor(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_stroke_color)) {
			strokeColor = attrs.getColor(R.styleable.FloatingActionButton_stroke_color, strokeColor);
		}
	}

	private void initShowAnimation(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_animation_onShow)) {
			final int animResId = attrs.getResourceId(
				R.styleable.FloatingActionButton_animation_onShow,Animations.NONE.animResId);
			animationOnShow = Animations.load(getContext(), animResId);
		}
	}

	private void initHideAnimation(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_animation_onHide)) {
			final int animResId = attrs.getResourceId(
				R.styleable.FloatingActionButton_animation_onHide,Animations.NONE.animResId);
			animationOnHide = Animations.load(getContext(), animResId);
		}
	}

	private void initImage(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_image)) {
			image = attrs.getDrawable(R.styleable.FloatingActionButton_image);
		}
	}

	private void initImageSize(TypedArray attrs) {
		if(attrs.hasValue(R.styleable.FloatingActionButton_image_size)) {
			imageSize = attrs.getDimension(R.styleable.FloatingActionButton_image_size, imageSize);
		}
	}

	public void show() {
		if(!isShown()) {
			startAnimation(getAnimationOnShow());
			setVisibility(VISIBLE);
		}
	}
	
	public void hide() {
		if(isShown()) {
			startAnimation(getAnimationOnHide());
			setVisibility(INVISIBLE);
		}
	}

	public void dismiss() {
		if(!isDismissed()) {
			if(isShown()) {
				startAnimation(getAnimationOnHide());
			}
			setVisibility(GONE);
			ViewGroup parent = (ViewGroup) getParent();
			parent.removeView(this);
		}
	}
	
	@SuppressWarnings("all")
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		final int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			setState(STATE.PRESSED);
			return true;
		case MotionEvent.ACTION_UP:
			setState(STATE.NORMAL);
			return true;
		default:
			return false;
		}
	}

	@SuppressWarnings("all")
	@Override
	public void startAnimation(Animation animation) {
		if(animation != null) {
			super.startAnimation(animation);
		}
	}
	
	protected final void resetPaint() {
		paint.reset();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	}
	
	@SuppressWarnings("all")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawCircle(canvas);
		if(hasStroke()) {
			drawStroke(canvas);
		}
		if(hasImage()) {
			drawImage(canvas);
		}
	}

	protected void drawCircle(Canvas canvas) {
		resetPaint();
		if(hasShadow()) {
			drawShadow();
		}
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(getState() == STATE.PRESSED ? getColorPressed() : getColorUnPressed());
		canvas.drawCircle(calculateCenterX(), calculateCenterY(), calculateCircleRadius(), paint);
	}
	
	protected void drawImage(Canvas canvas) {
		final int startPointX = (int) (calculateCenterX() - getImageSize() / 2);
		final int startPointY = (int) (calculateCenterY() - getImageSize() / 2);
		final int endPointX = (int) (startPointX + getImageSize());
		final int endPointY = (int) (startPointY + getImageSize());
		getImage().setBounds(startPointX, startPointY, endPointX, endPointY);
		getImage().draw(canvas);
	}
	
	protected void drawStroke(Canvas canvas) {
		resetPaint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(getStrokeWidth());
		paint.setColor(getStrokeColor());
		canvas.drawCircle(calculateCenterX(), calculateCenterY(), calculateCircleRadius(), paint);
	}

	protected void drawShadow() {
		if(!hasElevation()) {
			paint.setShadowLayer(getShadowRadius(), getShadowXOffset(), getShadowYOffset(), getShadowColor());
		}
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private boolean hasElevation() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getElevation() > 0.0f;
	}

	protected float calculateCenterX() {
		final float centerX = getWidth() / 2;
		return centerX;
	}

	protected float calculateCenterY() {
		final float centerY = getHeight() / 2;
		return centerY;
	}

	protected final float calculateCircleRadius() {
		final float circleRadius = getButtonSize() / 2;
		return circleRadius;
	}	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(calculateMeasuredWidth(), calculateMeasuredHeight());
	}

	private int calculateMeasuredWidth() {
		final int measuredWidth = (int) (getButtonSize() + calculateShadowWidth() * 2 + getStrokeWidth() * 2);
		return measuredWidth;
	}

	private int calculateMeasuredHeight() {
		final int measuredHeight = (int) (getButtonSize() + calculateShadowHeight() * 2 + getStrokeWidth() * 2);
		return measuredHeight;
	}
	
	private float calculateShadowWidth() {
		final float shadowWidth = hasShadow() ? getShadowRadius() + Math.abs(getShadowXOffset()) : 0.0f;
		return shadowWidth;
	}

	private float calculateShadowHeight() {
		final float shadowHeight = hasShadow() ? getShadowRadius() + Math.abs(getShadowYOffset()) : 0.0f;
		return shadowHeight;
	}

	
	public boolean isHidden() {
		return getVisibility() == INVISIBLE;
	}

	public boolean isDismissed() {
		ViewGroup parent = (ViewGroup) getParent();
		return parent == null;
	}

	public int getButtonSize() {
		final int buttonSize = (int) type.getSize(getContext());
		return buttonSize;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
		requestLayout();
	}
	
	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
		invalidate();
	}

	public int getColorUnPressed() {
		return colorUnPressed;
	}

	public void setColorUnPressed(int colorUnPressed) {
		this.colorUnPressed = colorUnPressed;
		invalidate();
	}
	
	public int getColorPressed() {
		return colorPressed;
	}

	public void setColorPressed(int colorPressed) {
		this.colorPressed = colorPressed;
		invalidate();
	}

	public boolean hasShadow() {
		return getShadowRadius() > 0.0f;
	}
	
	public void removeShadow() {
		if(hasShadow()) {
			setShadowRadius(0.0f);
		}
	}

	public float getShadowRadius() {
		return shadowRadius;
	}

	public void setShadowRadius(float shadowRadius) {
		this.shadowRadius = MetricsConverter.dpToPx(getContext(), shadowRadius);
		requestLayout();
	}

	public float getShadowXOffset() {
		return shadowXOffset;
	}

	public void setShadowXOffset(float shadowXOffset) {
		this.shadowXOffset = MetricsConverter.dpToPx(getContext(), shadowXOffset);
		requestLayout();
	}
	

	public float getShadowYOffset() {
		return shadowYOffset;
	}

	public void setShadowYOffset(float shadowYOffset) {
		this.shadowYOffset = MetricsConverter.dpToPx(getContext(), shadowYOffset);
		requestLayout();
	}
	
	public int getShadowColor() {
		return shadowColor;
	}

	public void setShadowColor(int shadowColor) {
		this.shadowColor = shadowColor;
		invalidate();
	}

	public boolean hasStroke() {
		return getStrokeWidth() > 0.0f;		
	}

	public void removeStroke() {
		if(hasStroke()) {
			setStrokeWidth(0.0f);
		}
	}
	
	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = MetricsConverter.dpToPx(getContext(), strokeWidth);
		requestLayout();
	}

	public int getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
		invalidate();
	}
	
	public Drawable getImage() {
		return image;
	}

	public boolean hasImage() {
		return getImage() != null;
	}

	public void removeImage() {
		if(hasImage()) {
			setImageDrawable(null);
		}
	}
	
	public void setImageDrawable(Drawable image) {
		this.image = image;
		invalidate();
	}

	public void setImageResource(int resId) {
		setImageDrawable(getResources().getDrawable(resId));
	}

	@SuppressLint("NewApi")
	public void setImageBitmap(Bitmap bitmap) {
		setImageDrawable(new BitmapDrawable(getResources(), bitmap));
	}

	public float getImageSize() {
		return getImage() != null ? imageSize : 0.0f;
	}

	public void setImageSize(float size) {
		this.imageSize = MetricsConverter.dpToPx(getContext(), size);
	}

	public Animation getAnimationOnShow() {
		return animationOnShow;
	}

	public void setAnimationOnShow(Animation animation) {
		this.animationOnShow = animation;
	}

	public void setAnimationOnShow(Animations animation) {
		setAnimationOnShow(Animations.load(getContext(), animation.animResId));
	}

	public Animation getAnimationOnHide() {
		return animationOnHide;
	}

	public void setAnimationOnHide(Animation animation) {
		this.animationOnHide = animation;
	}

	public void setAnimationOnHide(Animations animation) {
		this.animationOnHide = Animations.load(getContext(), animation.animResId);
	}

	
	public enum STATE {
		NORMAL,
		PRESSED
	}
	
	public enum TYPE {
		DEFAULT {
			@Override
			int getId() {
				return 0;
			}
		
			@Override
			float getSize(Context context) {
				return MetricsConverter.dpToPx(context, 56.0f);
			}
		},
		MINI {
			@Override
			int getId() {
				return 1;
			}
			
			@Override
			float getSize(Context context) {
				return MetricsConverter.dpToPx(context, 40.0f);
			}
		};

		abstract int getId();
		abstract float getSize(Context context);

		private static TYPE forId(int id) {
			for (TYPE type : values()) {
				if(type.getId() == id) {
					return type;
				}
			}
			return DEFAULT;
		}
	}

	
	public enum Animations {
		NONE                (0),
		FADE_IN             (R.anim.fab_fade_in),
		FADE_OUT            (R.anim.fab_fade_out),
		SCALE_UP            (R.anim.fab_scale_up),
		SCALE_DOWN          (R.anim.fab_scale_down),
		ROLL_FROM_DOWN      (R.anim.fab_roll_from_down),
		ROLL_TO_DOWN        (R.anim.fab_roll_to_down),
		ROLL_FROM_RIGHT     (R.anim.fab_roll_from_right),
		ROLL_TO_RIGHT       (R.anim.fab_roll_to_right),
		JUMP_FROM_DOWN      (R.anim.fab_jump_from_down),
		JUMP_TO_DOWN        (R.anim.fab_jump_to_down),
		JUMP_FROM_RIGHT     (R.anim.fab_jump_from_right),
		JUMP_TO_RIGHT       (R.anim.fab_jump_to_right);

		private final int animResId;
		
		private Animations(int animResId) {
			this.animResId = animResId;
		}

		protected static Animation load(Context context, int animResId) {
			return animResId == NONE.animResId ? null : AnimationUtils.loadAnimation(context, animResId);
		}
	}

}
