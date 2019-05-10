package HowdyIO.PartybAUX.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import HowdyIO.PartybAUX.Tools.SwipeGestureDetector;

/**
 * Created by Chris on 3/3/2019.
 */

public class GestureParent extends RelativeLayout {

    private Context context;
    private SwipeGestureDetector swipeGestureDetector;
    private OnSwipeListener onSwipeListener;

    public GestureParent(Context context) {
        super(context);
        sharedConst(context);
    }

    public GestureParent(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConst(context);
    }

    public GestureParent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConst(context);
    }

    public GestureParent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        sharedConst(context);
    }

    private void sharedConst(Context context) {
        this.context = context;
        setUpGestureDetection();
    }

    private void setUpGestureDetection() {
        swipeGestureDetector = new SwipeGestureDetector(context);
        swipeGestureDetector.setSwipeListener();
    }

    boolean eventStarted;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled = onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_UP) return handled;
        return false;
        /*if(ev.getAction() == MotionEvent.ACTION_DOWN)
            eventStarted = false;
        boolean superCall = false;
        superCall = super.onInterceptTouchEvent(ev);
        return false;*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (swipeGestureDetector.onTouch(this, event) && onSwipeListener != null) {
            switch (swipeGestureDetector.swipeType) {
                case SwipeGestureDetector.SWIPE_TOP:
                    onSwipeListener.onSwipeTop();
                    break;
                case SwipeGestureDetector.SWIPE_BOTTOM:
                    onSwipeListener.onSwipeBottom();
                    break;

            }
            eventStarted = true;
            return true;
        }
        if (event.getAction() != MotionEvent.ACTION_UP) return true;
/*        if(event.getAction() == MotionEvent.ACTION_UP) eventStarted = false;
        if(eventStarted) super.dispatchTouchEvent(event);*/

        return false;
}

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

public interface OnSwipeListener {
    void onSwipeRight();

    void onSwipeLeft();

    void onSwipeTop();

    void onSwipeBottom();
}
}
