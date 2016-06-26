package es.uoproject.pliskid.modelo;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.uoproject.pliskid.activities.Launcher;
import es.uoproject.pliskid.R;
import es.uoproject.pliskid.evento.AppClickListener;
import es.uoproject.pliskid.evento.MyOnTouchListener;
import es.uoproject.pliskid.util.SerializableData;
import es.uoproject.pliskid.util.Serialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Clase serializable que conforma el modelo de una aplicación y su funcionalidad.
 *
 */
public class Pack implements Serializable{

    private static final int INDEX_CHILD = 0;
    private static final long REP_DELAY = 1;
    //To serialize we can't use the Drawable. We create coordinates x, y and a String with the icon path directory
    transient Drawable icon;
    String name;
    String packageName;
    String label;


    public Drawable getIcon() {
        return icon;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    int x, y;
    String iconPath;

    public String getPackageName() {
        return packageName;
    }
    public String getName() {
        return name;
    }

    /**
     * Método que cachea la imagen
     */
    public void cache(){
        if (iconPath==null){
            //Create directory for saving cached icons of the apps
            new File(Launcher.getActivity().getApplicationInfo().dataDir+"/CachedApps/").mkdirs();
        }
        if (icon != null){
            iconPath=Launcher.getActivity().getApplicationInfo().dataDir+"/CachedApps/"+packageName+name;
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(iconPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fileOutputStream!=null){
                //We compress the icon image (quality 0-100 but for png it's ignored)
                drawableToBitmap(icon).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else
                iconPath=null; //Prevents repetition of failure
        }
    }

    public void deleteCached(){
        if(iconPath!=null)
            new File(iconPath).delete();
    }

    /**
     * Método que recoge la imagen de caché
     */
    public Bitmap getCached(){

        //We get a bitmap of the icon stored
        //We use BitmapFactory to get the bitmap from the file cached
        //BitmapFactory.Options will help us configure the decoded bitmap
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=false; //If set to true, the decoder will return null (no bitmap), but the out...
        options.inPreferredConfig= Bitmap.Config.ARGB_8888; //If this is non-null, the decoder will try to decode into this internal configuration.
        options.inDither=true; //If dither is true, the decoder will attempt to dither the decoded image.

        if (iconPath!=null){
            File iconCached=new File(iconPath);
            if (iconCached.exists())
                //If we really have an icon and a file: we do decodeFile with our options
                return BitmapFactory.decodeFile(iconCached.getAbsolutePath(), options);
            else
                return null;
        }else
            return null;

    }

    /**
     * Método que transforma un Drawable en Bitmap
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable){
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        //If it isn't drawable we have to create the bitmap (Config.ARGB_8888)
        Bitmap bitmap=Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        //1.We use Canvas to draw the bitmap
        Canvas canvas=new Canvas(bitmap);
        //2.Set the bounds
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        //3.And simply draw it
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Método que ubica nuestro objeto aplicación en el entorno. Establece su funcionalidad y lo guarda en el archivo de persistencia
     * @param context
     * @param home
     */
    public void addToHome(final Context context, final RelativeLayout home){
        final RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //the coordinates are float
        layoutParams.leftMargin=x +25;
        layoutParams.topMargin=y +75;

        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        //With the inflater we get the linear layout
        final LinearLayout linearLayout=(LinearLayout) inflater.inflate(R.layout.drawer_item, null);
        //We insert the view we recieve with the event click to the new layout

        if(icon==null){
            icon=new BitmapDrawable(context.getResources(), getCached());
        }
        ((ImageView)linearLayout.findViewById(R.id.icon_image)).setImageDrawable(icon);
        ((TextView)linearLayout.findViewById(R.id.icon_text)).setText(label);

        //In order to avoid apps for launching when drag and drop using longclick
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {


                linearLayout.setOnTouchListener(new MyOnTouchListener());

                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(v.isPressed()) {
                            Activity activity=(Activity)context;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    SerializableData serialize= Serialization.loadSerializableData();
                                    if (serialize==null)
                                        serialize=new SerializableData();
                                    if (serialize.packs==null)
                                        serialize.packs=new ArrayList<Pack>();
                                    for (Iterator<Pack> iterator = serialize.packs.iterator(); iterator.hasNext(); ) {
                                        Pack p = iterator.next();
                                        if(p.getPackageName().equals(Pack.this.getPackageName()))
                                            iterator.remove();
                                    }
                                    Serialization.serializeData(serialize);
                                    home.removeView(v);

                                }
                            });
                        }
                        else {
                            //SERIALIZE
                            SerializableData serialize= Serialization.loadSerializableData();
                            if (serialize==null)
                                serialize=new SerializableData();
                            if (serialize.packs==null)
                                serialize.packs=new ArrayList<Pack>();

                            for (Iterator<Pack> iterator = serialize.packs.iterator(); iterator.hasNext(); ) {
                                Pack p = iterator.next();
                                if(p.getPackageName().equals(Pack.this.getPackageName()))
                                    iterator.remove();
                            }
                            Pack.this.setX((int) v.getX());
                            Pack.this.setY((int) v.getY());
                            serialize.packs.add(Pack.this);
                            Serialization.serializeData(serialize);
                            this.cancel();
                        }
                    }
                },5000,6000);



                /*final ProgressBar bar= new ProgressBar(context,
                        null,
                        android.R.attr.progressBarStyleHorizontal);
                home.addView(bar, layoutParams);*/
                final Launcher activity=(Launcher)context;
                activity.setProgressbar(0);

                final CountDownTimer cdt = new CountDownTimer(5000, 200) {

                    public void onTick(long millisUntilFinished) {

                        if(v.isPressed()) {
                            int current = (5 - (int)(millisUntilFinished / 1000.0f));
                            activity.setProgressbar(current);
                        }else {
                            onFinish();
                            this.cancel();
                        }
                    }

                    public void onFinish() {
                        activity.removeProgressbar();
                    }
                }.start();



                if(!v.isPressed()) {
                    timer.cancel();
                    cdt.onFinish();
                }
                return true;
            }
        });
        linearLayout.setOnClickListener(new AppClickListener(context));

        linearLayout.setTag(this);
        //We put it into our home view in the same coordinates with the layourParams
        home.addView(linearLayout, INDEX_CHILD, layoutParams);
        home.bringChildToFront(linearLayout);
    }

}
