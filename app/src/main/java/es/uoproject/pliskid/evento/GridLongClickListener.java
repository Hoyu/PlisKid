package es.uoproject.pliskid.evento;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import es.uoproject.pliskid.activities.Launcher;
import es.uoproject.pliskid.modelo.Pack;
import es.uoproject.pliskid.R;
import es.uoproject.pliskid.util.SerializableData;
import es.uoproject.pliskid.util.Serialization;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

/**
 * Clase que establece la funcionalidad al mantener pulsado sobre una aplicación dentro del listado de aplicaciones
 */
public class GridLongClickListener implements AdapterView.OnItemLongClickListener {

    Context context;
    SlidingDrawer slidingDrawer;
    RelativeLayout home;
    Pack[] packs;
    boolean once=true;

    public GridLongClickListener(Context context, SlidingDrawer slidingDrawer, RelativeLayout home, Pack[] packs) {
        this.context = context;
        this.slidingDrawer = slidingDrawer;
        this.home = home;
        this.packs=packs;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

        /*
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(view.getWidth(), view.getHeight());
        //the coordinates are float
        layoutParams.leftMargin=(int)view.getX();
        layoutParams.topMargin=(int)view.getY();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        //With the inflater we get the linear layout
        final LinearLayout linearLayout=(LinearLayout) inflater.inflate(R.layout.drawer_item, null);
        //We insert the view we recieve with the event click to the new layout
        ((ImageView)linearLayout.findViewById(R.id.icon_image)).setImageDrawable(((ImageView)view.findViewById(R.id.icon_image)).getDrawable());
        ((TextView)linearLayout.findViewById(R.id.icon_text)).setText(((TextView) view.findViewById(R.id.icon_text)).getText());
        //Add the OnTouch event for Drag and Drop
        linearLayout.setOnTouchListener(new MyOnTouchListener());
        //In order to avoid apps for launching when drag and drop using longclick
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                linearLayout.setOnTouchListener(new MyOnTouchListener());
                return true;
            }
        });
        linearLayout.setOnClickListener(new AppClickListener(context));
*/
        //Add the click and launch app event
        String[] data=new String[2];
        data[0]=packs[position].getPackageName();
        data[1]=packs[position].getName();



        //Set the coordinates for the pack
        Pack serializePack = packs[position];
        serializePack.setX((int) view.getX());
        serializePack.setY((int) view.getY());
        serializePack.cache();

        //SERIALIZE
        SerializableData serialize= Serialization.loadSerializableData();
        if (serialize==null)
            serialize=new SerializableData();
        if (serialize.packs==null)
            serialize.packs=new ArrayList<Pack>();
        if(serialize.packs.contains(serializePack))
            serialize.packs.remove(serializePack);
        serialize.packs.add(serializePack);
        Serialization.serializeData(serialize);



        //We put it into our home view in the same coordinates with the layourParams
        serializePack.addToHome(context, home);
        //When we do this, we want our slide to close automatically
        slidingDrawer.animateClose();
        //Now we have to ensure that the new icon doesn't appear over the slidingDrawer
        slidingDrawer.bringToFront();
        //return true should stop onitemclicklistener

        if(once) {
            once=false;
            Launcher launcher = (Launcher) context;
            launcher.tutorial("Para mover o borrar manten pulsado el icono");
        }
        return true;
    }
}