package tarc.edu.selfcheckoutapp.UtlityClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LoginPreferenceUtils {

    public static final String KEY_PHONE = "phone";
    public static final String LOGGED_IN_STATUS = "status";
    public static final String PIN_STATUS = "pinStatus";


    public LoginPreferenceUtils() {
    }

    static SharedPreferences getPreferences(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void clear(Context context)
    {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.clear();
        editor.apply();
    }



    public static boolean savePhone(String phone, Context context)
    {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(KEY_PHONE,phone);
        editor.apply();
        return true;
    }

    public static String getPhone(Context context)
    {
        return getPreferences(context).getString(KEY_PHONE,null);
    }


    public static void setLoggedInStatus(Context context,boolean loggedIn)
    {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_STATUS,loggedIn);
        editor.apply();


    }

    public static boolean getLoggedStatus(Context context)
    {
        return getPreferences(context).getBoolean(LOGGED_IN_STATUS,false);
    }

    public static void setPinStatus(Context context,boolean pinStatus)
    {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(PIN_STATUS,pinStatus);
        editor.apply();


    }

    public static boolean getPinStatus(Context context)
    {
        return getPreferences(context).getBoolean(PIN_STATUS,false);
    }


}
