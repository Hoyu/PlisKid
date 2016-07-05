package es.uoproject.pliskid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * Clase que gestiona el almacenamiento en SharedPreferences
 */
public class Preferencias {

    private Context mContext;

    //Persistencia de datos
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String SHARED_PREFS_FILE = "UsuarioPrefs";

    private static final String LOCK_STATUS_BAR_KEY = "lockStatusBar";
    private static final String LOCK_INCOMING_CALLS_KEY = "lockCalls";
    private static final String PASSWORD_KEY = "password";
    private static final String USER_IMAGE_KEY= "userImage";

    public Preferencias(Context context){
        mContext = context;
    }

    /**
     * Método que establece el fichero y su privacidad
     * @return
     */
    private SharedPreferences getSettings(){
        return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
    }

    /**
     * Método que borra las SharedPreferences
     */
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

    public void removeKey (String key){
        getSettings().edit().remove(key).commit();
    }

    public String getUserImage(){
        return getSettings().getString(USER_IMAGE_KEY, null);
    }

    public void setUserImage(Uri selectedImage) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(USER_IMAGE_KEY, String.valueOf(selectedImage));
        editor.commit();
    }

    public boolean getLockStatusBarKey(){
        return getSettings().getBoolean(LOCK_STATUS_BAR_KEY, false);
    }

    public void setLockStatusBarKey(boolean lockStatusBarKey){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(LOCK_STATUS_BAR_KEY, lockStatusBarKey );
        editor.commit();
    }

    public boolean getLockIncomingCallsKey(){
        return getSettings().getBoolean(LOCK_INCOMING_CALLS_KEY, false);
    }

    public void setLockIncomingCallsKey(boolean lockStatusBarKey){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(LOCK_INCOMING_CALLS_KEY, lockStatusBarKey );
        editor.commit();
    }

}
