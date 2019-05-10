package HowdyIO.PartybAUX.Tools;

/**
 * Created by Chris on 2/12/2019.
 */

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Detects left and right swipes across a view.
 */
public class SwipeGestureDetector implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public int swipeType;

    public static final int SWIPE_TOP = 0;
    public static final int SWIPE_BOTTOM = 1;
    public static final int SWIPE_RIGHT = 2;
    public static final int SWIPE_LEFT = 3;
    public static final int SWIPE_NONE = 4;


    public SwipeGestureDetector(Context context){
        gestureDetector = new GestureDetector(context, new GestureListener());
    }


    public boolean onTouch(final View v, final MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public void setSwipeListener() {
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            result = onSwipeRight();
                        } else {
                            result = onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            result = onSwipeBottom();
                        } else {
                            result = onSwipeTop();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            if(!result)swipeType = SWIPE_NONE;
            return result;
        }
    }

    public boolean onSwipeRight() {
        return false;
    }

    public boolean onSwipeLeft() {
        return false;
    }

    public boolean onSwipeTop() {
        swipeType = SWIPE_TOP;
        return true;
    }

    public boolean onSwipeBottom() {
        swipeType = SWIPE_BOTTOM;
        return true;
    }

    public interface OnSwipeListener{
        void onSwipeRight();
        void onSwipeLeft();
        void onSwipeTop();
        void onSwipeBottom();

    }
}