package com.synappsis.carlos.apptunoni;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.synappsis.carlos.apptunoni.entidades.Documentos;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;
import com.synappsis.carlos.apptunoni.entidades.Producto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();
    OperacionesBaseDatos datos = null;
    String folioT ="", UserComanda = "";
    List<Producto> LISTAP = new ArrayList<Producto>();
    String tag = "NETWORK-SERVICE";
    Documentos doc = new Documentos();
    String comentarioComanda = "";
    boolean status = false;
    List<String> nombreTablas = Arrays.asList("Usuario", "Producto","Documentos");
    List<String> list64 = new ArrayList<>();

    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        datos = OperacionesBaseDatos
                .obtenerInstancia(getApplicationContext());
        mConnectivityReceiver = new ConnectivityReceiver(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob" + mConnectivityReceiver);
        registerReceiver(mConnectivityReceiver, new IntentFilter(CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob");
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        String message = isConnected ? "Conectado a Internet" : "Sin conexión a Internet";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        //obtener usuario
        obtenerUser();
        //obtener folio
        getFolio();
        //obtener porductos
        getProducts(folioT);
        //obtener foto
        if(UserComanda!=null){
            getFotos(UserComanda);
        }
        boolean userExist =false;
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
        datos.getDb().close();
        Toast.makeText(getApplicationContext(),"Información Enviada",Toast.LENGTH_SHORT).show();
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
}