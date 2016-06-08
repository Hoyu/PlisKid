package es.uoproject.pliskid.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by favila on 8/6/16.
 */
public class IncomingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

            Method m1 = tm.getClass().getDeclaredMethod("getITelephony");
            m1.setAccessible(true);
            Object iTelephony = m1.invoke(tm);

            Method m2 = iTelephony.getClass().getDeclaredMethod("silenceRinger");
            Method m3 = iTelephony.getClass().getDeclaredMethod("endCall");

            m2.invoke(iTelephony);
            m3.invoke(iTelephony);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}