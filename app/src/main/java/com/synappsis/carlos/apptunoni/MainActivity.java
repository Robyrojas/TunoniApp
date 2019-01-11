package com.synappsis.carlos.apptunoni;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.widget.Toast;

import com.synappsis.carlos.apptunoni.entidades.App;
import com.synappsis.carlos.apptunoni.entidades.Documentos;
import com.synappsis.carlos.apptunoni.entidades.Entrega;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;
import com.synappsis.carlos.apptunoni.entidades.Producto;
import com.synappsis.carlos.apptunoni.entidades.Usuario;

import com.synappsis.carlos.apptunoni.receiver.NetworkStateChangeReceiver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.synappsis.carlos.apptunoni.receiver.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

public class MainActivity extends AppCompatActivity {
    //Set Error Status
    static boolean errored = false;
    TextView statusTV;
    EditText userNameET , passWordET;
    ProgressBar webservicePG;
    String editTextUsername;
    boolean loginStatus;
    String editTextPassword;
    OperacionesBaseDatos datos = null;
    String ESTATUS = "";
    private static ConnectivityManager manager;
    String UserComanda = "";
    List<Producto> LISTAP = new ArrayList<Producto>();
    List<Documentos> LISTADOC = new ArrayList<Documentos>();
    List<Entrega> LISTAE = new ArrayList<Entrega>();
    //folioT ="",

