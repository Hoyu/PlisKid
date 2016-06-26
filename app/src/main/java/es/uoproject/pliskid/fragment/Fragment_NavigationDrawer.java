package es.uoproject.pliskid.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.uoproject.pliskid.R;
import es.uoproject.pliskid.activities.Ayuda;
import es.uoproject.pliskid.activities.Launcher;
import es.uoproject.pliskid.adapters.NavigationAdapter;
import es.uoproject.pliskid.util.Serialization;

/**
 * Clase Fragment que conforma el menú contextual y su funcionalidad
 */
public class Fragment_NavigationDrawer extends Fragment implements NavigationAdapter.ClickListener {

    private static final int CAMBIAR_VERSION= 0;
    private static final int CAMBIAR_FONDO= 1;
    private static final int QUITAR_BARRA_SISTEMA= 2;
    private static final int BLOQUEAR_LLAMADAS= 3;
    private static final int CAMBIAR_PASS= 4;
    private static final int RESETEAR_ENTORNO= 5;
    private static final int AYUDA=6;
    private static final int SALIR=7;

    private static final int CHANGE_PICTURE=10;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout mDrawerLayout;

    private RecyclerView drawer_recycler;
    private NavigationAdapter drawerAdapter;


    public Fragment_NavigationDrawer() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View layout=inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        drawer_recycler=(RecyclerView) layout.findViewById(R.id.drawer_recycler);


        return layout;
    }
/*
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
*/

    /**
     * Método que inicializa el menú contextual y su funcionalidad al abrir o cerrar
     * @param drawerLayout
     * @param toolbar
     */
    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
        mDrawerLayout=drawerLayout;
        drawerToggle=new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerAdapter=new NavigationAdapter(getActivity());
                drawerAdapter.setClickListener(Fragment_NavigationDrawer.this);

                drawer_recycler.setAdapter(drawerAdapter);
                drawer_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

                drawerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);

    }

    /**
     * Método que recoge la interfaz de pulsado del adapter del listado y ejecuta cada opción
     * @param view
     * @param position
     */
    @Override
    public void itemClicked(View view, int position) {

        Intent intentResult=null;
        Launcher activity= (Launcher)getActivity();
        switch(position){
            case CAMBIAR_FONDO:

                //Titulo del chooser
                String title = getResources().getString(R.string.chooser_title);
                //Chooser
                Intent chooserIntent = null;
                //Creamos una lista de opciones
                List<Intent> intentList = new ArrayList<>();

                //Intent para elegir imagen de la galería
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //Intent para hacer una foto
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //Añadimos los intents a la lista con el contexto
                intentList = addIntentsToList(getActivity().getApplicationContext(), intentList, pickIntent);
                intentList = addIntentsToList(getActivity().getApplicationContext(), intentList, takePhotoIntent);

                //Le pasamos al chooser la lista de intents y los vamos eliminando
                if (intentList.size() > 0) {
                    chooserIntent = Intent.createChooser(intentList.remove(intentList.size() -1), title);
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
                }

                //Lanzamos el chooser con su requestCode
                getActivity().startActivityForResult(chooserIntent, CHANGE_PICTURE);


                break;

            case CAMBIAR_VERSION:
                activity.cambiarVersion();
                break;

            case CAMBIAR_PASS:
                activity.cambiarPassword();
                break;

            case RESETEAR_ENTORNO:
                activity.reset();
                break;
            case AYUDA:
                Intent intent= new Intent(activity, Ayuda.class);
                startActivity(intent);
                break;
            case BLOQUEAR_LLAMADAS:
                activity.bloqueoLlamadas();
                break;
            case QUITAR_BARRA_SISTEMA:
                activity.bloqueoBarraSistema();
                break;
            case SALIR:
                activity.cambiarLauncherInicio();
                break;
        }
    }
}
