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
        new TareaPruebaDatos().execute();
    }

    /*CLASE PARA CONEXION AL WEB SERVICE*/

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Call Web Method
            loginStatus = WebService.invokeLoginWS(editTextUsername,editTextPassword,"Login");
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
                    startActivity(intObj);
                }else{
                    //Set Error message
                    statusTV.setText("Vuelve a intentar, fallo sesión");
                }
                //Error status is true
            }else{
                statusTV.setText("Error de conexion al Servidor");
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

            // [INSERCIONES]
            String fechaActual = Calendar.getInstance().getTime().toString();
            try {
                datos.getDb().beginTransaction();
                // Inserción USER
                String cliente1 = datos.insertarUser(new Usuario("admin","4321"));
                // Inserción ENTREGA
                String user="admin";
                //String formaPago1 = datos.insertarEntrega(new Entrega("F-001","Salida","dir1",fechaActual, "Armand","dir2", fechaActual, "juan","prueba",user));
                // Inserción Productos
                //String producto1 = datos.insertarProducto(new Producto(null, "Completo", 2, "Manzana unidad", "exlente", user));
                //String producto2 = datos.insertarProducto(new Producto(null, "Completo", 3, "Pera unidad", "exlente", user));
                //String producto3 = datos.insertarProducto(new Producto(null, "Completo", 8, "Pan unidad", "exlente", user));
                // Inserción D0cument0s
                //String pedido1 = datos.insertarDocumentos(new Documentos(null, "f0t01","f0t02","f0t03","firma", null, user));
                datos.getDb().setTransactionSuccessful();
            } finally {
                datos.getDb().endTransaction();
            }
            // [QUERIES]
            Log.d("USER","USER");
            DatabaseUtils.dumpCursor(datos.obtenerUser());
            //Log.d("Formas de pago", "Formas de pago");
            //DatabaseUtils.dumpCursor(datos.obtenerProducto("admin"));
            //Log.d("Productos", "Productos");
            //DatabaseUtils.dumpCursor(datos.obtenerEntregas("admin"));
            //Log.d("obtenerDocumentos", "obtenerDocumentos");
            //DatabaseUtils.dumpCursor(datos.obtenerDocumentos("admin"));
            return null;
        }
    }

}
