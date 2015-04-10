package com.s16.drawing;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class HomeUpDrawable extends Drawable implements Animatable {
	
	private static final int DEFAULT_INTRINSIC_SIZE = 32; // 50
	private static final float ARROW_HEAD_ANGLE = (float)Math.toRadians(45.0D);
	private static final long FRAME_DURATION = 1000 / 60;
	
	public static final int DEFAULT_ARROW_COLOR = 0xA0FFFFFF;
	public static final int DEFAULT_ARROW_COLOR_LIGHT = 0x60000000;
	private static final boolean MATERIAL_SDK_INT = android.os.Build.VERSION.SDK_INT > 20;
	
	public static final int MODE_NORMAL = 0;
	public static final int MODE_HOME_UP = 1;
	public static final int MODE_DRAWER = 2;
	
	private Context mContext;
	private final int mMode;
	private final Drawable mIcon;
	private final Path mArrowPath;
	private final Paint mArrowPaint;
	private final Paint mPaint;
	private float mProgress;
	private float mRotate;
	
	private final int mIntrinsicWidth;
	private final int mIntrinsicHeight;
	
	protected float mBarGap;
    protected float mBarSize;
    protected float mBarThickness;
    protected float mMiddleArrowSize;
    protected float mTopBottomArrowSize;
    
    private final Runnable mUpdater = new Runnable() {

		@Override
		public void run() {
			if(mRunning) {
				if (mProgress >= 0.0f) {
					mProgress += 0.1f;
					if (mProgress >= 1.0f) {
						mProgress = 1.0f;
						unscheduleSelf(this);
						mRunning = false;
					} else {
						scheduleSelf(this, SystemClock.uptimeMillis() + FRAME_DURATION);
					}
				} else {
					mProgress -= 0.1f;
					if (mProgress <= 0.0f) {
						mProgress = 0.0f;
						unscheduleSelf(this);
						mRunning = false;		
					} else {
						scheduleSelf(this, SystemClock.uptimeMillis() + FRAME_DURATION);
					}
				}
				
				updateBound();
				invalidateSelf();
			}
		}
	};
	private boolean mRunning;
	
	public HomeUpDrawable(Context context) {
		this(context, null, DEFAULT_ARROW_COLOR, MODE_NORMAL);
	}
	
	public HomeUpDrawable(Context context, int mode) {
		this(context, null, DEFAULT_ARROW_COLOR, mode);
	}
	
	public HomeUpDrawable(Context context, int arrowColor, int mode) {
		this(context, null, arrowColor, mode);
	}
	
	public HomeUpDrawable(Context context, Drawable icon, int mode) {
		this(context, icon, DEFAULT_ARROW_COLOR, mode);
	}
	
	public HomeUpDrawable(Context context, Drawable icon, int arrowColor, int mode) {
		mContext = context;
		if (icon == null) {
			ApplicationInfo applicationInfo = context.getApplicationInfo();
			mIcon = context.getResources().getDrawable(applicationInfo.icon);
		} else { 
			mIcon = icon;
		}
		mMode = mode;
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mBarSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, dm);
        mTopBottomArrowSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11, dm);
        mBarThickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, dm);
        mBarGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm);
        mMiddleArrowSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, dm);
		
		mArrowPath = new Path();
		mArrowPaint = new Paint();
		mArrowPaint.setStyle(Paint.Style.FILL);
		mArrowPaint.setColor(arrowColor);
		
		mPaint = new Paint();
		mPaint.setColor(arrowColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.SQUARE);
		mPaint.setStrokeWidth(mBarThickness);
		
		final int defaultIntrinsicSize = getDefaultIntrinsicSize(context);
		int baseSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultIntrinsicSize, dm);
		if (MATERIAL_SDK_INT) {
			mIntrinsicHeight = baseSize;
			mIntrinsicWidth = baseSize;
		} else {
			mIntrinsicHeight = (baseSize/4) * 3;
			mIntrinsicWidth = baseSize;
		}
	}
	
	private static int getDefaultIntrinsicSize(Context context) {
		int screenLayout = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
		switch (screenLayout) {
			case 1: // mdpi
				return DEFAULT_INTRINSIC_SIZE;
			case 2: // hdpi
				return (int)(DEFAULT_INTRINSIC_SIZE * 1.5f);
			case 3: // xhdpi
			case 4: // xxdpi
				return DEFAULT_INTRINSIC_SIZE * 2;
			//case 4: // xxdpi
			//	return DEFAULT_INTRINSIC_SIZE * 3;
			default:
				break;
		}
		
		if (screenLayout > 4) return DEFAULT_INTRINSIC_SIZE * 3;
		return DEFAULT_INTRINSIC_SIZE;
	}
	
	protected float lerp(float paramFloat1, float paramFloat2, float paramFloat3) {
        return paramFloat1 + paramFloat3 * (paramFloat2 - paramFloat1);
    }
	
	private void createArrowPath(RectF bounds) {
		if (mMode == MODE_NORMAL) return;
		
		float progress = (mMode == MODE_HOME_UP) ? 1.0f : mProgress;
		if (MATERIAL_SDK_INT) {
			float f1 = lerp(mBarSize, mTopBottomArrowSize, progress);
	        float f2 = lerp(mBarSize, mMiddleArrowSize, progress);
	        float f3 = lerp(0.0F, mBarThickness / 2.0F, progress);
	        float f4 = lerp(0.0F, ARROW_HEAD_ANGLE, progress);
	        float f5 = 0.0F;
	        float f6 = 180.0F;
	        mRotate = lerp(f5, f6, progress);
	        float f8 = lerp(mBarGap + mBarThickness, 0.0F, progress);
	        mArrowPath.rewind();
	        float f9 = -f2 / 2.0F;
	        mArrowPath.moveTo(f9 + f3, 0.0F);
	        mArrowPath.rLineTo(f2 - f3, 0.0F);
	        float f10 = (float) Math.round(f1 * Math.cos(f4));
	        float f11 = (float) Math.round(f1 * Math.sin(f4));
	        mArrowPath.moveTo(f9, f8);
	        mArrowPath.rLineTo(f10, f11);
	        mArrowPath.moveTo(f9, -f8);
	        mArrowPath.rLineTo(f10, -f11);
	        mArrowPath.moveTo(0.0F, 0.0F);
	        
		} else {
			float width = bounds.width();
			float height = bounds.height();
			float s = Math.min(width, height) / 48;
			
			if (mMode == MODE_HOME_UP) {
				mArrowPath.rewind();
				mArrowPath.moveTo(s * 26, s * 2);
				mArrowPath.lineTo(s * 35, s * 2);
				mArrowPath.lineTo(s * 21, s * 23);
				mArrowPath.lineTo(s * 34, s * 44);
				mArrowPath.lineTo(s * 25, s * 44);
				mArrowPath.lineTo(s * 12, s * 23);
				mArrowPath.lineTo(s * 26, s * 2);
				
			} else if (mMode == MODE_DRAWER) {
				float dx = 21.0f + (progress * 10.0f);
				mArrowPath.rewind();
				mArrowPath.moveTo(s * 1, s * 4);
				mArrowPath.lineTo(s * dx, s * 4);
				mArrowPath.lineTo(s * dx, s * 12);
				mArrowPath.lineTo(s * 1, s * 12);
				mArrowPath.lineTo(s * 1, s * 4);
				
				mArrowPath.moveTo(s * 1, s * 22);
				mArrowPath.lineTo(s * dx, s * 22);
				mArrowPath.lineTo(s * dx, s * 30);
				mArrowPath.lineTo(s * 1, s * 30);
				mArrowPath.lineTo(s * 1, s * 22);
				
				mArrowPath.moveTo(s * 1, s * 40);
				mArrowPath.lineTo(s * dx, s * 40);
				mArrowPath.lineTo(s * dx, s * 48);
				mArrowPath.lineTo(s * 1, s * 48);
				mArrowPath.lineTo(s * 1, s * 40);
			}
		}
		
		mArrowPath.close();
		mArrowPath.computeBounds(bounds, false);
	}
	
	private Rect getCalculateBounds(Rect bounds) {
		if (MATERIAL_SDK_INT) {
			int baseSize = Math.min(bounds.width(), bounds.height());
			int x= (bounds.width() - baseSize) / 2;
			int y= (bounds.height() - baseSize) / 2;
			return new Rect(x, y, x + baseSize, y + baseSize);
		} else {
			int dx = bounds.width() / 4;
			int dy = bounds.height() / 3;
			int baseSize = Math.min(dx, dy);
			
			int width = baseSize * 4;
			int height = baseSize * 3;
			int x= (bounds.width() - width) / 2;
			int y= (bounds.height() - height) / 2;
			 
			return new Rect(x, y, x + width, y + height);
		}
	}
	
	private RectF getArrowBounds(Rect bounds) {
		if (MATERIAL_SDK_INT) {
			int dx = bounds.width() / 4;
			int dy = bounds.height() / 4;
			int baseSize = Math.min(dx, dy);
			
			RectF arrowBounds = new RectF();
			arrowBounds.top = bounds.top + baseSize;
			arrowBounds.left = bounds.left + baseSize;
			arrowBounds.bottom = bounds.left + (baseSize * 3);
			arrowBounds.right = bounds.left + (baseSize * 3);
			return arrowBounds;
		} else {
			int dx = bounds.width() / 4;
			int dy = bounds.height() / 3;
			int baseSize = Math.min(dx, dy);
			
			RectF arrowBounds = new RectF();
			arrowBounds.top = bounds.top + baseSize;
			arrowBounds.bottom = bounds.top + (baseSize * 2);
			arrowBounds.left = bounds.left;
			arrowBounds.right = bounds.left + baseSize;
			return arrowBounds;
		}
	}
	
	private Rect getIconBounds(Rect bounds) {
		int dx = bounds.width() / 4;
		int dy = bounds.height() / 3;
		int baseSize = Math.min(dx, dy);
		
		Rect iconBounds = new Rect(bounds);
		iconBounds.left += baseSize;
		return iconBounds;
	}
	
	private void updateBound() {
		Rect bounds = getBounds();
		createArrowPath(getArrowBounds(bounds));
	}

	@Override
	public void draw(Canvas canvas) {
		
		Rect bounds = getBounds();
		Rect calcBounds = getCalculateBounds(bounds);
		
		if (mMode == MODE_HOME_UP || mMode == MODE_DRAWER) {
			int saveCount = canvas.save();
			RectF arrowBounds = getArrowBounds(calcBounds);
			if (MATERIAL_SDK_INT) {
				canvas.rotate(180.0F, arrowBounds.centerX(), arrowBounds.centerY());
				canvas.rotate(mRotate, arrowBounds.centerX(), arrowBounds.centerY());
		        canvas.translate(arrowBounds.centerX(), arrowBounds.centerY());
		        canvas.drawPath(mArrowPath, mPaint);
			} else {
				canvas.translate(arrowBounds.left, arrowBounds.top);
				canvas.drawPath(mArrowPath, mArrowPaint);
			}
			canvas.restoreToCount(saveCount);
		}
		
		if (!MATERIAL_SDK_INT && mIcon != null) {
			Rect iconRect = getIconBounds(calcBounds);
			mIcon.setBounds(iconRect);
			mIcon.draw(canvas);
		}
	}
	
	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		createArrowPath(getArrowBounds(bounds));
	}

	@Override
	public void setAlpha(int alpha) {
		mArrowPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mArrowPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}
	
	@Override
	public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }
	
	@Override
	public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }

	public void setProgress(float paramFloat) {
        mProgress = paramFloat;
        updateBound();
        invalidateSelf();
    }
	
	public void setColor(int resourceId) {
		mArrowPaint.setColor(mContext.getResources().getColor(resourceId));
    }

	@Override
	public void start() {
		if (isRunning()) return;
		scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
		invalidateSelf();
	}

	@Override
	public void stop() {
		if (!isRunning()) return;
		mRunning = false;
		unscheduleSelf(mUpdater);
	}
	
	@Override
	public void scheduleSelf(Runnable what, long when) {
		mRunning = true;
		super.scheduleSelf(what, when);
	}

	@Override
	public boolean isRunning() {
		return mRunning;
	}
}