    //Documentos doc = new Documentos();
    String comentarioComanda = "", tag="Main";
    boolean status = false;
    List<String> list64 = new ArrayList<>();
    AlertDialog Findialog;
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
        final Button btnEnviar = (Button) findViewById(R.id.enviarInfo);
        datos = OperacionesBaseDatos
                .obtenerInstancia(getApplicationContext());
        DatabaseUtils.dumpCursor(datos.obtenerUser());
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        Log.e("br0adcast",NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = isOnline();
                String networkStatus = isNetworkAvailable ? "Establecida" : "Desconectada";
                Log.e("br0adcast",networkStatus);
                //Toast.makeText(getApplicationContext(), networkStatus, Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.activity_main), "Conexión: " + networkStatus, Snackbar.LENGTH_LONG).show();
                if(isNetworkAvailable){
                    //obtener usuario
                    obtenerUser();
                    if(UserComanda!=null){
                        if(!UserComanda.isEmpty()){
                            getProducts();
                            getFotos(UserComanda);
                            if(!LISTAP.isEmpty() || !LISTADOC.isEmpty()){
                                btnEnviar.setVisibility(View.VISIBLE);
                            }else
                                btnEnviar.setVisibility(View.INVISIBLE);
                        }else
                            btnEnviar.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    btnEnviar.setVisibility(View.INVISIBLE);
                }
            }
        }, intentFilter);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new loading().execute();
            }
        });
        Log.e("mainactivity","init app");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if text controls are not empty
                if (userNameET.getText().length() != 0 && userNameET.getText().toString() != "") {
                    if(passWordET.getText().length() != 0 && passWordET.getText().toString() != ""){
                        webservicePG.setVisibility(View.VISIBLE);
                        //Log.d("l0gin","activar l0ad");
                        editTextUsername = userNameET.getText().toString();
                        editTextPassword = passWordET.getText().toString();
                        statusTV.setText("");
                        //Create instance for AsyncCallWS
                        //if(isOnline(getApplicationContext())){
                            AsyncCallWS task = new AsyncCallWS();
                        //}

                        //Log.d("l0gin","para el task");
                        if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            task.execute();
                        }
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


    }
    /*CLASE PARA CONEXION AL WEB SERVICE*/

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Call Web Method
            Log.d("l0gin","entre al bt0t0n");
            loginStatus = WebService.invokeLoginWS(editTextUsername,editTextPassword,"LoginApp");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Make Progress Bar invisible
            webservicePG.setVisibility(View.INVISIBLE);
            Intent intObj = new Intent(MainActivity.this, Nav_Principal.class);
            Log.e("l0gin","p0st: "+loginStatus + "err0r: " + errored);
            //errored=false;//loginStatus=true;
            //Error status is false
            if(!errored){
                //Based on Boolean value returned from WebService
                Log.e("l0gin","c0n internet");
                if(loginStatus){
                    //Navigate to Home Screen
                    String pantalla = obtenerEstado();
                    if (pantalla.equals("Entregando"))
                        intObj = new Intent(MainActivity.this, productos.class);
                    else{
                        registro();
                    }
                    startActivity(intObj);
                }else{
                    //Set Error message
                    statusTV.setText("Error en Usuario y/o contraseña");
                }
                //Error status is true
            }else{
                Log.d("l0gin","sin internet");
                String base=obtenerUSER();
                if(!base.equals("Error")) {
                    String ESTADO = obtenerEstado();
                    if(!ESTADO.equals("Error")) {
                        String[] parts = base.split(",");
                            if (editTextUsername.equals(parts[0]) && editTextPassword.equals(parts[1])) {
                                if (ESTADO.equals("Entregando")) {
                                    startActivity(new Intent(MainActivity.this, productos.class));
                                } else if (ESTADO.equals("En Camino") || ESTADO.equals("Aceptado") || ESTADO.equals("Sin enviar") || ESTADO.equals("Send")) {
                                    startActivity(new Intent(MainActivity.this, Nav_Principal.class));
                                } else {
                                    statusTV.setText("No hay conexión al Servidor");
                                }
                            } else {
                                statusTV.setText("Error en Usuario y/o contraseña");
                            }
                    }else{
                        //Set Error message
                        statusTV.setText("No hay conexión al Servidor");
                    }
                }else{
                    //Set Error message
                    statusTV.setText("No hay conexión al Servidor");
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

    private void registro() {
        try {
            datos.getDb().beginTransaction();
            String user=editTextUsername;
            String pass=editTextPassword;
            // Inserción USER
            String vistaSave="";
            Cursor cursor= datos.obtenerUser();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("nombre");
                    vistaSave = cursor.getString(columna);
                }
                if(vistaSave!=null){
                    if(!vistaSave.isEmpty()){
                        if(vistaSave.equals(user))
                            datos.insertarUser(new Usuario(user,pass));//pass=xcvb
                        else{
                            boolean valu = datos.eliminarUser(vistaSave);
                            if(valu)
                                datos.insertarUser(new Usuario(user,pass));//pass=xcvb
                        }
                    }else{
                        datos.insertarUser(new Usuario(user,pass));//pass=xcvb
                    }
                }else{
                    datos.insertarUser(new Usuario(user,pass));//pass=xcvb
                }
            }
            Cursor app = datos.obtenerApp();
            if(app!=null) {
                int columna;
                if (app.moveToFirst()) {
                    columna = cursor.getColumnIndex("folio");
                    Log.d("REGISTRO","-----+++----" + "f0li0");
                    if(columna>-1){
                        String appBase = cursor.getString(columna);
                        Log.d("REGISTRO","-----+++----" + appBase);
                        if(appBase!=null || appBase.isEmpty()){
                            datos.insertarApp(new App("SF","Sin enviar",null, null));
                        }}
                }else
                    datos.insertarApp(new App("SF","Sin enviar",null, null));
            }else{
                datos.insertarApp(new App("SF","Sin enviar",null, null));
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();

        }
        // [QUERIES]
        Log.d("USER","----------------Obtencion de base de datos MAINACTIVITY");
        DatabaseUtils.dumpCursor(datos.obtenerUser());
        DatabaseUtils.dumpCursor(datos.obtenerApp());
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
                if(!u1.isEmpty() && !p1.isEmpty())
                    resStatus = u1+","+p1;
                else
                    resStatus="Error";
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
        String resStatus = "Error";
        try {
            Log.e("ESTAD0", "Actualizar en obtener estado");
            datos.getDb().beginTransaction();
            Cursor cursor =datos.obtenerApp();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("estatus");
                    ESTATUS = cursor.getString(columna);
                }
                Log.e("ESTAD0", "ESTATUS: "+ESTATUS);
                if(ESTATUS!=null){
                    if(!ESTATUS.isEmpty())
                        resStatus = ESTATUS;
                }
            }
            else{
                Log.d("USER","Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerApp());
        return resStatus;
    }

    public static boolean isOnline() {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private void obtenerUser() {
        try {
            //Log.e(tag, "Actualizar");
            datos.getDb().beginTransaction();
            UserComanda=null;
            Cursor cursor = datos.obtenerUser();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("nombre");
                    UserComanda = cursor.getString(columna);
                }
                Log.e(tag, "user: " + UserComanda);
            } else {
                Log.d("USER", "Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerApp());
    }

    public void getFolios() {
        try {
            Log.e(tag, "Obteniendo folios de entrega");
            datos.getDb().beginTransaction();
            Cursor cursor1 = datos.obtenerEntregas();
            if (cursor1 != null) {
                if (cursor1.moveToFirst()) {
                    do {
                        Entrega e = new Entrega();
                        int columna = cursor1.getColumnIndex("folio");
                        e.folio = cursor1.getString(columna);
                        int columna1 = cursor1.getColumnIndex("estatus");
                        e.estatus = cursor1.getString(columna1);
                        int columna2 = cursor1.getColumnIndex("dirorigen");
                        e.dirorigen = cursor1.getString(columna2);
                        int columna3 = cursor1.getColumnIndex("fechaorigen");
                        e.fechaorigen = cursor1.getString(columna3);
                        int columna4 = cursor1.getColumnIndex("nombre");
                        e.nombre = cursor1.getString(columna4);
                        int columna5 = cursor1.getColumnIndex("nombredestino");
                        e.nombredestino = cursor1.getString(columna5);
                        int columna6 = cursor1.getColumnIndex("dirdestino");
                        e.dirdestino = cursor1.getString(columna6);
                        int columna7 = cursor1.getColumnIndex("fechadestino");
                        e.fechadestino = cursor1.getString(columna7);
                        int columna8 = cursor1.getColumnIndex("nombrereceptor");
                        e.nombrereceptor = cursor1.getString(columna8);
                        int columna9 = cursor1.getColumnIndex("info");
                        e.info = cursor1.getString(columna9);
                        int columna0 = cursor1.getColumnIndex("usuario_nombre");
                        e.usuario_nombre = cursor1.getString(columna0);
                        LISTAE.add(e);
                    } while(cursor1.moveToNext());

                }
                datos.getDb().setTransactionSuccessful();
            }
        } finally{
            datos.getDb().endTransaction();
        }
    }

    public void getProducts(){
        try {
            Log.e(tag, "get productos");
            datos.getDb().beginTransaction();
            Cursor cursor1 =datos.obtenerProductos(UserComanda);
            if(cursor1!=null){
                //Nos aseguramos de que existe al menos un registro
                if (cursor1.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {
                        Producto p = new Producto();
                        int columna = cursor1.getColumnIndex("producto");
                        p.producto = cursor1.getString(columna);
                        int columna2 = cursor1.getColumnIndex("estado");
                        p.cantidad = cursor1.getString(columna2);
                        int columna3 = cursor1.getColumnIndex("faltante");
                        p.cantidad = cursor1.getString(columna3);
                        int columna4 = cursor1.getColumnIndex("entrega_folio");
                        p.entrega_folio = cursor1.getString(columna4);
                        LISTAP.add(p);
                    } while(cursor1.moveToNext());
                }
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        //DatabaseUtils.dumpCursor(datos.obtenerProductos(UserComanda));
        Log.d(tag, "Tam List: "+LISTAP.size());
    }

    public void getFotos(String user){
        try {
            Log.e(tag, "get fotos");
            datos.getDb().beginTransaction();
            Cursor cursor1 =datos.obtenerDocumentos(user);
            if(cursor1!=null){
                //Nos aseguramos de que existe al menos un registro
                if (cursor1.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {
                        Documentos d = new Documentos();
                        int columna0 = cursor1.getColumnIndex("idDocumentos");
                        int columna = cursor1.getColumnIndex("foto1");
                        int columna2 = cursor1.getColumnIndex("foto2");
                        int columna3 = cursor1.getColumnIndex("foto3");
                        int columna4 = cursor1.getColumnIndex("firma");
                        int columna5 = cursor1.getColumnIndex("comentarios");
                        String t1 = cursor1.getString(columna);
                        String t2 = cursor1.getString(columna2);
                        String t3 = cursor1.getString(columna3);
                        String t4 = cursor1.getString(columna4);
                        String t0 = cursor1.getString(columna0);
                        if(!t1.isEmpty() || !t2.isEmpty() || !t3.isEmpty() || !t4.isEmpty()){
                            d.foto1 = cursor1.getString(columna);
                            d.foto2 = cursor1.getString(columna2);
                            d.foto3 = cursor1.getString(columna3);
                            d.firma = cursor1.getString(columna4);
                            d.iddocumentos = cursor1.getString(columna0);
                            comentarioComanda = cursor1.getString(columna5);
                            LISTADOC.add(d);
                        }else{
                            try {
                                boolean res =datos.eliminarDocumentos(t0);
                                if(res){
                                    //Nos aseguramos de que existe al menos un registro
                                    Log.e(tag, "base fotos borrada con: " + t0);
                                }
                                //datos.getDb().setTransactionSuccessful();
                            } finally {
                                //datos.getDb().endTransaction();
                            }
                        }
                        //LISTAP.add(p);
                    } while(cursor1.moveToNext());
                }
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerDocumentos(user));
    }

    private class loading extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            //enviar prodcutos
            Log.d(tag, "L0ading");
            for(int i = 0; i<LISTAP.size();i++) { //entrega_folio
                status = WebService.invokeProducto(LISTAP.get(i).entrega_folio, LISTAP.get(i).producto, LISTAP.get(i).estado, LISTAP.get(i).faltante, comentarioComanda);
                //reenviar
                Log.d("PR0DUCT0 ws", "0 " + status);
            }
            //Convertir y enviar fotos
            for(int i = 0; i<LISTADOC.size();i++) { //entrega_folio
                String f1 = convert64(LISTADOC.get(i).foto1);
                String f2 = convert64(LISTADOC.get(i).foto2);
                String f3 = convert64(LISTADOC.get(i).foto3);
                String f4 = convert64(LISTADOC.get(i).firma);
                status = WebService.invokeImagenWS(LISTADOC.get(i).entrega_folio,f1,"Foto1");Log.d("CICL0 ws", "0 "+status);
                status = WebService.invokeImagenWS(LISTADOC.get(i).entrega_folio,f2,"Foto2");Log.d("CICL0 ws", "1 "+status);
                status = WebService.invokeImagenWS(LISTADOC.get(i).entrega_folio,f3,"Foto3");Log.d("CICL0 ws", "2 "+status);
                status = WebService.invokeImagenWS(LISTADOC.get(i).entrega_folio,f4,"Firma");Log.d("CICL0 ws", "3 "+status);
                //reenviar
                Log.d("Doc ws", "0 " + status);
            }
            //enviar satuts
            getFolios();
            for(int i = 0; i<LISTAE.size();i++) {
                status = WebService.invokeComanda(LISTAE.get(i).folio, "Entregada");
                //reenviar
                Log.d("ENTRAGFOLIOS ws", "0 " + status);
            }

            Log.d("imagen ws","termine ed enviar");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
                        //Error status is false
            if(status){
                Log.e(tag, "Envios completos");
                try {
                    datos.getDb().beginTransaction();
                    datos.borrar("Producto");
                    datos.borrar("Documentos");
                    datos.borrar("Entrega");
                    Log.d("MAIN", "NumProductos: "+datos.contarRegistros("Producto"));
                    Log.d("MAIN", "NumDocs: "+datos.contarRegistros("Documentos"));
                    Log.d("MAIN", "NumDocs: "+datos.contarRegistros("Entrega"));
                    datos.getDb().setTransactionSuccessful();
                } finally {
                    datos.getDb().endTransaction();
                }
                Toast.makeText(getApplicationContext(),"Se envío información",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Vuelva a intentar",Toast.LENGTH_SHORT).show();
            }
            if (Findialog != null) {
                Findialog.dismiss();
                Findialog = null;
            }
            //Re-initialize Error Status to False
            status = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            Log.d("imagen","l0ading");
            AlertDialog.Builder finBuilder = new AlertDialog.Builder(MainActivity.this);
            View vistaFin = getLayoutInflater().inflate(R.layout.dialog_estado,null);
            finBuilder.setView(vistaFin);
            Findialog = finBuilder.create();
            Findialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void borrarBase(){
        datos.getDb().beginTransaction();
        String folioT ="";
        Cursor cursor =datos.obtenerApp();
        if(cursor!=null){
            if (cursor.moveToFirst()) {
                int columna = cursor.getColumnIndex("folio");
                folioT = cursor.getString(columna);
            }
            Log.e(tag, "folioT-U: "+folioT);
        }
        datos.actualizarStatus("Sin Enviar",folioT);
        datos.borrar("App");
        datos.borrar("Producto");
        datos.borrar("Documentos");
        datos.borrar("Entrega");
        datos.borrar("Usuario");
        //DatabaseUtils.dumpCursor(datos.obtenerApp());
        //DatabaseUtils.dumpCursor(datos.obtenerUser());
        Cursor cursor1 =datos.obtenerApp();
        String hola =null;
        if(cursor1!=null){
            if (cursor1.moveToFirst()) {
                int columna = cursor1.getColumnIndex("estatus");
                hola = cursor1.getString(columna);
            }
            Log.e("ESTAD0", "BORRAR: "+hola);
        }
        cursor1 =datos.obtenerUser();
        String hola1 =null;
        if(cursor1!=null){
            if (cursor1.moveToFirst()) {
                int columna = cursor1.getColumnIndex("nonbre");
                hola1 = cursor1.getString(columna);
            }
            Log.e("ESTAD0", "BORRAR-U: "+hola1);
        }
        datos.deleteALL(getApplicationContext());
    }

    private String convert64(String ruta){
        String xxx = encodeImage(ruta);
        Log.e(tag, " TAM: " +xxx.length());
        return xxx;
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,18,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;

    }

    @Override
    public void onBackPressed() {
        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("¿Quieres salir de la aplicación?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        return;
                    }
                })
                .show();
    }
}
