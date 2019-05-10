package HowdyIO.PartybAUX.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import PartyBauxUI.R;

/**
 * Created by Chris on 1/23/2019.
 */

public class CustomTextView extends AppCompatTextView {

    private Context context;

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public CustomTextView(Context context) {
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
        }catch (RuntimeException e){
            Log.e("No Asset","Asset not found for custom textview font");
        }


    }
}
