/**
 * Copyright 2014, Barend Garvelink
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.s16.drawing;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

/**
 * A square Drawable that renders a single glyph from a Typeface resource. The drawable has a
 * content area defined by its bounds and its padding. The glyph is scaled so that its largest
 * dimension fills this area. The smaller dimension is then centered.
 */
public class IconFontDrawable extends Drawable {

	private static final int DEFAULT_INTRINSIC_SIZE = 48;
    /**
     * Configurable: alpha channel for the foreground color (default unset). If not set, the alpha
     * value from the {@link #color} or {@link #colorStateList} is used. Once set, this overrides
     * the alpha information in the assigned color, including with state changes. The unset value
     * is {@code -1}.
     */
    private int alpha = -1;

    /**
     * Configurable: foreground color, simple case (default black). Any changes to {@link #alpha}
     * are reflected in this variable.
     */
    private int color = Color.BLACK;

    /**
     * Configurable: foreground color, for state-aware rendering (optional, no default). Any changes
     * to {@link #alpha} are reflected in this variable. Prevails over {@link #color} if set.
     */
    private ColorStateList colorStateList;

    /**
     * Configurable: glyph to display in the drawable (required).
     */
    private final char[] glyph = new char[]{'\0'};

    /**
     * Configurable: intrinsic size of the icon (optional, default -1 for no intrinsic size).
     */
    private int intrinsicSize = -1;

    /**
     * Configurable: padding around the icon glyph within the bounds (optional, default zero).
     */
    private int padding = 0;

    /**
     * Configurable: the rotation in degrees of the canvas when drawing the glyph. Zero is straight
     * up, positive values rotate the glyph clockwise (optional, default zero).
     */
    private float rotation;

    /**
     * Configurable: typeface to select the glyph from.
     */
    private Typeface typeface;

    /**
     * Internal: a rectangle used as a temporary value during layout.
     *
     * @hide
     */
    private Rect drawableArea;

    /**
     * Internal: the Paint used to draw {@link #glyphPath} onto our canvas.
     *
     * @hide
     */
    private Paint glyphPaint;

    /**
     * Internal: the font glyph path to draw onto our canvas.
     *
     * @hide
     */
    private Path glyphPath;

    /**
     * Internal: a float rectangle used as a temporary value during layout.
     *
     * @hide
     */
    private RectF glyphPathBounds;

    /**
     * Internal: the transformation matrix to use for scaling and centering the glyph.
     *
     * @hide
     */
    private Matrix glyphPathTransform;

    /**
     * Internal: glyph rendering color, calculated from state, alpha and color values.
     *
     * @hide
     */
    private int renderingColor = Color.BLACK;
    
