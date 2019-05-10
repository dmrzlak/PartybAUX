package HowdyIO.PartybAUX.Views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Chris on 2/3/2019.
 */

public class TwoWayAnimatedDrawable{
    private ImageView background;
    private AnimatedVectorDrawable state1;
    private AnimatedVectorDrawable state2;
    private AnimatedVectorDrawable currentState;

    private StateChangedListener stateChangedListener;
    private ArrayList<Animatable2.AnimationCallback> animationCallbacks;
    private Animatable2.AnimationCallback twoWayCallBackState1;
    private Animatable2.AnimationCallback twoWayCallBackState2;
    private Animatable2.AnimationCallback loopCallBackState1;
    private Animatable2.AnimationCallback loopCallBackState2;
    private boolean isRunning;

    public enum DrawableState{
        STATE1, STATE2
    }

    public TwoWayAnimatedDrawable(ImageView imageView, AnimatedVectorDrawable state1, AnimatedVectorDrawable state2){
        this.state1 = state1;
        this.state2 = state2;

        currentState = state1;
        (background = imageView).setBackground(state1);

        setUpAnimCallbacks();
    }

    private void setUpAnimCallbacks(){
        animationCallbacks = new ArrayList<>();
        state1.registerAnimationCallback(twoWayCallBackState1 = new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                background.setBackground(currentState = state2);
                state1.reset();

                if(stateChangedListener != null)stateChangedListener.onStateChanged(DrawableState.STATE1, DrawableState.STATE2);
            }
        });
        state2.registerAnimationCallback(twoWayCallBackState2 = new Animatable2.AnimationCallback() {
            @Override
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                background.setBackground(currentState = state1);
                state2.reset();

                if(stateChangedListener != null)stateChangedListener.onStateChanged(DrawableState.STATE2, DrawableState.STATE1);
            }
        });
        animationCallbacks.add(twoWayCallBackState1);
        animationCallbacks.add(twoWayCallBackState2);
    }

    public TwoWayAnimatedDrawable start(){
        if(twoWayCallBackState1 == null || twoWayCallBackState2 == null) setUpAnimCallbacks();
        if(background.getBackground() != currentState){
            background.setBackground(currentState);
        }
        currentState.start();

        isRunning = true;
        return this;
    }

    public void stop(){
        setLoopMode(LoopMode.NONE);
        for(Animatable2.AnimationCallback callback: animationCallbacks){
            state1.unregisterAnimationCallback(callback);
            state2.unregisterAnimationCallback(callback);
        }
        animationCallbacks.clear();
        currentState.stop();
        twoWayCallBackState1 = twoWayCallBackState2 = null;

        isRunning = false;
    }

    enum LoopMode{
        INDEFINITE, NONE
    }

    private LoopMode loopMode = LoopMode.NONE;
    public TwoWayAnimatedDrawable setLoopMode(LoopMode loopMode){
        if(loopCallBackState1 != null)state1.unregisterAnimationCallback(loopCallBackState1);
        if(loopCallBackState2 != null)state2.unregisterAnimationCallback(loopCallBackState2);

        this.loopMode = loopMode;
        if(loopMode == LoopMode.INDEFINITE){
            state1.registerAnimationCallback(loopCallBackState1 = new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    state2.start();
                }
            });
            state2.registerAnimationCallback(loopCallBackState2 = new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    state1.start();
                }
            });
        }
        return this;
    }

    public TwoWayAnimatedDrawable addAnimationCallback(Animatable2.AnimationCallback animationCallback){
        state1.registerAnimationCallback(animationCallback);
        state2.registerAnimationCallback(animationCallback);
        animationCallbacks.add(animationCallback);

        return this;
    }

    public DrawableState getState(){
        return currentState == state1? DrawableState.STATE1: DrawableState.STATE2;
    }
    public void setState(DrawableState state){
        if(state == DrawableState.STATE1){
            background.setBackground(currentState = state1);
        }else{
            background.setBackground(currentState = state2);
        }
        currentState.reset();
    }

    public void setStateChangedListener(StateChangedListener stateChangedListener){
        this.stateChangedListener = stateChangedListener;
    }

    public interface StateChangedListener{
        void onStateChanged(DrawableState lastState, DrawableState currentState);
    }

}
