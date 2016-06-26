package es.uoproject.pliskid.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import es.uoproject.pliskid.R;

/**
 * Clase que establece la configuración del listado del menú contextual
 */
public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.MyViewHolder> {


    private static final int CAMBIAR_VERSION= 0;
    private static final int CAMBIAR_FONDO= 1;
    private static final int QUITAR_BARRA_SISTEMA= 2;
    private static final int BLOQUEAR_LLAMADAS= 3;
    private static final int CAMBIAR_PASS= 4;
    private static final int RESETEAR_ENTORNO= 5;
    private static final int AYUDA=6;
    private static final int SALIR=7;

    private ClickListener clickListener;
    private LayoutInflater inflater;
    private String [] options;
    private int [] imagenes;
    private Context context;

    public NavigationAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
        Resources res=context.getResources();
        options=res.getStringArray(R.array.options);
        imagenes=new int[]{R.mipmap.ico_cambiar_roll, R.mipmap.ico_fondo_pantalla,
                R.mipmap.ico_bloqueo_barra_nav, R.mipmap.ico_bloqueo_llamadas,
                R.mipmap.ico_cambiar_contrase, R.mipmap.ico_reset_entorno,
                R.mipmap.ico_ayuda, R.mipmap.avatar};
    }

    /**
     * Método que crea el view por cada item
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public NavigationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v=inflater.inflate(R.layout.drawer_menu_row, parent, false);

        MyViewHolder myViewHolder=new MyViewHolder(v);

        return myViewHolder;
    }

    /**
     * Método que le asigna los valores al item
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(NavigationAdapter.MyViewHolder holder, int position) {

        holder.drawer_option.setText(options[position]);
        holder.drawer_icon.setImageResource(imagenes[position]);

    }

    /**
     * Método con el número de objetos del listado
     * @return
     */
    @Override
    public int getItemCount() {
        return options.length;
    }

    /**
     * Clase que establece el modelo y funcionalidad del item
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView drawer_option;
        ImageView drawer_icon;
        LinearLayout option_layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            drawer_option=(TextView) itemView.findViewById(R.id.option_menu);
            drawer_icon=(ImageView) itemView.findViewById(R.id.icono_menu);
            option_layout=(LinearLayout) itemView.findViewById(R.id.option_layout);
            option_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
            v.startAnimation(buttonClick);
            v.invalidate();

            Intent intent;
            switch(getAdapterPosition()){
                case CAMBIAR_VERSION:

                    clickListener.itemClicked(v, getAdapterPosition());

                    break;
                case CAMBIAR_FONDO:
                    clickListener.itemClicked(v, getAdapterPosition());
                    break;
                case QUITAR_BARRA_SISTEMA:
                    clickListener.itemClicked(v, getAdapterPosition());
                    break;
                case BLOQUEAR_LLAMADAS:
                    clickListener.itemClicked(v, getAdapterPosition());
                    break;
                case CAMBIAR_PASS:
                    clickListener.itemClicked(v, getAdapterPosition());
                    break;
                case RESETEAR_ENTORNO:
                    clickListener.itemClicked(v, getAdapterPosition());
                    break;
                case AYUDA:
                    clickListener.itemClicked(v, getAdapterPosition());
                    break;
                case SALIR:
                    clickListener.itemClicked(v, getAdapterPosition());
                    break;

            }
        }
    }


    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * Interfaz que nos permitirá la comunicación con el fragment del menú contextual
     */
    public interface ClickListener {

        public void itemClicked(View view, int position);
    }
}