    public static int getDefaultIntrinsicSize(Context context) {
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

    private IconFontDrawable(final Typeface typeface) {
        this.typeface = typeface;
        this.glyphPaint = new Paint();
        this.glyphPaint.setAntiAlias(true);
        this.glyphPaint.setTypeface(typeface);
        this.glyphPathTransform = new Matrix();
        this.glyphPath = new Path();
        this.drawableArea = new Rect();
        this.glyphPathBounds = new RectF();
    }

    /**
     * Fully initializing constructor to support the Builder pattern.
     */
    IconFontDrawable(int alpha, int color, ColorStateList colorStateList, char glyph,
                     int intrinsicSize, int padding, float rotation, Typeface typeface) {
        this(typeface);
        this.alpha = alpha;
        this.color = color;
        this.colorStateList = colorStateList;
        this.glyph[0] = glyph;
        this.intrinsicSize = intrinsicSize;
        this.padding = padding;
        this.rotation = rotation;
        computeRenderingColor();
    }

    /**
     * Construct an icon font drawable without an intrinsic size in a solid color.
     *
     * @param typeface (nullable) typeface to select the glyph from.
     * @param glyph    the glyph to use.
     * @param color    the color in which to render the glyph.
     */
    public IconFontDrawable(final Typeface typeface, final char glyph, final int color) {
        this(typeface);
        this.glyph[0] = glyph;
        this.color = color;
        computeRenderingColor();
    }

    /**
     * Construct an icon font drawable with an intrinsic size in a solid color.
     *
     * @param typeface      (nullable) typeface to select the glyph from.
     * @param glyph         the glyph to use.
     * @param color         the color in which to render the glyph.
     * @param intrinsicSize the intrinsic size in pixels.
     */
    public IconFontDrawable(final Typeface typeface, final char glyph, final int color, final int intrinsicSize) {
        this(typeface);
        this.glyph[0] = glyph;
        this.color = color;
        this.intrinsicSize = intrinsicSize;
        computeRenderingColor();
    }

    /**
     * Sets the alpha value, triggering a repaint if the value changed.
     *
     * @param alpha an alpha value.
     */
    @Override
    public void setAlpha(int alpha) {
        final int newAlpha = (alpha & 0xFF);
        if (this.alpha != newAlpha) {
            this.alpha = newAlpha;
            computeRenderingColor();
        }
    }

    /**
     * Unsets the alpha value, thus reverting the transparency to the level encoded in the glyph
     * color value. This method triggers a repaint if needed.
     */
    public void unsetAlpha() {
        setAlpha(-1);
    }

    /**
     * Sets the icon color to a single color, triggering a repaint if the value changed. Note that
     * if {@link #colorStateList} is set to a non-null value, it prevails.
     *
     * @param color a color value. The alpha bits are ignored.
     * @see #setAlpha(int)
     * @see #setColor(android.content.res.ColorStateList)
     */
    public void setColor(int color) {
        final int newColor = (color & 0x00FFFFFF);
        if (this.color != newColor) {
            this.color = newColor;
            computeRenderingColor();
        }
    }

    /**
     * Sets the icon color to a color state list, triggering a repaint if the value changed.
     *
     * @param stateColors a color state list. The alpha value is ignored.
     * @see #setAlpha(int)
     * @see #setColor(int)
     */
    public void setColor(ColorStateList stateColors) {
        this.colorStateList = stateColors;
        computeRenderingColor();
    }

    /**
     * Sets the displayed glyph, triggering a layout and repaint if the value changed.
     *
     * @param glyph the glyph.
     */
    public void setGlyph(char glyph) {
        if (glyph != this.glyph[0]) {
            this.glyph[0] = glyph;
            computeGlyphPath();
        }
    }

    /**
     * Sets the intrinsic size of the drawable, triggering a layout and repaint if the value
     * changed. The drawable is constrained to square.
     *
     * @param intrinsicSize the intrinsic size in pixels.
     */
    public void setIntrinsicSize(int intrinsicSize) {
        if (this.intrinsicSize != intrinsicSize) {
            this.intrinsicSize = intrinsicSize;
            computeGlyphPath();
        }
    }

    /**
     * Sets the padding of the drawable area, triggering a layout and repaint if the value changed.
     *
     * @param padding the padding value in pixels.
     */
    public void setPadding(int padding) {
        if (this.padding != padding) {
            this.padding = padding;
            computeGlyphPath();
        }
    }

    /**
     * Sets the rotation of the drawable, triggering a repaint if the value changed.
     *
     * @param rotation the rotation in degrees. Zero is straight up, positive values rotate the
     *                 glyph clockwise.
     */
    public void setRotation(float rotation) {
        if (this.rotation != rotation) {
            this.rotation = rotation;
            invalidateSelf();
        }
    }

    /**
     * Sets the typeface asset from which the glyph is taken, triggering a layout and repaint if
     * the value changed.
     *
     * @param typeface the typeface asset.
     */
    public void setTypeface(Typeface typeface) {
        if (this.typeface != typeface) {
            this.typeface = typeface;
            this.glyphPaint.setTypeface(typeface);
            computeGlyphPath();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        glyphPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public boolean isStateful() {
        return colorStateList != null && colorStateList.isStateful();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        computeGlyphPath();
    }

    @Override
    public int getIntrinsicWidth() {
        return intrinsicSize;
    }

    @Override
    public int getIntrinsicHeight() {
        return intrinsicSize;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        if (colorStateList != null) {
            computeRenderingColor();
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotation, drawableArea.exactCenterX(), drawableArea.exactCenterY());
        canvas.drawPath(glyphPath, glyphPaint);
        canvas.restore();
    }

    private void computeGlyphPath() {
        drawableArea.set(getBounds());
        drawableArea.inset(padding, padding);
        glyphPaint.getTextPath(glyph, 0, 1, 0, 0, glyphPath);
        // Add an extra path point to fix the icon remaining blank on a Galaxy Note 2 running 4.1.2.
        glyphPath.computeBounds(glyphPathBounds, false);
        final float centerX = glyphPathBounds.centerX();
        final float centerY = glyphPathBounds.centerY();
        glyphPath.moveTo(centerX, centerY);
        glyphPath.lineTo(centerX + 0.001f, centerY + 0.001f);
        final float areaWidthF = (float) drawableArea.width();
        final float areaHeightF = (float) drawableArea.height();
        final float scaleX = areaWidthF / glyphPathBounds.width();
        final float scaleY = areaHeightF / glyphPathBounds.height();
        final float scaleFactor = Math.min(scaleX, scaleY);
        glyphPathTransform.setScale(scaleFactor, scaleFactor);
        glyphPath.transform(glyphPathTransform);

        // TODO this two pass calculation irks me.
        // It has to be possible to push this into a single Matrix transform; what makes it hard is
        // that the origin of Text is not top-left, but baseline-left so need to account for that.
        glyphPath.computeBounds(glyphPathBounds, false);
        final float areaLeftF = (float) drawableArea.left;
        final float areaTopF = (float) drawableArea.top;
        float transX = areaLeftF - glyphPathBounds.left;
        transX += 0.5f * Math.abs(areaWidthF - glyphPathBounds.width());
        float transY = areaTopF - glyphPathBounds.top;
        transY += 0.5f * Math.abs(areaHeightF - glyphPathBounds.height());
        glyphPath.offset(transX, transY);

        invalidateSelf();
    }

    private void computeRenderingColor() {
        final int newColor;
        if (colorStateList != null) {
            newColor = colorStateList.getColorForState(getState(), renderingColor);
        } else {
            newColor = color;
        }
        int colorWithAlpha = newColor;
        if (alpha >= 0) {
            colorWithAlpha = (newColor & 0x00FFFFFF) | (alpha << 24);
        }
        if (colorWithAlpha != renderingColor) {
            renderingColor = colorWithAlpha;
            glyphPaint.setColor(renderingColor);
            invalidateSelf();
        }
    }

    /**
     * Used for testing only.
     *
     * @hide
     */
    int getRenderingColor() {
        return renderingColor;
    }

    /**
     * Obtain a builder.
     *
     * @param context a context from which to resolve resources.
     * @return a builder.
     */
    public static Builder builder(Context context) {
        return new Builder(context.getResources());
    }

    /**
     * Obtain a builder.
     *
     * @param resources from which to resolve resources.
     * @return a builder.
     */
    public static Builder builder(Resources resources) {
        return new Builder(resources);
    }

    /**
     * Fluent API builder for font icons.
     * <p>
     * Instances of this class can be reused to construct multiple font icons. All properties are
     * kept between builds.
     * </p>
     */
    public static class Builder {
        private final Resources resources;
        private int alpha = -1;
        private int color;
        private ColorStateList colorStateList;
        private char glyph;
        private int intrinsicSize = -1;
        private int padding;
        private float rotation;
        private Typeface typeface;

        Builder(Resources res) {
            this.resources = res;
        }

        /**
         * Transparency value, [0..255].
         *
         * @param alpha an alpha value.
         */
        public Builder setAlphaValue(int alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * Resets the transparency value defined by {@link #setAlphaValue(int)} or
         * {@link #setOpacity(float)}.
         */
        public Builder unsetAlphaValue() {
            this.alpha = -1;
            return this;
        }

        /**
         * Color value, rgb, [0..255] for each channel. If a color StateList is set, it is cleared.
         *
         * @param color a color value. The alpha bits are ignored.
         */
        public Builder setColorValue(int color) {
            this.color = color;
            this.colorStateList = null;
            return this;
        }

        /**
         * Color state list, nullable.
         *
         * @param colorStateList color statelist.
         */
        public Builder setColorStateList(ColorStateList colorStateList) {
            this.colorStateList = colorStateList;
            return this;
        }

        /**
         * Color from resources. If a color StateList is set, it is cleared.
         *
         * @param colorResId {@code R.color} resource ID.
         */
        public Builder setColorResource(int colorResId) {
            this.color = resources.getColor(colorResId);
            this.colorStateList = null;
            return this;
        }

        /**
         * Color StateList from resources.
         *
         * @param colorResId {@code R.color} resource ID.
         */
        public Builder setColorStateListResource(int colorResId) {
            this.colorStateList = resources.getColorStateList(colorResId);
            return this;
        }

        /**
         * Font glyph to render.
         *
         * @param glyph the chosen glyph.
         */
        public Builder setGlyph(char glyph) {
            this.glyph = glyph;
            return this;
        }

        /**
         * Intrinsic size in pixels.
         *
         * @param pixels size in px.
         */
        public Builder setIntrinsicSizeInPixels(int pixels) {
            this.intrinsicSize = pixels;
            return this;
        }

        /**
         * Intrinsic size in density-independent pixels.
         *
         * @param dips size in dip.
         */
        public Builder setIntrinsicSizeInDip(float dips) {
            return setIntrinsicSize(dips, TypedValue.COMPLEX_UNIT_DIP);
        }

        /**
         * Intrinsic size from resources.
         *
         * @param dimensionResId {@code R.dimen} resource ID.
         */
        public Builder setIntrinsicSizeResource(int dimensionResId) {
            this.intrinsicSize = resources.getDimensionPixelSize(dimensionResId);
            return this;
        }

        /**
         * Intrinsic size in a specified unit.
         *
         * @param size the size.
         * @param unit one of {@code TypedValue.COMPLEX_UNIT_*}.
         */
        public Builder setIntrinsicSize(float size, int unit) {
            float dimension = TypedValue.applyDimension(unit, size, resources.getDisplayMetrics());
            this.intrinsicSize = Math.round(dimension);
            return this;
        }

        /**
         * Un-sets the intrinsic size (value will be -1).
         */
        public Builder setNoIntrinsicSize() {
            this.intrinsicSize = -1;
            return this;
        }

        /**
         * Transparency value, [0.0f..1.0f].
         *
         * @param opacity an opacity percentage.
         */
        public Builder setOpacity(float opacity) {
            this.alpha = Math.round(opacity * 255);
            return this;
        }

        /**
         * Padding in pixels.
         *
         * @param pixels padding in px.
         */
        public Builder setPaddingInPixels(int pixels) {
            this.padding = pixels;
            return this;
        }

        /**
         * Padding in density-independent pixels.
         *
         * @param dips padding in dip.
         */
        public Builder setPaddingInDip(float dips) {
            return setPadding(dips, TypedValue.COMPLEX_UNIT_DIP);
        }

        /**
         * Padding from resources.
         *
         * @param dimensionResId {@code R.dimen} resource ID.
         */
        public Builder setPaddingResource(int dimensionResId) {
            this.padding = resources.getDimensionPixelSize(dimensionResId);
            return this;
        }

        /**
         * Padding in a specified unit.
         *
         * @param size the size.
         * @param unit one of {@code TypedValue.COMPLEX_UNIT_*}.
         */
        public Builder setPadding(float size, int unit) {
            float dimension = TypedValue.applyDimension(unit, size, resources.getDisplayMetrics());
            this.padding = Math.round(dimension);
            return this;
        }

        /**
         * Rotation in degrees, where zero is straight up and positive values go clockwise.
         *
         * @param rotation the rotation in degrees.
         */
        public Builder setRotation(float rotation) {
            this.rotation = rotation;
            return this;
        }

        /**
         * The typeface asset to select the glyph from. No caching is done.
         *
         * @param typeface the typeface.
         */
        public Builder setTypeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        /**
         * Build an {@code IconFontDrawable} from the current builder state.
         *
         * @return the requested drawable.
         */
        public IconFontDrawable build() {
            return new IconFontDrawable(alpha, color, colorStateList, glyph, intrinsicSize, padding, rotation, typeface);
        }
    }
}
