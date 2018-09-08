package com.readytoborad.map;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by harendrasinghbisht on 18/01/17.
 */

public class MapWrapperLayout extends FrameLayout {
   // private GestureDetectorCompat mGestureDetector;

    public interface OnDragListener {
        public void onDrag(MotionEvent motionEvent);
    }

    private OnDragListener mOnDragListener;

    public MapWrapperLayout(Context context) {
        super(context);
      //  mGestureDetector = new GestureDetectorCompat(context, mGestureListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mOnDragListener != null) {
            mOnDragListener.onDrag(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setOnDragListener(OnDragListener mOnDragListener) {
        this.mOnDragListener = mOnDragListener;
    }

    private final GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //Notify the event bus (I am using Otto eventbus of course) that you have just received a double-tap event on the map, inside the event bus event listener
            // EventBus_Singleton.getInstance().post(new EventBus_Poster("double_tapped_map"));
            return true;
        }
    };


}