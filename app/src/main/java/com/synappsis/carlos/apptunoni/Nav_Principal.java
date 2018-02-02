package com.synappsis.carlos.apptunoni;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;


public class Nav_Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, EntregaProceso.OnFragmentInteractionListener, ViajesAsignados.OnFragmentInteractionListener {
    public static Activity firtsA;
    OperacionesBaseDatos datos = null;
    int cambiarFragment = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav__principal);
        firtsA = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment fragmento = new ViajesAsignados();
        getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor,fragmento).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        datos = OperacionesBaseDatos
                .obtenerInstancia(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav__principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //AQUI PARA BLOQUEAR MENU
        consultar();
        int id = item.getItemId();
        Fragment fragmento = null;
        boolean seleccion = false;
        if(cambiarFragment != 0){
            if(cambiarFragment == 1){
                if (id == R.id.nav_camera) {
                    fragmento = new EntregaProceso();
                    seleccion = true;
                } else if (id == R.id.nav_gallery) {
                    fragmento = new ViajesAsignados();
                    seleccion=true;
                }
            }else{
                Toast.makeText(this, "Proceso de entrega en camino", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        else
        {
            Toast.makeText(this, "El proceso aún no esta terminado", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(seleccion)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor,fragmento).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void consultar() {
        try {
            datos.getDb().beginTransaction();
            //int a = 1;
            Cursor cursor =datos.obtenerEstatus();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("estatus");
                    String estado = cursor.getString(columna);
                    Log.d("QUERY", "Termine task:" +estado);
                    if(estado.equals("Sin Enviar"))
                        cambiarFragment=0;
                    else if(estado.equals("Aceptado"))
                        cambiarFragment=1;
                    else if(estado.equals("En Camino"))
                        cambiarFragment=2;
                }
            }
            datos.getDb().setTransactionSuccessful();
            Log.d("QUERY", "Termine task:" +cambiarFragment);
        } finally {
            datos.getDb().endTransaction();
        }
        return;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /*TEST DE BASE DE DATOS*/
    public class obtenerStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // [INSERCIONES]
            try {
                datos.getDb().beginTransaction();
                //int a = 1;
                Cursor cursor =datos.obtenerEstatus();
                if(cursor!=null){
                    if (cursor.moveToFirst()) {
                        int columna = cursor.getColumnIndex("estatus");
                        String estado = cursor.getString(columna);
                        if(estado == "Sin Enviar")
                            cambiarFragment=0;
                        else if(estado=="Aceptado")
                            cambiarFragment=1;
                        else if(estado=="En Camino")
                            cambiarFragment=2;
                    }
                }
                datos.getDb().setTransactionSuccessful();
                Log.d("QUERY", "Termine task:" +cambiarFragment);
            } finally {
                datos.getDb().endTransaction();
            }
            // [QUERIES]
            return null;
        }
    }

}
