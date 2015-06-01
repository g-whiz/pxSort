package com.github.pxsrt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by George on 2015-05-22.
 */
public class SortView extends SurfaceView implements SurfaceHolder.Callback, Sort.Callback{

    public static final String TAG = SortView.class.getSimpleName();

    private Sort sort;
    private Bitmap img;

    public SortView(Context context, Sort sort, Bitmap img) {
        super(context);
        getHolder().addCallback(this);

        this.sort = sort;
        sort.addCallback(this);

        this.img = img;
    }

    public SortView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        sort.apply(img);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void drawImg() {
        Canvas canvas = getHolder().lockCanvas();
        Log.d(TAG, "Attempting to draw...");

        if(canvas == null){
            Log.d(TAG, "Could not draw. Canvas is null.");
        } else {
            Log.d(TAG, "Drawing...");
            synchronized (getHolder()) {
                canvas.drawBitmap(img, null, getCenteredScaledRect(img), null);
            }
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Returns a Rect of correct dimensions to scale and center the specified Bitmap in this View
     * when drawing to this View's Canvas.
     *
     * @param img The image to scale.
     * @return Rect
     */
    private Rect getCenteredScaledRect(Bitmap img) {
        int viewHeight = getHeight();
        int viewWidth = getWidth();

        int imgHeight = img.getHeight();
        int imgWidth = img.getWidth();

        double heightScale = (double) viewHeight / (double) imgHeight;
        double widthScale = (double) viewWidth / (double) imgWidth;

        if (imgWidth >= imgHeight) {

            int scaledHeight = (int) Math.round(widthScale * imgHeight);
            int leftoverHeight = viewHeight - scaledHeight;

            Rect rect = new Rect(0, leftoverHeight / 2, viewWidth, scaledHeight + (leftoverHeight / 2));

            Log.d(TAG, "Scaled to: height - " + rect.height() + " width - " + rect.width() + " imgHeight - " + scaledHeight);

            return rect;
        } else {

            int scaledWidth = (int) Math.round(heightScale * imgWidth);
            int leftoverWidth = viewWidth - scaledWidth;

            return new Rect(leftoverWidth, 0, scaledWidth + (leftoverWidth / 2), viewHeight);
        }
    }

    @Override
    public void sortComplete() {
        drawImg();
    }
}
