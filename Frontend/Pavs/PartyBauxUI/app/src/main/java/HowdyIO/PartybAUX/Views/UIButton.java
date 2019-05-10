package HowdyIO.PartybAUX.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import PartyBauxUI.R;

/**
 * Created by Chris on 3/10/2019.
 */

public class UIButton extends CardView {

    Context context;
    View root;

    FrameLayout backgroundGradient;
    TextView title;
    ImageView drawableAnimR;
    TwoWayAnimatedDrawable animatedDrawable;

    boolean checked;

    OnUIButtonClickListener onClickListener;

    public UIButton(@NonNull Context context) {
        super(context);
        sharedConst(context, null);
    }

    public UIButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        sharedConst(context, attrs);
    }

    public UIButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConst(context, attrs);
    }

    private void sharedConst(Context context, AttributeSet attrs){
        this.context = context;
        root = inflate(getContext(), R.layout.view_song_details, this);
        //root = LayoutInflater.from(context).inflate(R.layout.view_song_details, this, false);
        root.post(new Runnable() {
            @Override
            public void run() {
                init();

                if(attrs != null) {
                    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UIButton);
                    String givenTitle = a.getString(R.styleable.UIButton_title);
                    if(givenTitle != null) title.setText(givenTitle);
                    String givenColor = a.getString(R.styleable.UIButton_buttonColor);
                    if(givenColor != null) setColor(givenColor);

                    a.recycle();
                }
                //setUpGestureDetector();
            }
        });
    }

    private void init(){
        backgroundGradient = root.findViewById(R.id.background_gradient);
        title = root.findViewById(R.id.textview_title);
        drawableAnimR = root.findViewById(R.id.drawableanim_button);

        setRadius(getResources().getDimension(R.dimen.default_rounded_corner));
        setColor("purple");

        setUpOnClicks();
    }

    private void setUpOnClicks() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checked = !checked;
                animateClick();
                onClickListener.onClick(checked);
            }
        });
    }

    private void animateClick() {
        if(animatedDrawable != null) {
            if (drawableAnimR.getVisibility() != VISIBLE)
                animatedDrawable.setState(TwoWayAnimatedDrawable.DrawableState.STATE2);
            else
                animatedDrawable.start();
        }
    }

    public void setTwoWayAnimatedDrawable(@DrawableRes int drawableFirst, @DrawableRes int drawableSecond){
        animatedDrawable = new TwoWayAnimatedDrawable(drawableAnimR,
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, drawableFirst),
                (AnimatedVectorDrawable) ContextCompat.getDrawable(context, drawableSecond));
    }

    public void setColor(String color) {
        if(color.equalsIgnoreCase("purple")){
            backgroundGradient.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_purple_button));
        }else{
            backgroundGradient.setBackground(ContextCompat.getDrawable(context, R.drawable.selector_green_button));
        }
    }

    public void setOnUIButtonClickListener(OnUIButtonClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public interface OnUIButtonClickListener{
        void onClick(boolean isNowChecked);
    }


}
