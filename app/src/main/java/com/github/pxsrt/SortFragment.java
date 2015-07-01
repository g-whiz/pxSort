package com.github.pxsrt;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.github.pxsrt.sort.PixelSorter;
import com.github.pxsrt.ui.SortView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by George on 2015-06-01.
 */
public class SortFragment extends Fragment implements SurfaceHolder.Callback, PixelSorter.OnSortCompleteListener {

    public static final String TAG = SortFragment.class.getSimpleName();

    private PixelSorter pixelSorter;
    private SortView sortView;

    private Bitmap img;
    private final Object imgLock = new Object();

    private boolean sortIsRunning;
    private final Object sortIsRunningLock = new Object();

    private boolean readyToDraw;
    private final Object readyToDrawLock = new Object();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_fragment_sort, container, false);
        sortView = ((SortView) view.findViewById(R.id.sort_view));
        sortView.getHolder().addCallback(this);

        return view;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (getImage() == null) {
                    try {
                        Log.d(TAG, "Image is null. Sleeping for 0.25 seconds.");
                        Thread.sleep(250L);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Thread interrupted.", e);
                    }
                }

                sortView.drawImage(getImage());
            }
        }).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    public void onSortComplete() {
        sortView.drawImage(img);
    }

    public void runSort(int framesPerSecond) {

        final long period = Math.round( (1.0 / (double) framesPerSecond) * 1000.0);

        final TimerTask setReadyToDraw = new TimerTask() {
            @Override
            public void run() {
                setReadyToDraw(true);
            }
        };

        Timer drawTimer = new Timer();
        drawTimer.scheduleAtFixedRate(setReadyToDraw, 0L, period);

        new Thread (new Runnable() {
            @Override
            public void run() {
                while (sortIsRunning()) {
                    applySortAndDraw();
                    setReadyToDraw(false);

                    while(!isReadyToDraw()){
                        try {
                            Thread.sleep(period / 6);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Thread interrupted.", e);
                        }
                    }
                }
            }
        }).start();
    }

    private boolean isReadyToDraw() {
        synchronized (readyToDrawLock) {
            return readyToDraw;
        }
    }

    private void setReadyToDraw(boolean readyToDraw) {
        synchronized (readyToDrawLock) {
            this.readyToDraw = readyToDraw;
        }
    }

    public void applySortAndDraw() {
        pixelSorter.apply(img);
    }

    public Bitmap getImage() {
        synchronized (imgLock) {
            return img;
        }
    }

    public synchronized void setImage(Bitmap img) {
        synchronized (imgLock) {
            this.img = img;
        }
    }

    public void setPixelSorter(PixelSorter pixelSorter) {
        this.pixelSorter = pixelSorter;
        this.pixelSorter.setOnSortCompleteListener(this);
    }


    public boolean sortIsRunning() {
        synchronized (sortIsRunningLock) {
            return sortIsRunning;
        }
    }

    public void setSortRunning(boolean isRunning){
        synchronized (sortIsRunningLock) {
            sortIsRunning = isRunning;
        }
    }


}
