package HowdyIO.PartybAUX.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import PartyBauxUI.R;

/**
 * Created by Chris on 2/6/2019.
 */

public class CustomProgressBar extends RelativeLayout{

    private Context context;
    private View root;
    private RelativeLayout contentView;
    private ImageView progress;
    private TwoWayAnimatedDrawable progressAnim;
    private ValueAnimator colorAnim;
    int[] theme;
    int themeIndex;
    boolean finished = true;
    boolean finishing = false;

    public CustomProgressBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init(){
        root = inflate(getContext(), R.layout.view_custom_progress_bar, this);
        //root = LayoutInflater.from(context).inflate(R.layout.view_song_details, this, false);
        root.post(new Runnable() {
            @Override
            public void run() {
                contentView = root.findViewById(R.id.content_view);
                progress = root.findViewById(R.id.progress_bar_container);
                //root.setVisibility(INVISIBLE);

                setUpBackground();
                setUpDefaultTheme();
                start();
                //setUpColorAnim();
                //setUpDrawableAnim();
            }
        });
    }

    private void setUpBackground() {
        progressAnim = new TwoWayAnimatedDrawable(progress,
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.play_to_pause_offset),
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.pause_to_play_offset));
    }

    private void setUpDefaultTheme(){
        theme = new int[]{ContextCompat.getColor(context, R.color.spotGreen), ContextCompat.getColor(context, R.color.goodPurp)};
        progress.getBackground().setTint(theme[themeIndex]);
        themeIndex++;
    }

    private void animateColors(){
        int nextColor = theme[themeIndex];
        int lastColor = theme[themeIndex == 0? theme.length - 1: themeIndex - 1];
        progress.getBackground().setTint(nextColor);
        colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), lastColor, nextColor).setDuration(2000);
        //colorAnim.setStartDelay(1850);
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(finishing || finished) return;
                int val = (Integer) valueAnimator.getAnimatedValue();
                progress.getBackground().setTint(val);
            }
        });
        themeIndex = themeIndex == theme.length - 1? themeIndex = 0: themeIndex + 1;
        colorAnim.start();
    }

    private Animatable2.AnimationCallback getAnimationCallback(){
        return new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationStart(Drawable drawable) {
                super.onAnimationStart(drawable);
                animateColors();
            }
        };
    }

    public void show(){
        setVisibility(VISIBLE);
    }
    public void hide(){
        setVisibility(INVISIBLE);
    }

    public void start(){
        if(finishing)return;

        finished = false;
        progressAnim.setLoopMode(TwoWayAnimatedDrawable.LoopMode.INDEFINITE).addAnimationCallback(getAnimationCallback());
        contentView.animate().alpha(1f).setDuration(150).start();
        progressAnim.start();

        if(getVisibility() != VISIBLE)finish();
    }

    public void finish(){
        finishing = true;
        if(progressAnim.getState() == TwoWayAnimatedDrawable.DrawableState.STATE2)
            commitFinish();
        else {
            progressAnim.setStateChangedListener(new TwoWayAnimatedDrawable.StateChangedListener() {
                @Override
                public void onStateChanged(TwoWayAnimatedDrawable.DrawableState lastState, TwoWayAnimatedDrawable.DrawableState currentState) {
                    if (currentState == TwoWayAnimatedDrawable.DrawableState.STATE2 && !finished) {
                        commitFinish();
                        progressAnim.setStateChangedListener(null);
                    }
                }
            });
        }

    }

    private void commitFinish(){
        finished = true;
        progressAnim.stop();
        colorAnim.removeAllUpdateListeners();
        colorAnim.cancel();
        AnimatedVectorDrawable finishAnim = (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.partybauxanim);
        progress.setBackground(null);
        progress.setBackground(finishAnim);
        progress.getBackground().setTintList(null);
        progress.getBackground().setColorFilter(null);
        if(finishAnim != null)
            finishAnim.start();
        final ViewPropertyAnimator alphaAnim = contentView.animate().alpha(0f).setStartDelay(1200).setDuration(250);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!finishing){
                    return;
                }
                contentView.setAlpha(0f);
                alphaAnim.setListener(null);
                finishing = false;
            }
        }, 1450);
        alphaAnim.start();
    }

    public void setScale(float scale){
        progress.setScaleX(progress.getScaleX() * scale);
        progress.setScaleY(progress.getScaleY() * scale);
    }

    public void setSize(int widthAndHeight){

    }
}
