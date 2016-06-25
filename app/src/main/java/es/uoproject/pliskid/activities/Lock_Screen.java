package es.uoproject.pliskid.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import es.uoproject.pliskid.R;
import es.uoproject.pliskid.util.Preferencias;


/**
 * A login screen that offers login via email/password.
 */
public class Lock_Screen extends AppCompatActivity  {

    // UI references.
    private EditText mPasswordView;

    private Preferencias preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lock__screen);

        preferencias=new Preferencias(this);

        mPasswordView = (EditText) findViewById(R.id.password);


        Button botonClave = (Button) findViewById(R.id.botonClave);

        botonClave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String password= preferencias.getUserPass();
                if(password!=null){
                    if(String.valueOf(mPasswordView.getText()).equals(password)){
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }else{
                        Toast.makeText(Lock_Screen.this, "Clave incorrecta", Toast.LENGTH_SHORT).show();
                    }

                }else {

                    if (mPasswordView.getText().length() > 3) {
                        preferencias.setUserPass(mPasswordView.getText().toString());
                        Intent intent = new Intent(Lock_Screen.this, Launcher.class);
                        startActivity(intent);
                        finish();
                    } else
                        Toast.makeText(Lock_Screen.this, "Esa clave es muy corta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

