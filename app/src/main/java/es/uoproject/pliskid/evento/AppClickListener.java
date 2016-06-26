package es.uoproject.pliskid.evento;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import es.uoproject.pliskid.activities.Launcher;
import es.uoproject.pliskid.modelo.Pack;

/**
 * Clase que establece el comportamiento al pulsar sobre una aplicación
 */
public class AppClickListener implements View.OnClickListener {

    Context context;

    public AppClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        //We use .Tag() to obtain the info for the position in the array
        Pack data= (Pack) v.getTag();

        Intent launchIntent=new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName componentName=new ComponentName(data.getPackageName(), data.getName());
        launchIntent.setComponent(componentName);
        context.startActivity(launchIntent);

    }
}
