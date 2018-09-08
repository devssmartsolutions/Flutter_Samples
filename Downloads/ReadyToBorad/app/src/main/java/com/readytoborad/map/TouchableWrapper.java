package com.readytoborad.map;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.widget.FrameLayout;

/**
 * Created by harendrasinghbisht on 11/02/17.
 */

public class TouchableWrapper extends FrameLayout {
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private VelocityTracker mVelocityTracker;
    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            //Notify the event bus (I am using Otto eventbus of course) that you have just received a double-tap event on the map, inside the event bus event listener
            EventBus_Singleton.getInstance().post(new EventBus_Poster("double_tapped_map"));

            return true;
        }
    };

    public TouchableWrapper(Context context) {
        super(context);
        mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mVelocityTracker=VelocityTracker.obtain();
    }

    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {


        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations.
         */

        float startingSpan;
        float startFocusX;
        float startFocusY;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            startingSpan = scaleGestureDetector.getCurrentSpan();
            startFocusX = scaleGestureDetector.getFocusX();
            startFocusY = scaleGestureDetector.getFocusY();

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scale = scaleGestureDetector.getCurrentSpan() / startingSpan;

            mVelocityTracker.computeCurrentVelocity(1000);

            Log.d("VELOCITY", "X vel : " + mVelocityTracker.getXVelocity());
            Log.d("VELOCITY", "Y vel : " + mVelocityTracker.getYVelocity());

            if (scale <= 1.0) {
                EventBus_Singleton.getInstance().post(new EventBus_Poster("pinched_map", "out"));
            } else {
                EventBus_Singleton.getInstance().post(new EventBus_Poster("pinched_map", "in"));
            }

            return true;
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        mScaleGestureDetector.onTouchEvent(ev);

        return super.onInterceptTouchEvent(ev);
    }
}
