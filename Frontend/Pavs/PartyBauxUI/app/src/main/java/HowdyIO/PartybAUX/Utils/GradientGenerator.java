package HowdyIO.PartybAUX.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.animation.DecelerateInterpolator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import PartyBauxUI.R;
import de.androidpit.androidcolorthief.MMCQ;

/**
 * Created by Chris on 4/23/2019.
 */

public class GradientGenerator {

    private static GradientDrawable lastGeneratedGradient;

    private static GradientCallBack callBack;

    public static void generateGradient(Context context, Drawable reference, GradientCallBack gradientCallBack){
        callBack = gradientCallBack;
        new GradientTask(context).execute(reference);
    }

    public static GradientDrawable getLastGeneratedGradient(){
        return lastGeneratedGradient;
    }

    public interface GradientCallBack{
        void onResult(GradientDrawable gradientDrawable);
        void onError();
    }


    private static class GradientTask extends AsyncTask<Drawable, Void, GradientDrawable> {

        private WeakReference<Context> contextRef;

        public GradientTask(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected GradientDrawable doInBackground(Drawable... drawables) {
            List<int[]> result = new ArrayList<>();
            Bitmap imgBitmap = ((BitmapDrawable) drawables[0]).getBitmap();
            try {
                result = MMCQ.compute(imgBitmap, 5);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            Context context = contextRef.get();
            if(context == null) return null;

            GradientDrawable gradientDrawable = new GradientDrawable();
            int[] dominantColor = result.get(0);
            int domColorInt = Color.rgb(dominantColor[0], dominantColor[1], dominantColor[2]);
            int primary = ContextCompat.getColor(context, R.color.colorPrimary);
            gradientDrawable.setColors(new int[]{domColorInt, ContextCompat.getColor(context, R.color.background), primary, primary});
            //gradientDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
            gradientDrawable.setGradientRadius(4000);
            gradientDrawable.setGradientCenter(0.5f, -0.1f);

            return gradientDrawable;
        }

        @Override
        protected void onPostExecute(GradientDrawable drawable) {
            if (drawable == null)
                callBack.onError();

            lastGeneratedGradient = drawable;
            callBack.onResult(drawable);
            super.onPostExecute(drawable);
        }
    }
}
