package es.uoproject.pliskid.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.service.notification.StatusBarNotification;
import android.support.annotation.DimenRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import es.uoproject.pliskid.R;
import es.uoproject.pliskid.evento.GridClickListener;
import es.uoproject.pliskid.evento.GridLongClickListener;
import es.uoproject.pliskid.evento.MyOnTouchListener;
import es.uoproject.pliskid.evento.ShortcutClickListener;
import es.uoproject.pliskid.fragment.Fragment_NavigationDrawer;
import es.uoproject.pliskid.util.LauncherAppWidgetHost;
import es.uoproject.pliskid.util.LauncherAppWidgetHostView;
import es.uoproject.pliskid.util.NotificationListener;
import es.uoproject.pliskid.util.Preferencias;
import es.uoproject.pliskid.util.SerializableData;
import es.uoproject.pliskid.util.Serialization;
import es.uoproject.pliskid.adapters.DrawerAdapter;
import es.uoproject.pliskid.modelo.Pack;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Launcher extends AppCompatActivity {

    private static final int REQUEST_PICK_APPWIDGET=20;
    private static final int REQUEST_PASSWORD = 1;
    private static final int REQUEST_PICK_SHORTCUT = 2;
    private static final int REQUEST_CREATE_SHORTCUT = 3;
    private static final int CHANGE_PICTURE=10;
    //Grid with all the apps
    GridView grid;
    //Main screen/ user desktop
    RelativeLayout home;
    //the slide screen with the grid
    SlidingDrawer drawer;
    //We need an adapter to draw our screens
    DrawerAdapter drawerAdapter;

    FloatingActionButton fab_salir;

    ImageView fondo_pantalla;

    Preferencias preferencias;

    //With the packageManager we'll get the info of the system to our array of apps
    Pack[] packs;
    PackageManager packageManager;


    AppWidgetManager mAppWidgetManager;
    LauncherAppWidgetHost mAppWidgetHost;
    //To get access to our activity info (SAVING CHANGES IN ACTIVITY)
    static Activity activity;
    boolean version=true;

    private ProgressBar bar;

    private Fragment_NavigationDrawer fragment_drawer;
    private DrawerLayout drawerLayout;
    public boolean longpressed=false;

    public static Activity getActivity() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launchAppChooser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_launcher);

        preferencias=new Preferencias(this);

        bar= new ProgressBar(Launcher.this,
                null,
                android.R.attr.progressBarStyleHorizontal);

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new LauncherAppWidgetHost(this, R.id.APPWIDGET_HOST_ID);

        //we save our main activity in activity (SAVING CHANGES IN ACTIVITY)
        activity = this;

        packageManager = getPackageManager();
        //We get the GridView "content" that we created in the xml
        grid = (GridView) findViewById(R.id.content);
        home = (RelativeLayout) findViewById(R.id.home);

        home.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Launcher.this);
                builder.setTitle("¿Qué quieres añadir?").setItems(R.array.send_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                selectWidget();
                                break;
                            case 1:
                                selectShortcut();
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }
        });



        drawer = (SlidingDrawer) findViewById(R.id.drawer);
        //For drawing the grid layout with the applications of the system
        //Get the applications info in our array

        //setPacks();
        if(preferencias.getUserPass()!=null)
            new LoadApps().execute();

        //GetApps From previous session
        appsLoad();


        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerlayout);
        fragment_drawer = (Fragment_NavigationDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        fragment_drawer.setUp((DrawerLayout) findViewById(R.id.drawerlayout), null);


        fondo_pantalla = (ImageView) findViewById(R.id.fondo_pantalla);
        String selectedImage = preferencias.getUserImage();
        if(selectedImage!=null) {
            Drawable dr = new BitmapDrawable(getResources(), getBitmap(Uri.parse(selectedImage)));
            fondo_pantalla.setImageDrawable(dr);
        }

        fab_salir= (FloatingActionButton) findViewById(R.id.fab);
        fab_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(Launcher.this, Lock_Screen.class);
                startActivity(intent);
                cambiarVersion();
            }
        });

        startService(new Intent(getApplicationContext(), NotificationListener.class));

        //Pregunta para poner como default
        //launchAppChooser();

        /*//Adding widgets
        //As for apps, we need a manager to get the widgets info
        widgetManager = AppWidgetManager.getInstance(this);
        //We create the id.xml in values for creating a resource item of type id
        widgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);*/
    }

    @Override
    public void onNewIntent(Intent newIntent) {

        version = newIntent.getBooleanExtra("Version", false);
        if(version)
            cambiarVersion();
        else{
            version=false;
        }
    }

    public void cambiarVersion() {

        if(version) {
            fab_salir.setVisibility(View.VISIBLE);
            grid.setVisibility(View.INVISIBLE);
            drawer.setVisibility(View.INVISIBLE);
            home.setOnLongClickListener(null);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            for (int i = 0; i < home.getChildCount(); i++) {
                View v = home.getChildAt(i);
                v.setOnLongClickListener(null);
            }

            version = false;
        }else{
            fab_salir.setVisibility(View.INVISIBLE);
            grid.setVisibility(View.VISIBLE);
            drawer.setVisibility(View.VISIBLE);
            home.setOnLongClickListener(null);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            version = true;
        }
    }

    private List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);

        }
        return list;
    }


    void selectShortcut() {
        Intent intent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        intent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        startActivityForResult(intent, REQUEST_PICK_SHORTCUT);
    }

    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }

    void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                configureWidget(data);
            } else if (requestCode == R.id.REQUEST_CREATE_APPWIDGET) {
                createWidget(data);
            } else if (requestCode == REQUEST_PICK_SHORTCUT) {
                configureShortcut(data);
            } else if (requestCode == REQUEST_CREATE_SHORTCUT) {
                createShortcut(data);
            } else if (requestCode == CHANGE_PICTURE) {
                    Uri selectedImage = data.getData();
                    preferencias.setUserImage(selectedImage);
                    Drawable dr= new BitmapDrawable(getResources(), getBitmap(selectedImage));
                    fondo_pantalla.setImageDrawable(dr);
            }

        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    public Bitmap getBitmap(Uri selectedImage) {
        Bitmap actuallyUsableBitmap = null;

        int sampleSize = 7; // TODO improve resizer
        try {
            do {
                //Vamos disminuyendo el tamaño para problemas de memoria
                sampleSize -= 2;

                //Usamos BitmapFactory.Options para asignar este sampleSize
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sampleSize;

                //Read or write an Uri from context
                AssetFileDescriptor fileDescriptor = null;
                try {
                    fileDescriptor = getApplicationContext().getContentResolver().openAssetFileDescriptor(selectedImage, "r");
                } catch (FileNotFoundException e) {
                    // ErrorDialogFragment.newInstance("Error al cargar imagen", "No se encuentra la imagen de usuario.").show(getSupportFragmentManager(), "error");
                }

                //Creamos el bitmap a partir de la URI usando el samplesize para evitar Error OutOfMemory
                actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);


            } while (actuallyUsableBitmap.getWidth() < 400 && sampleSize > 1);

        } catch (NullPointerException e) {

            Toast.makeText(Launcher.this,"Error al cargar imagen\nNo se encuentra la imagen de usuario", Toast.LENGTH_LONG).show();
        }
        return actuallyUsableBitmap;
    }

    private void configureShortcut(Intent data) {
        startActivityForResult(data, REQUEST_CREATE_SHORTCUT);
    }

    public void createShortcut(Intent intent) {
        Intent.ShortcutIconResource iconResource = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
        Bitmap icon = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
        String shortcutLabel = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        final Intent shortIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);

        if (icon == null) {
            if (iconResource != null) {
                Resources resources = null;
                try {
                    resources = packageManager.getResourcesForApplication(iconResource.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (resources != null) {
                    int id = resources.getIdentifier(iconResource.resourceName, null, null);
                    if (resources.getDrawable(id) instanceof StateListDrawable) {
                        Drawable d = ((StateListDrawable) resources.getDrawable(id)).getCurrent();
                        icon = ((BitmapDrawable) d).getBitmap();
                    } else
                        icon = ((BitmapDrawable) resources.getDrawable(id)).getBitmap();
                }
            }
        }


        if (shortcutLabel != null && shortIntent != null && icon != null) {
            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 100;
            lp.topMargin = (int) 100;

            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_item, null);

            ((ImageView) ll.findViewById(R.id.icon_image)).setImageBitmap(icon);
            ((TextView) ll.findViewById(R.id.icon_text)).setText(shortcutLabel);

            ll.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(final View v) {
                    ll.setOnTouchListener(new MyOnTouchListener());

                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (v.isPressed()) {
                                Activity activity = (Activity) Launcher.this;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        home.removeView(v);

                                    }
                                });
                            } else {

                                this.cancel();
                            }
                        }
                    }, 5000, 6000);

                    home.addView(bar);

                    final CountDownTimer cdt = new CountDownTimer(5000, 200) {

                        public void onTick(long millisUntilFinished) {

                            if (v.isPressed()) {
                                int current = (int) (100 - (millisUntilFinished / 3000.0f) * 100.f);
                                bar.setProgress(current);
                            } else {
                                onFinish();
                                this.cancel();
                            }
                        }

                        public void onFinish() {
                            home.removeView(bar);
                        }
                    }.start();


                    if (!v.isPressed()) {
                        timer.cancel();
                        cdt.onFinish();
                    }
                    return true;
                }

            });
            ll.setOnClickListener(new ShortcutClickListener(this));
            ll.setTag(shortIntent);
            home.addView(ll, lp);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAppWidgetHost.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
        
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }

    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        final LauncherAppWidgetHostView hostView = (LauncherAppWidgetHostView) mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);

        hostView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(final View v) {

                longpressed=true;
                v.setOnTouchListener(new View.OnTouchListener() {
                    int leftMargin;
                    int topMargin;
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_MOVE:
                                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(v.getWidth(), v.getHeight());
                                //Here we get the event RAWx and y and adjust the result to put the icon in the center
                                leftMargin=(int)event.getRawX() - v.getWidth()/2;
                                topMargin=(int)event.getRawY() - v.getHeight()/2;
                                //Take care of margins
                                //Right side of screen
                                if (leftMargin+v.getWidth() > v.getRootView().getWidth())
                                    leftMargin=v.getRootView().getWidth() - v.getWidth();
                                //Left side
                                if (leftMargin < 0)
                                    leftMargin=0;
                                //bottom of screen
                                if (topMargin + v.getHeight() > ((View)v.getParent()).getHeight())
                                    topMargin=((View)v.getParent()).getHeight() - v.getHeight();
                                //top of screen
                                if (topMargin < 0)
                                    topMargin=0;

                                layoutParams.leftMargin= leftMargin;
                                layoutParams.topMargin=topMargin;
                                //Set the view with the new layout parameters
                                v.setLayoutParams(layoutParams);

                                break;
                            case MotionEvent.ACTION_UP:
                                longpressed=false;
                                v.setOnTouchListener(null);

                                break;
                        }
                        //to continue handling the event after this event
                        return false;
                    }
                });

                final Timer timerWidget = new Timer();
                timerWidget.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (longpressed) {
                            Activity activity = (Activity) Launcher.this;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    home.removeView(hostView);
                                }
                            });
                        } else {

                            this.cancel();
                        }
                    }
                }, 5000, 6000);

                home.addView(bar);

                final CountDownTimer cdtWidget = new CountDownTimer(5000, 200) {

                    public void onTick(long millisUntilFinished) {

                        if (longpressed) {
                            int current = (int) (100 - (millisUntilFinished / 3000.0f) * 100.f);
                            bar.setProgress(current);
                        } else {
                            onFinish();
                            this.cancel();
                        }
                    }

                    public void onFinish() {
                        home.removeView(bar);
                    }
                }.start();


                if (!longpressed) {
                    timerWidget.cancel();
                    cdtWidget.onFinish();
                }
                return true;
            }

        });

        home.addView(hostView);
        drawer.bringToFront();
    }

    public void launchAppChooser() {
        //Log.d(TAG, "launchAppChooser()");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void appsLoad() {
        SerializableData data = Serialization.loadSerializableData();
        if (data != null) {
            for (Pack pack : data.packs) {
                pack.addToHome(this, home);
            }
        }
        drawer.bringToFront();
    }


    public void setPacks() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        //We want to get the apps that could be launch
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //We make a query with the PackageManager for activities launchables using our intent and no flags
        List<ResolveInfo> listPacks = packageManager.queryIntentActivities(mainIntent, 0);
        packs = new Pack[listPacks.size()];
        //We create the packs with the info needed: icons, names, labels
        for (int i = 0; i < listPacks.size(); i++) {
            packs[i] = new Pack();
            packs[i].setIcon(listPacks.get(i).loadIcon(packageManager));
            packs[i].setPackageName(listPacks.get(i).activityInfo.packageName);
            packs[i].setName(listPacks.get(i).activityInfo.name);
            packs[i].setLabel(listPacks.get(i).loadLabel(packageManager).toString());
        }
        //We can reorder the apps if we want :D

        //initialize the DrawerAdapter with the info
        drawerAdapter = new DrawerAdapter(this, packs);
        //Then just let the adapter do his job on the GridView
        grid.setAdapter(drawerAdapter);
        //We need to make the icons launch the apps with an event. See GridClickListener
        grid.setOnItemClickListener(new GridClickListener(this, packageManager, packs));
        //Long click for putting the icons on home screen
        grid.setOnItemLongClickListener(new GridLongClickListener(this, drawer, home, packs));

    }

    public class AppListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent arg1) {
            // TODO Auto-generated method stub
            //setPacks();
            new LoadApps().execute();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //DialogFragment.instantiate(this, "Salir");
    }






    public class LoadApps extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> packsList = packageManager.queryIntentActivities(mainIntent, 0);
            packs = new Pack[packsList.size()];
            for (int i = 0; i < packsList.size(); i++) {
                packs[i] = new Pack();
                packs[i].setIcon(packsList.get(i).loadIcon(packageManager));
                packs[i].setPackageName(packsList.get(i).activityInfo.packageName);
                packs[i].setName(packsList.get(i).activityInfo.name);
                packs[i].setLabel(packsList.get(i).loadLabel(packageManager).toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (drawerAdapter == null) {
                drawerAdapter = new DrawerAdapter(getActivity(), packs);
                grid.setAdapter(drawerAdapter);
                grid.setOnItemClickListener(new GridClickListener(getActivity(), packageManager, packs));
                grid.setOnItemLongClickListener(new GridLongClickListener(getActivity(), drawer, home, packs));
            } else {
                drawerAdapter.setPacks(packs);
                drawerAdapter.notifyDataSetInvalidated();
            }
        }
    }


    public void onWindowFocusChanged(boolean hasFocus)
    {
        try
        {
            if(!hasFocus)
            {
                Object service  = getSystemService("statusbar");
                Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                Method collapse = statusbarManager.getMethod("collapsePanels");
                collapse .setAccessible(true);
                collapse .invoke(service);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
