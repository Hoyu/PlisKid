package es.uoproject.pliskid.util;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Servicio que recibe las notificaciones
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();

    private NLServiceReceiver nlservicereciver;

    @Override

    public void onCreate() {

        super.onCreate();

        nlservicereciver = new NLServiceReceiver();

        IntentFilter filter = new IntentFilter();

        filter.addAction("es.uoproject.pliskid.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");

        registerReceiver(nlservicereciver,filter);

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        Intent i = new  Intent("es.uoproject.pliskid.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName() + "\n");
        sendBroadcast(i);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
        Intent i = new  Intent("es.uoproject.pliskid.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");

        sendBroadcast(i);
    }



    @Override

    public void onDestroy() {

        super.onDestroy();

        unregisterReceiver(nlservicereciver);

    }


    class NLServiceReceiver extends BroadcastReceiver {

        @Override

        public void onReceive(Context context, Intent intent) {

            NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationListener.this.cancelAllNotifications();

        }

    }
}

