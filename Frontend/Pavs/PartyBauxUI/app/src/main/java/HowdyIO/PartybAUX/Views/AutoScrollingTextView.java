package HowdyIO.PartybAUX.Views;

/**
 * Created by Chris on 4/24/2019.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * A TextView that scrolls it contents across the screen, in a similar fashion as movie credits roll
 * across the theater screen.
 *
 * @author Matthias Kaeppler
 *
 */
public class AutoScrollingTextView extends CustomTextView implements Runnable {

    private static final float DEFAULT_SPEED = 10.0f;

    private Scroller scroller;
    private float speed = DEFAULT_SPEED;
    private boolean continuousScrolling = true;
    private Orientation orientation = Orientation.horizontal;
    public enum Orientation{
        horizontal, vertical
    }

    public AutoScrollingTextView(Context context) {
        super(context);
        setup(context);
    }

    public AutoScrollingTextView(Context context, AttributeSet attributes) {
        super(context, attributes);
        setup(context);
    }

    public AutoScrollingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context);
    }

    private void setup(Context context) {
        scroller = new Scroller(context, new LinearInterpolator());
        setScroller(scroller);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (scroller.isFinished()) {
            scroll();
        }
    }

    private void scroll() {
        int range, visibleRange, offset, distance, duration;
        if(orientation == Orientation.horizontal){
            range = getWidth();
            visibleRange = range - getPaddingEnd() - getPaddingStart();
            offset = -visibleRange;

            distance = visibleRange + 12 * getLineHeight();
            duration = (int) (distance * speed);

            scroller.startScroll(offset, 0, distance, 0, duration);
        } else {
            range = getHeight();
            visibleRange = range - getPaddingBottom() - getPaddingTop();

            offset = -visibleRange;

            distance = visibleRange + getLineCount() * getLineHeight();
            duration = (int) (distance * speed);

            scroller.startScroll(0, offset, 0, distance, duration);
        }

        if (continuousScrolling) {
            post(this);
        }
    }

    @Override
    public void run() {
        if (scroller.isFinished()) {
            scroll();
        } else {
            post(this);
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return speed;
    }

    public void setContinuousScrolling(boolean continuousScrolling) {
        this.continuousScrolling = continuousScrolling;
    }

    public boolean isContinuousScrolling() {
        return continuousScrolling;
    }

    public void setOrientation(Orientation orientation){
        this.orientation = orientation;
    }
}