package com.synappsis.carlos.apptunoni;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;
import com.synappsis.carlos.apptunoni.entidades.Producto;

import java.util.ArrayList;
import java.util.List;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();
    OperacionesBaseDatos datos = null;
    String folioT ="";
    List<Producto> LISTAP = new ArrayList<Producto>();

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
        // Ejecutar operación aquí
        getFolio();
        getProducts(folioT);
        if(LISTAP.size() != 0)
        {
            for (int i = 0; i < LISTAP.size(); i++) {
                Log.d("tabla check",""+ LISTAP.get(i).producto);
            }
        }


    }

    public String getFolio(){
        try {
            Log.e("PRODUCTO", "GUARDANDO FOTOS");
            datos.getDb().beginTransaction();
            Cursor cursor1 =datos.obtenerApp();
            if(cursor1!=null){
                if (cursor1.moveToFirst()) {
                    int columna = cursor1.getColumnIndex("folio");
                    folioT = cursor1.getString(columna);
                }
                Log.e("ESTAD0", "folioT-U: "+folioT);
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        return folioT;
    }

    public void getProducts(String f){
        try {
            Log.e("PRODUCTO", "get dat0s");
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
                        int columna2 = cursor1.getColumnIndex("cantidad");
                        p.cantidad = cursor1.getString(columna2);
                        LISTAP.add(p);
                    } while(cursor1.moveToNext());
                }
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
    }



}