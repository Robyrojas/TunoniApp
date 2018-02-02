package com.synappsis.carlos.apptunoni;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.synappsis.carlos.apptunoni.entidades.App;
import com.synappsis.carlos.apptunoni.entidades.Documentos;
import com.synappsis.carlos.apptunoni.entidades.Entrega;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;
import com.synappsis.carlos.apptunoni.entidades.Producto;
import com.synappsis.carlos.apptunoni.entidades.Usuario;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    //Set Error Status
    static boolean errored = false;
    Button b;
    TextView statusTV;
    EditText userNameET , passWordET;
    ProgressBar webservicePG;
    String editTextUsername;
    boolean loginStatus;
    String editTextPassword;
    OperacionesBaseDatos datos = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameET = (EditText) findViewById(R.id.editText);
        passWordET = (EditText) findViewById(R.id.editText2);
        statusTV = (TextView) findViewById(R.id.textView2);
        webservicePG = (ProgressBar) findViewById(R.id.progressBar2);
        webservicePG.setVisibility(View.INVISIBLE);
        Button btn = (Button) findViewById(R.id.initSesion);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if text controls are not empty
                if (userNameET.getText().length() != 0 && userNameET.getText().toString() != "") {
                    if(passWordET.getText().length() != 0 && passWordET.getText().toString() != ""){
                        webservicePG.setVisibility(View.VISIBLE);
                        editTextUsername = userNameET.getText().toString();
                        editTextPassword = passWordET.getText().toString();
                        statusTV.setText("");
                        //Create instance for AsyncCallWS
                        AsyncCallWS task = new AsyncCallWS();
                        //Call execute
                        task.execute();
                        //sin web service
                        /*Intent intObj = new Intent(MainActivity.this, Nav_Principal.class);
                        startActivity(intObj);
                        webservicePG.setVisibility(View.INVISIBLE);*/
                    }
                    //If Password text control is empty
                    else{
                        statusTV.setText("Falta escribir la contraseña");
                    }
                    //If Username text control is empty
                } else {
                    statusTV.setText("Falta escribir el usuario");
                }
            }
        });
        getApplicationContext().deleteDatabase("pedidos.db");
        datos = OperacionesBaseDatos
                .obtenerInstancia(getApplicationContext());

    }

    /*CLASE PARA CONEXION AL WEB SERVICE*/

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Call Web Method
            loginStatus = WebService.invokeLoginWS(editTextUsername,editTextPassword,"LoginApp");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Make Progress Bar invisible
            webservicePG.setVisibility(View.INVISIBLE);
            Intent intObj = new Intent(MainActivity.this, Nav_Principal.class);
            //Error status is false
            if(!errored){
                //Based on Boolean value returned from WebService
                if(loginStatus){
                    //Navigate to Home Screen
                    new TareaPruebaDatos().execute();
                    startActivity(intObj);
                }else{
                    //Set Error message
                    statusTV.setText("Vuelve a intentar, Error en Usuario y/o contraseña");
                }
                //Error status is true
            }else{
                statusTV.setText("No hay conexión a internet");
            }
            //Re-initialize Error Status to False
            errored = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            webservicePG.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    /*TEST DE BASE DE DATOS*/
    public class TareaPruebaDatos extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                datos.getDb().beginTransaction();
                String user=editTextUsername;
                String pass=editTextPassword;
                // Inserción USER
                String cliente1 = datos.insertarUser(new Usuario(user,pass));//pass=xcvb
                String inicio = datos.insertarApp(new App("SF","Sin enviar",null, null));
                datos.getDb().setTransactionSuccessful();
            } finally {
                datos.getDb().endTransaction();

            }
            // [QUERIES]
            Log.d("USER","----------------Obtencion de base de datos MAINACTIVITY");
            DatabaseUtils.dumpCursor(datos.obtenerUser());
            DatabaseUtils.dumpCursor(datos.obtenerApp());
            return null;
        }
    }

}
