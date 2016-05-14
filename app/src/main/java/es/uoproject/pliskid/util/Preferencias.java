package es.uoproject.pliskid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * Created by favila on 14/5/16.
 */
public class Preferencias {

    private Context mContext;

    //Persistencia de datos
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String SHARED_PREFS_FILE = "UsuarioPrefs";

    private static final String PASSWORD_KEY = "password";
    private static final String USER_IMAGE_KEY= "userImage";

    public Preferencias(Context context){
        mContext = context;
    }

    private SharedPreferences getSettings(){
        return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
    }

    public void clearPrefs(){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.clear();
        editor.commit();
    }

    public String getUserPass(){
        return getSettings().getString(PASSWORD_KEY, null);
    }

    public void setUserPass(String password){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(PASSWORD_KEY, password );
        editor.commit();
    }

    public String getUserImage(){
        return getSettings().getString(USER_IMAGE_KEY, null);
    }

    public void setUserImage(Uri selectedImage) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(USER_IMAGE_KEY, String.valueOf(selectedImage));
        editor.commit();
    }

}
