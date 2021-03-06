package es.uoproject.pliskid.evento;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import es.uoproject.pliskid.modelo.Pack;

/**
 * Clase que establece la funcionalidad al pulsar sobre un atajo
 */
public class ShortcutClickListener implements View.OnClickListener {

    Context context;

    public ShortcutClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        //We use .Tag() to obtain the info for the position in the array
        Intent data= (Intent) v.getTag();
        context.startActivity(data);

    }
}