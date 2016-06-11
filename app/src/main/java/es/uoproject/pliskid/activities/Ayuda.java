package es.uoproject.pliskid.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import es.uoproject.pliskid.R;

public class Ayuda extends AppCompatActivity {

    private ArrayList<FAQ> faqs = new ArrayList<>();
    private RecyclerView recycler;
    private FAQAdapter adapter;
    private String[] preguntas;
    private String[] respuestas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        recycler = (RecyclerView) findViewById(R.id.listaFAQ);

        preguntas=getResources().getStringArray(R.array.preguntas);
        respuestas=getResources().getStringArray(R.array.respuestas);
        for(int i = 0; i<preguntas.length;i++){
            faqs.add(new FAQ(preguntas[i], respuestas[i]));
        }

        adapter = new FAQAdapter(this,faqs);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();
    }


    private class FAQ {

        public FAQ(String pregunta, String respuesta) {
            this.pregunta=pregunta;
            this.respuesta=respuesta;
        }

        public String getPregunta() {
            return pregunta;
        }

        public void setPregunta(String pregunta) {
            this.pregunta = pregunta;
        }

        public String getRespuesta() {
            return respuesta;
        }

        public void setRespuesta(String respuesta) {
            this.respuesta = respuesta;
        }

        private String pregunta;
        private String respuesta;
    }

    public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {

        private ArrayList<FAQ> mDataset;
        private Context context;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView pregunta;
            public TextView respuesta;

            public ViewHolder(View itemView) {
                super(itemView);
                pregunta = (TextView) itemView.findViewById(R.id.preguntaFAQ);
                respuesta = (TextView) itemView.findViewById(R.id.respuestaFAQ);

            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public FAQAdapter(Context context,ArrayList<FAQ> myDataset) {
            this.context=context;
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public FAQAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.faq, parent, false);
            // set the view's size, margins, paddings and layout parameters
            //...
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.pregunta.setText(mDataset.get(position).getPregunta());
            holder.respuesta.setText(mDataset.get(position).getRespuesta());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
