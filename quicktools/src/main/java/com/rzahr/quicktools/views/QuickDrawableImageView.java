package com.rzahr.quicktools.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * this class is used to enable drawing on top of an image view
 */

@SuppressWarnings("unused")
public class QuickDrawableImageView extends AppCompatImageView implements View.OnTouchListener{

    float downX = 0;
    float downY = 0;
    float upx = 0;
    float upy = 0;

    Canvas canvas;
    Paint paint;
    Matrix matrix;

    public QuickDrawableImageView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
    }

    public QuickDrawableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
    }

    public QuickDrawableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
    }

    public void setNewImage(Bitmap alteredBitmap, Bitmap bmp, int color, int width) {
        canvas = new Canvas(alteredBitmap);
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(color);
        paint.setDither(true);
        paint.setStrokeWidth(width);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        matrix = new Matrix();
        canvas.drawBitmap(bmp, matrix, paint);

        setImageBitmap(alteredBitmap);
    }

    public void clearImage(Bitmap originalBitmap, Bitmap bmp) {
        try {
            canvas = new Canvas(originalBitmap);
            canvas.drawBitmap(bmp, matrix, paint);

            setImageBitmap(originalBitmap);
        }
        catch (Exception ignored){}
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = getPointerCoords(event)[0];//event.getX();
                downY = getPointerCoords(event)[1];//event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downX, downY, upx, upy, paint);
                invalidate();
                downX = upx;
                downY = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downX, downY, upx, upy, paint);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    final float[] getPointerCoords(MotionEvent e) {
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        getImageMatrix().invert(matrix);
        matrix.postTranslate(getScrollX(), getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }

    public void changeDrawingColor(int color){
        if(paint != null)
            paint.setColor(color);
    }

    public void changeDrawingStroke(int strokeWidth){
        if(paint != null)
            paint.setStrokeWidth(strokeWidth);
    }
}
