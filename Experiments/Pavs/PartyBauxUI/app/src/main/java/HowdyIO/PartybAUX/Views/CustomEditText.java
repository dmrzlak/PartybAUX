package HowdyIO.PartybAUX.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import PartyBauxUI.R;

/**
 * Created by Chris on 1/23/2019.
 */

public class CustomEditText extends AppCompatEditText {

    private Context context;

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public CustomEditText(Context context) {
        super(context);
        this.context = context;
    }

    private void init(AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);

        String font = a.getString(R.styleable.CustomTextView_custom_font);
        setCustomTypeFace(font);

        a.recycle();
    }

    public void setCustomTypeFace(String path) {
        try {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + path);
            setTypeface(tf ,1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
