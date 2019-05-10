package HowdyIO.PartybAUX.Views;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import HowdyIO.PartybAUX.Tools.QuickTools;
import PartyBauxUI.R;

/**
 * Created by Chris on 4/6/2019.
 */

public class PartyPopup extends RelativeLayout {

    private Context context;
    AlertDialog.Builder builder;
    View root;
    ViewGroup container;
    View parentLayout;
    ViewGroup popup;
    ViewGroup header;
    CustomTextView title;
    LinearLayout wPopupContent;
    ViewGroup contentTop;
    ViewGroup contentBottom;
    ViewGroup wIcon;
    ImageView icon;
    CustomTextView description;
    CustomTextView subHeader;
    CustomEditText input;
    CardView wButtonPos;
    CardView wButtonNeg;
    CustomTextView textPos;
    CustomTextView textNeg;

    private float originalY = -1;

    private boolean isInput;
    public static boolean keyboardShowing;

    public static boolean isOpen;


    public PartyPopup(Context context, View parent) {
        super(context);
        parentLayout = parent;
        sharedConst(context);
    }

    public PartyPopup(Context context) {
        super(context);
        sharedConst(context);
    }

    public PartyPopup(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConst(context);
    }

    public PartyPopup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConst(context);
    }

    public PartyPopup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        sharedConst(context);
    }

    private void sharedConst(Context context) {
        this.context = context;
        root = inflate(context, R.layout.view_party_popup, this);
        ((ViewGroup) parentLayout).addView(root);
        container = root.findViewById(R.id.background);
        container.setAlpha(0f);
        popup = root.findViewById(R.id.popup_container);
        header = root.findViewById(R.id.container_header);
        title = root.findViewById(R.id.textview_title);
        wPopupContent = root.findViewById(R.id.wrapper_popup_content);
        contentTop = root.findViewById(R.id.container_content_top);
        contentBottom = root.findViewById(R.id.container_content_bottom);
        wIcon = root.findViewById(R.id.wrapper_icon);
        icon = root.findViewById(R.id.imageview_icon);
        description = root.findViewById(R.id.textview_description);
        subHeader = root.findViewById(R.id.textview_sub_header);
        input = root.findViewById(R.id.edittext_input);
        wButtonPos = root.findViewById(R.id.wrapper_button_positive);
        textPos = root.findViewById(R.id.textview_positive);
        wButtonNeg = root.findViewById(R.id.wrapper_button_negative);
        textNeg = root.findViewById(R.id.textview_negative);

        setUpDefaultClickListeners();
        root.setVisibility(GONE);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpDefaultClickListeners() {
        OnClickListener dismissClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyboardShowing) {
                    hideKeyboard(context, input.getWindowToken());
                    input.setShouldConsumeBackPress(true);
                    slideForKeyboard(false);
                } else
                    dismiss();
            }
        };
        container.setOnClickListener(dismissClickListener);
        wButtonPos.setOnClickListener(dismissClickListener);
        wButtonNeg.setOnClickListener(dismissClickListener);
        input.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        keyboardShowing = true;
                        input.setShouldConsumeBackPress(false);
                        slideForKeyboard(true);
                        break;
                }
                return false;
            }

        });
        input.setBackPressedListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyboardShowing) {
                    input.setShouldConsumeBackPress(true);
                    keyboardShowing = false;
                    slideForKeyboard(false);
                } else {
                    input.setShouldConsumeBackPress(false);
                    dismiss();
                }
            }
        });
    }

    public PartyPopup setTitle(String title) {
        this.title.setText(title);

        return this;
    }

    public void setIcon(Drawable drawable){
        if(drawable == null){
            wPopupContent.setWeightSum(1);
            wIcon.setVisibility(GONE);
            return;
        }

        wPopupContent.setWeightSum(2);
        icon.setBackground(drawable);
    }

    public PartyPopup setDescription(String description){
        this.description.setText(description);

        return this;
    }

    public PartyPopup setSubHeader(String subHeader){
        this.subHeader.setText(subHeader);

        return this;
    }

    public PartyPopup setInputHint(String hint){
        this.input.setHint(hint);

        return this;
    }

    public PartyPopup setIsInput(boolean isInput){
        this.isInput = isInput;
        if(isInput){
            wIcon.setVisibility(GONE);
            description.setVisibility(GONE);
            subHeader.setVisibility(VISIBLE);
            input.setVisibility(VISIBLE);
            setContentWeightSum(2);
            setContentTopWeight(1);
            setContentBottomWeight(1);
        }else{
            wIcon.setVisibility(VISIBLE);
            description.setVisibility(VISIBLE);
            subHeader.setVisibility(GONE);
            input.setVisibility(GONE);
            setContentWeightSum(3);
            setContentTopWeight(2);
            setContentBottomWeight(1);
        }

        return this;
    }

    public PartyPopup setContentWeightSum(int weightSum){
        wPopupContent.setWeightSum(weightSum);

        return this;
    }

    public PartyPopup setContentTopWeight(int weight){
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) contentTop.getLayoutParams();
        lp.weight = weight;
        contentTop.setLayoutParams(lp);

        return this;
    }

    public PartyPopup setContentBottomWeight(int weight){
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) contentBottom.getLayoutParams();
        lp.weight = weight;
        contentBottom.setLayoutParams(lp);

        return this;
    }

    public PartyPopup setPositiveText(String text) {
        textPos.setText(text);

        return this;
    }

    public PartyPopup setPositiveClickListener(OnClickListener onPositiveClickListener) {
        wButtonPos.setOnClickListener(onPositiveClickListener);

        return this;
    }

    public PartyPopup setNegativeText(String text) {
        textNeg.setText(text);

        return this;
    }

    public PartyPopup setNegativeClickListener(OnClickListener onNegativeClickListener) {
        wButtonNeg.setOnClickListener(onNegativeClickListener);

        return this;
    }

    public String getInput(){
        return input.getText().toString();
    }

    public PartyPopup show() {
        root.setVisibility(VISIBLE);
        container.setAlpha(1f);
        popup.setAlpha(0f); //TODO some animation
        container.getBackground().setAlpha(0);
        ValueAnimator bgAlphaAnim = ValueAnimator.ofInt(0, 255).setDuration(QuickTools.ANIM_TIME_SHORT);
        bgAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                container.getBackground().setAlpha((int) animation.getAnimatedValue());
            }
        });
        bgAlphaAnim.start();
        popup.animate().alpha(1f).setStartDelay(100).setDuration(QuickTools.ANIM_TIME_SHORT).start();
        container.setClickable(true);
        container.setFocusable(true);
        keyboardShowing = false;
        input.setShouldConsumeBackPress(true);
        input.requestFocus();

        isOpen = true;

