/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vextil.shapeimageview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ShapeImageView extends ImageView {

    /**
     * Shape type
     */
    public interface Shape {
        int CIRCLE = 1;
        int ROUNDED = 2;
    }

    /**
     * Direction of decorations
     */
    public interface Direction {
        int LEFT_TOP = 1;
        int LEFT_BOTTOM = 2;
        int RIGHT_TOP = 3;
        int RIGHT_BOTTOM = 4;
    }

    private int direction = Direction.RIGHT_BOTTOM;
    private int borderColor = Color.BLACK;
    private int shapeType = Shape.ROUNDED;
    private int borderWidth;
    private int padding;
    private int resource;

    private float radiusX = 24f;
    private float radiusY = 24f;

    private boolean invalidated = true;
    private boolean rebuildShader = true;

    private Drawable decorationsView;

    private Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint shaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path maskPath = new Path();
    private RectF rectF;

    public ShapeImageView(Context context) {
        this(context, null);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShapeImageView);
            shapeType = a.getInt(R.styleable.ShapeImageView_shape, shapeType);
            direction = a.getInt(R.styleable.ShapeImageView_decorations_direction, direction);
            radiusX = a.getFloat(R.styleable.ShapeImageView_radius_x, radiusX);
            radiusY = a.getFloat(R.styleable.ShapeImageView_radius_y, radiusY);
            decorationsView = a.getDrawable(R.styleable.ShapeImageView_decorations_src);
            a.recycle();
        }

        shaderPaint.setFilterBitmap(true);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (ScaleType.FIT_XY == scaleType) {
            scaleType = ScaleType.CENTER_CROP;
        }
        super.setScaleType(scaleType);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            if (invalidated) {
                invalidated = false;
                createMask(getMeasuredWidth(), getMeasuredHeight());
            }

            if (rebuildShader) {
                rebuildShader = false;
                createShader();
            }

            if (null != shaderPaint.getShader()) {

                canvas.drawPath(maskPath, shaderPaint);

                if (borderWidth > 0) {
                    maskPaint.setStyle(Paint.Style.STROKE);
                    maskPaint.setColor(borderColor);
                    maskPaint.setStrokeWidth(borderWidth);
                    canvas.drawPath(maskPath, maskPaint);
                }

                if (decorationsView != null) {
                    Bitmap bitmap = ((BitmapDrawable) decorationsView).getBitmap();
                    int width = decorationsView.getIntrinsicWidth();
                    int height = decorationsView.getIntrinsicHeight();
                    switch (direction) {
                        case Direction.LEFT_TOP:
                            canvas.drawBitmap(bitmap, padding, padding, shaderPaint);
                            break;
                        case Direction.LEFT_BOTTOM:
                            canvas.drawBitmap(bitmap, padding, getHeight() - height - padding, shaderPaint);
                            break;
                        case Direction.RIGHT_TOP:
                            canvas.drawBitmap(bitmap, getWidth() - width - padding, padding, shaderPaint);
                            break;
                        case Direction.RIGHT_BOTTOM:
                            canvas.drawBitmap(bitmap, getWidth() - width - padding,
                                    getHeight() - height - padding, shaderPaint);
                            break;
                    }
                }
            }

        } else {
            super.onDraw(canvas);
        }
    }

    private void createShader() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawableToBitmap(drawable);
            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP);
            shader.setLocalMatrix(getScaleMatrix(bitmap));
            shaderPaint.setShader(shader);
        }
    }

    private void createMask(int width, int height) {
        maskPath.reset();
        maskPaint.setStyle(Paint.Style.FILL);
        int offset = borderWidth / 2;
        switch (shapeType) {
            case Shape.CIRCLE:
                float center = Math.min(width, height) / 2f;
                float radius = center;
                radius -= offset;// avoid stroke out of bounds
                maskPath.addCircle(center, center, radius, Path.Direction.CW);
                break;
            case Shape.ROUNDED:
                if (rectF == null) {
                    rectF = new RectF();
                }
                rectF.set(offset, offset, width - offset, height - offset);
                maskPath.addRoundRect(rectF, radiusX, radiusY, Path.Direction.CW);
                break;
        }
    }

    /**
     * @param direction {@link ShapeImageView.Direction}
     */
    public void setDecorations(int direction, int padding, Drawable drawable) {
        this.direction = direction;
        this.padding = padding;
        decorationsView = fromDrawable(drawable);
        invalidate();
    }

    public void setBorderColor(int color) {
        borderColor = color;
        invalidate();
    }

    public void setBorderWidth(int width) {
        borderWidth = width;
        invalidate();
    }

    /**
     * @param rx The x-radius of the rounded corners
     * @param ry The y-radius of the rounded corners
     */
    public void setDegreeForRoundRectangle(int rx, int ry) {
        radiusX = rx;
        radiusY = ry;
    }

    /**
     * @param type {@link ShapeImageView.Shape}
     */
    public void changeShapeType(int type) {
        if (shapeType != type) {
            invalidated = true;
        }
        shapeType = type;
        invalidate();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        resource = 0;
        super.setImageDrawable(fromDrawable(drawable));
    }

    @Override
    public void setImageResource(int resId) {
        if (resource != resId) {
            resource = resId;
            setImageDrawable(resolveResource());
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        setImageDrawable(getDrawable());
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        setImageDrawable(new BitmapDrawable(getResources(), bm));
    }

    private Drawable resolveResource() {
        Resources res = getResources();
        if (res == null) {
            return null;
        }

        Drawable d = null;
        if (resource != 0) {
            try {
                d = res.getDrawable(resource);
            } catch (Exception e) {
                resource = 0;
            }
        }
        return fromDrawable(d);
    }

    private Drawable fromDrawable(Drawable drawable) {
        rebuildShader = true;
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                return drawable;
            } else if (drawable instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable) drawable;
                drawable = ld.getDrawable(0);
            } else if (drawable instanceof StateListDrawable) {
                StateListDrawable stateListDrawable = (StateListDrawable) drawable;
                drawable = stateListDrawable.getCurrent();
            }

            if (!(drawable instanceof BitmapDrawable)) {
                Bitmap bm = drawableToBitmap(drawable);
                if (bm != null) {
                    drawable = new BitmapDrawable(getResources(), bm);
                }
            }
        }
        return drawable;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        try {
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                int width = Math.max(drawable.getIntrinsicWidth(), 2);
                int height = Math.max(drawable.getIntrinsicHeight(), 2);
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    private Matrix getScaleMatrix(Bitmap bitmap) {
        float scaleWidth = ((float) getMeasuredWidth()) / bitmap.getWidth();
        float scaleHeight = ((float) getMeasuredHeight()) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return matrix;
    }
}
