package com.github.pxsrt.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Created by George on 2015-05-22.
 */
public class SortView extends SurfaceView{

    public static final String TAG = SortView.class.getSimpleName();

    public SortView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void drawImage(final Bitmap img) {
        Canvas canvas = getHolder().lockCanvas();
        Log.d(TAG, "Attempting to draw...");

        if (canvas == null) {
            Log.d(TAG, "Could not draw. Canvas is null.");
        } else {
            Log.d(TAG, "Drawing...");
            canvas.drawBitmap(img, null, getCenteredScaledRect(img), null);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Returns a Rect of correct dimensions to scale and center the specified
     * Bitmap in this View when drawing to this View's Canvas.
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

            return new Rect(0, leftoverHeight / 2, viewWidth, scaledHeight + (leftoverHeight / 2));

        } else {

            int scaledWidth = (int) Math.round(heightScale * imgWidth);
            int leftoverWidth = viewWidth - scaledWidth;

            return new Rect(leftoverWidth, 0, scaledWidth + (leftoverWidth / 2), viewHeight);
        }
    }
}