/*        if(parentLayout != null) {
            ViewGroup.LayoutParams popupLP = popup.getLayoutParams();
            popupLP.width = (int) (parentLayout.getWidth() * 0.8f);
            popupLP.height = (int) (parentLayout.getHeight() * 0.6f);
        }*/

        return this;
    }

    public void dismiss() {
        container.getBackground().setAlpha(0);
        container.setClickable(false);
        container.setFocusable(false);
        root.setVisibility(GONE);
        hideKeyboard(context, input.getWindowToken());
        slideForKeyboard(false);

        isOpen = false;
    }

    public void setKeyboardShowing(boolean keyboardShowing){
        PartyPopup.keyboardShowing = keyboardShowing;
        if(originalY != -1)
            slideForKeyboard(keyboardShowing);
    }

    public static void hideKeyboard(Context context, IBinder token){
        keyboardShowing = false;
        InputMethodManager imm = ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE));
        if(imm != null) imm.hideSoftInputFromWindow(token, 0);
    }

/*    int moveAmt = -1;
    boolean isSlidUp;
    private void slideForKeyboard(boolean slideUp){
        if(moveAmt == -1) moveAmt = header.getHeight();

        if(slideUp && !isSlidUp)
            popup.animate().yBy(moveAmt).setDuration(150).start();
        else if(!slideUp && isSlidUp)
            popup.animate().yBy(-moveAmt).setDuration(150).start();

        isSlidUp = slideUp;
    }*/

    private void slideForKeyboard(boolean slideUp){
        if(originalY == -1) originalY = getY();

        if(slideUp)
            popup.animate().y(header.getHeight()).setDuration(150).start();
        else
            popup.animate().translationY(originalY).setDuration(150).start();

    }
}
