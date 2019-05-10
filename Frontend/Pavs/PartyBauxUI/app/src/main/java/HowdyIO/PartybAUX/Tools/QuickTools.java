package HowdyIO.PartybAUX.Tools;

/**
 * Created by Chris on 1/24/2019.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.TypedValue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

import HowdyIO.PartybAUX.Activities.PartyActivity;
import HowdyIO.PartybAUX.Model.User;

/**
 * Created by Chris on 8/28/2018.
 */

public class QuickTools {
    public static String userName = "";
    public static String hostName = "";
    public static int partyID = -1;
    public static int userType = -1;

    public static final int ROLE_HOST = 2;
    public static final int ROLE_MOD = 1;
    public static final int ROLE_GUEST = 0;

    public static final String SHARED_PREFS_USER = "user";
    public static final String SHARED_PREFS_USERTYPE = "user_type";

    public static final int ANIM_TIME_SHORT = 250;
    public static final int ANIM_TIME_DEFAULT = 250;
    public static final int ANIM_TIME_LONG = 400;

    private static String[] niceGradients = new String[]{"0072ff 00c6ff |BlueOne", "18334F FD746C |DBlueToRed", "4D034B aa076b |DPurpleToMagenta",
            "f83600 fe8c00 |DOrangeToOrange", "000428 004e92 |DBlueToLBlue", "A81B24 7b4397 |RedToLPurple", "185a9d 43cea2 |BlueToSeaGreen",
            "19547b ffd89b |BlueToYellow", "3a1c71 d76d77 ffaf7b |DPurpleToPinkToPeach", "9708cc 43cbff |PurpleToLBlue"};

    private static String[] happyAdjectives = new String[]{"cute", "sexy", "hot", "great", "nice",
            "pretty cool", "cool", "awfully amazing", "amazing", "good", "fresh", "\"lit\"", "sick",
            "lovely", "wild", "", "you love", "popular", "unique", "classic", "memorable", "wonderful",
            "provocative", "dope", "explosive", "explicit", "chill", "neat", "grooovy", "marvelous", "glorious",
            "nifty", "thrifty", "schwifty", "fire", "crazy", "dapper", "upbeat",
            "spicy", "hip", "exceptional"};

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


    public static SharedPreferences sharedPrefs(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
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

    public static User parseUser(String result) {
        User user = new User();
        try {
            JSONObject json = new JSONObject(result);
            user.setUserID(json.getInt("client_id"));
            user.setUsername(json.getString("username"));
            user.setEmail(json.getString("email"));
            user.setPartyID(json.getInt("partyid"));
            user.setUserType(json.getInt("userTypeid"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return user;
    }

    public static void saveUser(Context context, User user) {
        JSONObject json = new JSONObject();
        try {
            json.put(User.SHARED_PREFS_USERNAME, user.getUsername() );
            json.put(User.SHARED_PREFS_PASSWORD, user.getPassword() );
            json.put(User.SHARED_PREFS_TOKEN, user.getToken() );

            QuickTools.sharedPrefs(context).edit().putString(SHARED_PREFS_USER , json.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static User getSavedUser(Context context){
        User user = null;
        try {
            JSONObject jsonObject = new JSONObject(QuickTools.sharedPrefs(context).getString(SHARED_PREFS_USER, ""));
            if (jsonObject.length() == 0) return user;

            user = new User(jsonObject);
            userName = user.getUsername();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static String getRandomAdjective() {
        int randomNum = new Random().nextInt(happyAdjectives.length);
        return happyAdjectives[randomNum];
    }

    public static boolean trackUriEquals(String trackUri1, String trackUri2) {
        String annoyingSpotifyInfo = "spotify:track:";
        if(trackUri1.contains(annoyingSpotifyInfo)) trackUri1 = trackUri1.substring(annoyingSpotifyInfo.length());
        if(trackUri2.contains(annoyingSpotifyInfo)) trackUri2 = trackUri2.substring(annoyingSpotifyInfo.length());

        return trackUri1.equals(trackUri2);
    }
}
