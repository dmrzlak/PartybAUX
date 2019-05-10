package HowdyIO.PartybAUX.Tools;

/**
 * Created by Chris on 1/24/2019.
 */

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

/**
 * Created by Chris on 8/28/2018.
 */

public class QuickTools {
    private static String[] niceGradients = new String[]{"0072ff 00c6ff |BlueOne", "18334F FD746C |DBlueToRed", "4D034B aa076b |DPurpleToMagenta",
            "f83600 fe8c00 |DOrangeToOrange", "000428 004e92 |DBlueToLBlue", "A81B24 7b4397 |RedToLPurple", "185a9d 43cea2 |BlueToSeaGreen",
            "19547b ffd89b |BlueToYellow", "3a1c71 d76d77 ffaf7b |DPurpleToPinkToPeach", "9708cc 43cbff |PurpleToLBlue"};

    public static void vibrate(Context context, int ms){
        Vibrator vibrator = ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
        if(vibrator == null)return;
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(ms);
        }
    }

    public static int convertDpToPx(Context context, int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float convertDpToPx(Context context, float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String getRandomGradient(){
        int randomNum = new Random().nextInt(niceGradients.length);
        return niceGradients[3];
    }

    public static <T extends Serializable> T stringToObjectS(String string) {
        byte[] bytes = Base64.decode(string, 0);
        T object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            object = (T) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static String objectToString(Parcelable object) {
        String encoded = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            encoded = new String(Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoded;
    }
}
