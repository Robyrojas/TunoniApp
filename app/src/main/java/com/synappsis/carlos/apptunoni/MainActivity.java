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
    Button b;
    TextView statusTV;
    EditText userNameET , passWordET;
    ProgressBar webservicePG;
    String editTextUsername;
    boolean loginStatus;
    String editTextPassword;
    OperacionesBaseDatos datos = null;
    String ESTATUS = "";
    private static ConnectivityManager manager;
    String folioT ="", UserComanda = "";
    List<Producto> LISTAP = new ArrayList<Producto>();
    Documentos doc = new Documentos();
    String comentarioComanda = "", tag="Main";
    boolean status = false;
    List<String> nombreTablas = Arrays.asList("Usuario", "Producto","Documentos");
    List<String> list64 = new ArrayList<>();

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
                    //obtener folio
                    getFolio();
                    if(!folioT.isEmpty()){
                        getProducts(folioT);
                        if(!LISTAP.isEmpty()){
                            btnEnviar.setVisibility(View.VISIBLE);
                        }else
                            btnEnviar.setVisibility(View.INVISIBLE);
                    }else
                        btnEnviar.setVisibility(View.INVISIBLE);
                }
                else{
                    btnEnviar.setVisibility(View.INVISIBLE);
                }
            }
        }, intentFilter);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnviarRegistros();
                Snackbar.make(findViewById(R.id.activity_main), "Se ha enviado correctamente ", Snackbar.LENGTH_LONG).show();
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

    private void EnviarRegistros(){
        boolean userExist =false;
        obtenerUser();
        //obtener folio
        getFolio();
        //obtener porductos
        if(!folioT.isEmpty()){
            getProducts(folioT);
            //obtener foto
            if(UserComanda!=null){
                getFotos(UserComanda);
            }
            for (int i = 0; i < nombreTablas.size(); i++) {
                //Enviar Status, productos y fotos
                if(numRegistros(nombreTablas.get(i))){
                    if(i == 0 && UserComanda!=null){
                        userExist =true;
                    }
                    if(userExist && i == 1){
                        new mandarProductos().execute();
                        new enviarStatus().execute();
                    }
                    if(userExist && i == 2){
                        new enviarFotos().execute();
                        new enviarStatus().execute();
                    }

                }
            }
        }
        datos.getDb().close();
        //Toast.makeText(getApplicationContext(),"Información Enviada",Toast.LENGTH_SHORT).show();
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

    public String getFolio() {
        try {
            Log.e(tag, "Obteniendo folio");
            datos.getDb().beginTransaction();
            Cursor cursor1 = datos.obtenerApp();
            if (cursor1 != null) {
                if (cursor1.moveToFirst()) {
                    int columna = cursor1.getColumnIndex("folio");
                    folioT = cursor1.getString(columna);
                }
                Log.e("ESTAD0", "folioT-U: " + folioT);

                datos.getDb().setTransactionSuccessful();
            }
        } finally{
            datos.getDb().endTransaction();
        }
        return folioT;
    }

    public void getProducts(String f){
        try {
            Log.e(tag, "get productos");
            datos.getDb().beginTransaction();
            Cursor cursor1 =datos.obtenerProducto(f);
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
                        //int columna4 = cursor1.getColumnIndex("entrega_folio");
                        p.entrega_folio = folioT;
                        LISTAP.add(p);
                    } while(cursor1.moveToNext());
                }
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerProducto(f));
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

                        int columna = cursor1.getColumnIndex("foto1");
                        int columna2 = cursor1.getColumnIndex("foto2");
                        int columna3 = cursor1.getColumnIndex("foto3");
                        int columna4 = cursor1.getColumnIndex("firma");
                        int columna5 = cursor1.getColumnIndex("comentarios");
                        String t1 = cursor1.getString(columna);
                        String t2 = cursor1.getString(columna2);
                        String t3 = cursor1.getString(columna3);
                        String t4 = cursor1.getString(columna4);
                        if(!t1.isEmpty() || !t2.isEmpty() || !t3.isEmpty() || !t4.isEmpty()){
                            doc.foto1 = cursor1.getString(columna);
                            doc.foto2 = cursor1.getString(columna2);
                            doc.foto3 = cursor1.getString(columna3);
                            doc.firma = cursor1.getString(columna4);
                            comentarioComanda = cursor1.getString(columna5);
                        }else{
                            try {
                                boolean res =datos.eliminarDocumentos(folioT);
                                if(res){
                                    //Nos aseguramos de que existe al menos un registro
                                    Log.e(tag, "base fotos borrada");
                                }
                                datos.getDb().setTransactionSuccessful();
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

    private class mandarProductos extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Call Web Method
            //Log.d("data", "data pr0duct0");
            for(int i = 0; i<LISTAP.size();i++) {
                status = WebService.invokeProducto(folioT, LISTAP.get(i).producto, LISTAP.get(i).estado, LISTAP.get(i).faltante, comentarioComanda);
                //reenviar
                Log.d("PR0DUCT0 ws", "0 " + status);
            }
            //Log.d("imagen ws","termine ed enviar");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Error status is false
            if(status){
                Log.e(tag, "Se envio prodcutos completos");
                //Toast.makeText(getApplicationContext(),"Se guardo correctamente la información",Toast.LENGTH_SHORT).show();
                try {
                    datos.getDb().beginTransaction();
                    boolean res =datos.eliminarProducto(folioT);
                    if(res){
                        //Nos aseguramos de que existe al menos un registro
                        Log.e(tag, "base prodcutos borrada");
                    }
                    datos.getDb().setTransactionSuccessful();
                } finally {
                    datos.getDb().endTransaction();
                }
            }else{
                //Toast.makeText(getApplicationContext(),"Aún no se envia información",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public class enviarStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.e("Status","Entregada");
            boolean status = WebService.invokeComanda(folioT, "Entregada");
            return null;
        }
    }

    private class enviarFotos extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Call Web Method
            convert64();
            status = WebService.invokeImagenWS(folioT,list64.get(0),"Foto1");Log.d("CICL0 ws", "0 "+status);
            status = WebService.invokeImagenWS(folioT,list64.get(1),"Foto2");Log.d("CICL0 ws", "1 "+status);
            status = WebService.invokeImagenWS(folioT,list64.get(2),"Foto3");Log.d("CICL0 ws", "2 "+status);
            status = WebService.invokeImagenWS(folioT,list64.get(3),"Firma");Log.d("CICL0 ws", "3 "+status);

            Log.d("imagen ws","termine ed enviar");
            return null;
        }
        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            if(status){
                Log.e(tag, "Se envio prodcutos completos");
                try {
                    datos.getDb().beginTransaction();
                    boolean res =datos.eliminarDocumentos(folioT);
                    if(res){
                        //Nos aseguramos de que existe al menos un registro
                        Log.e(tag, "base fotos borrada");
                    }
                    datos.getDb().setTransactionSuccessful();
                } finally {
                    datos.getDb().endTransaction();
                }
            }

        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

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

    public boolean numRegistros(String nametable){
        boolean res = false;
        try {
            //Log.e(tag, "get dat0s");
            datos.getDb().beginTransaction();
            long cont =datos.contarRegistros(nametable);
            Log.e(tag,"La tabla tiene " + nametable + ": "+cont);
            if(cont > 0){
                //Nos aseguramos de que existe al menos un registro
                res = true;
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        return res;
    }

    private void convert64(){
        List<String> list64path = new ArrayList<>();
        list64path.add(doc.foto1);
        list64path.add(doc.foto2);
        list64path.add(doc.foto3);
        list64path.add(doc.firma);
        for(int i = 0; i < list64path.size(); i++){
            String xxx = encodeImage(list64path.get(i));
            Log.e(tag, " TAM: " +xxx.length());
            list64.add(xxx);
        }
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
