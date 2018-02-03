package com.synappsis.carlos.apptunoni;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.synappsis.carlos.apptunoni.entidades.App;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;
import com.synappsis.carlos.apptunoni.entidades.Usuario;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;



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
    String ESTATUS = "";
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
                    String pantalla = obtenerEstado();
                    if (pantalla.equals("Entregando"))
                        intObj = new Intent(MainActivity.this, productos.class);
                    else
                        new TareaPruebaDatos().execute();
                        startActivity(intObj);
                }else{
                    //Set Error message
                    statusTV.setText("Vuelve a intentar, Error en Usuario y/o contraseña");
                }
                //Error status is true
            }else{
                String base=obtenerUSER();
                if(!base.equals("Error")) {
                    String[] parts = base.split(",");
                    if (editTextUsername.equals(parts[0]) && editTextPassword.equals(parts[1])) {
                        String ESTADO = obtenerEstado();
                        if (ESTADO.equals("Entregando")) {
                            startActivity(new Intent(MainActivity.this, productos.class));
                        } else if (ESTADO.equals("En Camino")) {
                            startActivity(new Intent(MainActivity.this, Nav_Principal.class));
                        } else {
                            statusTV.setText("No hay conexión a internet");
                        }
                    } else {
                        statusTV.setText("Vuelve a intentar, Error en Usuario y/o contraseña");
                    }
                }else{
                    //Set Error message
                    statusTV.setText("No hay conexión a internet");
                }
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

    private String obtenerUSER() {
        String resStatus = "";
        String u1="", p1="";
        try {
            Log.e("ESTAD0", "entre a user");
            datos.getDb().beginTransaction();
            Cursor cursor =datos.obtenerUser();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("nombre");
                    u1 = cursor.getString(columna);
                    columna = cursor.getColumnIndex("pass");
                    p1 = cursor.getString(columna);
                }
                resStatus = u1+","+p1;
            }
            else{
                Log.d("USER","Error algo vacio");
                resStatus="Error";
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        Log.e("ESTAD0", resStatus);
        return resStatus;
    }

    private String obtenerEstado() {
        String resStatus = "";
        try {
            Log.e("ESTAD0", "Actualizar");
            datos.getDb().beginTransaction();
            Cursor cursor =datos.obtenerApp();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("estatus");
                    ESTATUS = cursor.getString(columna);
                }
                Log.e("ESTAD0", "ESTATUS: "+ESTATUS);
                resStatus = ESTATUS;
            }
            else{
                Log.d("USER","Error algo vacio");
                resStatus="Error";
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerApp());
        return resStatus;
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
