package utilities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by christian on 09/02/18.
 */

public class PrefUtil {

    private Activity activity;
    public static final String ID = "ID";
    public static final String PROFILE = "PICTURE";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String GENDER = "GENDER";
    public static final String BIRTHDAY = "BIRTHDAY";
    public static final String TELP = "TELP";
    public static final String ALAMAT = "ALAMAT";
    public static final String LOGIN = "LOGIN";
    public static final String FB = "FB";
    public static final String PHONE = "PHONE";
    public static final String GL = "GOOGLE";
    public static final String OTHER = "EMAIL";
    public static final String COUNTTOKO = "COUNT_TOKO";

    // Constructor
    public PrefUtil(Activity activity) {
        this.activity = activity;
    }

    public void clear() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void saveUserInfo(String name, String email, String id, String gender, String foto,
                                  String login, String birthday, String telp, String alamat,
                             Integer countToko){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(GENDER, gender);
        editor.putString(PROFILE, foto);
        editor.putString(BIRTHDAY, birthday);
        editor.putString(ALAMAT, alamat);
        editor.putString(TELP, telp);
        editor.putString(ID, id);
        editor.putString(LOGIN, login);
        editor.putInt(COUNTTOKO, countToko);
        editor.apply(); // This line is IMPORTANT !!!
    }

    public SharedPreferences getUserInfo(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        //Log.d("MyApp", "Name : "+prefs.getString("fb_name",null)+"\nEmail : "+prefs.getString("fb_email",null));
        return prefs;
    }
}
