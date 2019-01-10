package com.synappsis.carlos.apptunoni;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.synappsis.carlos.apptunoni.entidades.Comanda;
import com.synappsis.carlos.apptunoni.entidades.Entrega;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;
import com.synappsis.carlos.apptunoni.entidades.Producto;
import com.synappsis.carlos.apptunoni.entidades.Usuario;
import com.synappsis.carlos.apptunoni.entidades.App;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ViajesAsignados extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ExpandableListView listViewVar;
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    OperacionesBaseDatos datos = null;
    private int lastExpandedPosition = -1;
    private int grupActual = -1;
    private OnFragmentInteractionListener mListener;
    /*base de datos*/
    static boolean errored = false;
    private String UserComanda ="";
    String folioString = "";
    ArrayList<Entrega> datosComanda = new ArrayList<Entrega>();
    ArrayList<Producto> pComanda = new ArrayList<Producto>();
    String grupotext=null;
    String vistaSave=null;

    public ViajesAsignados() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ViajesAsignados newInstance(String param1, String param2) {
        ViajesAsignados fragment = new ViajesAsignados();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_viajes_asignados, container, false);
        datos = OperacionesBaseDatos.obtenerInstancia(getContext());
        getUser();
        Log.d("ViajesAsigandos", "Estoy en el viajes asignados");
        if(isOnline(getContext())){
            if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                new AsyncCallWS().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new AsyncCallWS().execute();
            }
        }
        else{
            datos.getDb().beginTransaction();
            Log.d("ViajesAsigandos", "begintransacitión2");
            try {
                Log.e("ViajesAsigandos", "get dat0s");
                datos.getDb().beginTransaction();
                Cursor cursor1 =datos.obtenerEntregas();
                if(cursor1!=null){
                    //Nos aseguramos de que existe al menos un registro
                    if (cursor1.moveToFirst()) {
                        //Recorremos el cursor hasta que no haya más registros
                        do {
                            Entrega e = new Entrega();
                            int columna = cursor1.getColumnIndex("folio");
                            e.folio = cursor1.getString(columna);
                            int columna2 = cursor1.getColumnIndex("estatus");
                            e.estatus = cursor1.getString(columna2);
                            int columna3 = cursor1.getColumnIndex("dirOrigen");
                            e.dirorigen = cursor1.getString(columna3);
                            int columna4 = cursor1.getColumnIndex("fechaOrigen");
                            e.fechaorigen = cursor1.getString(columna4);
                            int columna5 = cursor1.getColumnIndex("nombre");
                            e.nombre = cursor1.getString(columna5);
                            int columna6 = cursor1.getColumnIndex("nombredestino");
                            e.nombredestino = cursor1.getString(columna6);
                            int columna7 = cursor1.getColumnIndex("dirDestino");
                            e.dirdestino = cursor1.getString(columna7);
                            int columna8 = cursor1.getColumnIndex("fechaDestino");
                            e.fechadestino = cursor1.getString(columna8);
                            int columna9 = cursor1.getColumnIndex("nombreReceptor");
                            e.nombrereceptor = cursor1.getString(columna9);
                            int columna10 = cursor1.getColumnIndex("infoAdicional");
                            e.info = cursor1.getString(columna10);
                            int columna11 = cursor1.getColumnIndex("Usuario_nombre");
                            e.usuario_nombre = cursor1.getString(columna11);
                            if(cursor1.getString(columna2).equals("Por salir"))
                            {
                                datosComanda.add(e);
                            }
                        } while(cursor1.moveToNext());
                    }
                }
                datos.getDb().setTransactionSuccessful();
            } finally {
                datos.getDb().endTransaction();
            }

        }

        listViewVar =(ExpandableListView)rootView.findViewById(R.id.listview);
        // preparing list data rootView
        final Button button = rootView.findViewById(R.id.asignarViaje);
        final Button actBoton = rootView.findViewById(R.id.actualizar);
        vistaSave = obtenerEstado();
        if(vistaSave.equals("Aceptado")) {
            button.setEnabled(false);
            actBoton.setEnabled(false);
            Cursor folio = datos.obtenerApp();
            if(folio!=null){
                if (folio.moveToFirst()) {
                    int columna = folio.getColumnIndex("folio");
                    folioString = folio.getString(columna);
                }
                Log.e("ViajesAsigandos", "folio: "+folioString);
            }
            Toast.makeText(getContext(), "Se eligio el Folio: "+folioString, Toast.LENGTH_SHORT).show();
        }else {
            button.setEnabled(true);
            actBoton.setEnabled(true);
            Toast.makeText(getContext(), "Cargando...", Toast.LENGTH_SHORT).show();
        }
        //prepareListData();

        if(listDataHeader != null || listDataChild != null) {
            listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
            // setting list adapter
            listViewVar.setAdapter(listAdapter);
        }

        if(!isOnline(getContext())){
            if(!datosComanda.isEmpty()){
                if(datosComanda.get(0).folio!=null){
                    llenarTabs();
                    refrescar();
                }
                else
                    Toast.makeText(getContext(),"No hay envios, vuelve a intentar más tarde",Toast.LENGTH_SHORT).show();
            }else{
                //Set Error message
                Toast.makeText(getContext(),"No hay envios, vuelve a intentar más tarde",Toast.LENGTH_SHORT).show();
            }
        }
        listViewVar.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    listViewVar.collapseGroup(lastExpandedPosition);
                    //listAdapter.disableCheck(lastExpandedPosition, );
                    listAdapter.setGroupViewData(lastExpandedPosition,"0");
                }
                lastExpandedPosition = groupPosition;
                grupActual = groupPosition;
                //listAdapter.enableCheck(groupPosition, );
                listAdapter.setGroupViewData(groupPosition,"1");
            }
        });
        listViewVar.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                listAdapter.setGroupViewData(groupPosition,"0");
                grupActual = -1;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(grupActual<0){
                    Toast.makeText(getContext(), "No ha seleccionado ninguna entrega", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Aviso");
                    dialogo1.setMessage("¿Continuar con el proceso de entrega?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            grupotext = listAdapter.getNameGrup(grupActual);
                            new actualizarStatus().execute();
                            new enviarStatus().execute();
                            Toast.makeText(getContext(), "Ha seleccionado: "+ grupotext, Toast.LENGTH_SHORT).show();

                            button.setEnabled(false);
                            irEntregaProceso();

                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //Toast.makeText(getContext(), "Ha seleccionado: "+grupActual, Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogo1.show();

                }
            }
        });
        actBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapter=null;
                grupActual = -1;
                datosComanda.removeAll(datosComanda);
                listViewVar.setAdapter(listAdapter);
                Toast.makeText(getContext(), "Actualizando datos", Toast.LENGTH_SHORT).show();
                datos.getDb().close();
                if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                    new AsyncCallWS().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new AsyncCallWS().execute();
                }
            }
        });
        return rootView;
    }

    private void irEntregaProceso() {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.replace(R.id.Contenedor,new EntregaProceso());
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commit();
    }

    private void refrescar() {
        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
        // setting list adapter
        listViewVar.setAdapter(listAdapter);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public boolean llenarTabs(){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        int conteo = 0;
        if(datosComanda!=null){
            for(int tab =0; tab<datosComanda.size();tab++)
            {
                Entrega e = datosComanda.get(tab);
                listDataHeader.add(e.folio);
                conteo++;
            }
            for(int tab =0; tab<conteo;tab++)
            {
                Entrega e = datosComanda.get(tab);
                List<String> top250 = new ArrayList<String>();
                top250.add("Estatus: "+e.estatus);
                //top250.add("Dirección de Origen: ");
                top250.add("Fecha de Origen: "+e.fechaorigen);
                top250.add("Nombre: "+e.nombre);
                top250.add("Dirección de Destino: "+e.dirdestino);
                top250.add("Fecha de Destino: "+e.fechadestino);
                top250.add("Nombre Receptor: "+e.nombrereceptor);
                //top250.add("Información Adicional: ---");
                listDataChild.put(listDataHeader.get(tab), top250);
            }
        }
        else
            return false;
        return true;
    }

    /*CLASE PARA CONEXION AL WEB SERVICE*/

     private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Call Web Method
            Log.d("ViajesAsigandos", "Estoy en el WS");
            Entrega[] comanda = null;
            try{
                comanda = WebService.invokeGetComanda(UserComanda,"ComandaPendientes");
            }catch (Exception e){
                MainActivity.errored = true;
                e.printStackTrace();
            }

            if(comanda.length == 0){
                return null;
            }
            for(int i=0;i<comanda.length;i++)
            {
                datosComanda.add(comanda[i]);
            }
            Log.d("ViajesAsigandos", "Numero de c: "+comanda.length);
            datos.getDb().beginTransaction();
            //Log.d("ViajesAsigandos", "begintransacitión");
            try { Log.d("ViajesAsigandos", "Estoy en el TRY");
                if(datosComanda.get(0).folio!=null){
                    for(int i = 0; i<datosComanda.size();i++){
                        Entrega llenar = datosComanda.get(i);
                        datos.insertarEntrega(llenar);
                        Log.d("ViajesAsigandos", "Folio: "+datosComanda.get(i).folio);
                    }
                }else{ Log.d("ViajesAsigandos", "datos vacios");}
                datos.getDb().setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();Log.d("ViajesAsigandos", "Error" +e.toString());
                datos.getDb().endTransaction();
            }
            finally {
                datos.getDb().endTransaction();

            }
            // [QUERIES]
            Log.d("USER","----------------Obtencion de base de datos");
            DatabaseUtils.dumpCursor(datos.obtenerEntregas(UserComanda));


            //insertar pr0duct0s

            //ciclo para guardar productos con folio
            if(datosComanda.size() > 0){
                for (int j = 0; j < datosComanda.size(); j++)
                {
                    String folio = datosComanda.get(j).folio;
                    //do something with i
                    Log.d("ViajesAsigandosPR0DUCT0", "Estoy en el WS");
                    Producto[] listaProducto = WebService.invokeGetProduct(UserComanda, folio);
                    Log.d("ViajesAsigandosPR0DUCT0", "Sali");
                    if(listaProducto.length == 0){
                        return null;
                    }
                    Log.d("ViajesAsigandosPR0DUCT0", "Numero de pr0duct0s: "+listaProducto.length);
                    for(int i=0;i<listaProducto.length;i++)
                    {
                        pComanda.add(listaProducto[i]);
                    }
                    datos.getDb().beginTransaction();
                    Log.d("ViajesAsigandosPR0DUCT0", "pr0duct0s de c: "+pComanda.size());
                    try { Log.d("ViajesAsigandosPR0DUCT0", "Estoy en el TRY");
                        if(pComanda.get(0).producto!=null){
                            for(int i = 0; i<pComanda.size();i++){
                                Producto llenar = pComanda.get(i);
                                datos.insertarProducto(llenar);
                                Log.d("ViajesAsigandosPR0DUCT0", "Folio: "+pComanda.get(i).entrega_folio);
                            }
                        }else{ Log.d("ViajesAsigandosPR0DUCT0", "datos P vacios");}
                        datos.getDb().setTransactionSuccessful();
                    }catch (Exception e){
                        e.printStackTrace();Log.d("ViajesAsigandosPR0DUCT0", "Error" +e.toString());
                        datos.getDb().endTransaction();
                    }
                    finally {
                        datos.getDb().endTransaction();

                    }
                    // [QUERIES]
                    Log.d("USER","----------------Obtencion de base de datos");
                    DatabaseUtils.dumpCursor(datos.obtenerProducto(UserComanda));
                }
            }


            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Error status is false
            if(!errored){
                //Based on Boolean value returned from WebService
                Log.d("ViajesAsigandos", "Estoy en el POST");
                if(!datosComanda.isEmpty()){
                    if(datosComanda.get(0).folio!=null){
                        llenarTabs();
                        refrescar();
                    }
                    else
                        Toast.makeText(getContext(),"No hay envios, vuelve a intentar más tarde",Toast.LENGTH_SHORT).show();
                }else{
                    //Set Error message
                    Toast.makeText(getContext(),"No hay envios, vuelve a intentar más tarde",Toast.LENGTH_SHORT).show();
                }
                //Error status is true
            }else{
                Toast.makeText(getContext(),"Error de conexion al Servidor",Toast.LENGTH_SHORT).show();
            }
            //Re-initialize Error Status to False
            errored = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {
            //webservicePG.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    /*TEST DE BASE DE DATOS*/
    public class enviarStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean status = WebService.invokeComanda(grupotext, "Seleccionada");
            return null;
        }
    }

    /*TEST DE BASE DE DATOS*/
    public class actualizarStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // [INSERCIONES]
            try {
                datos.getDb().beginTransaction();
                //int a = 1;
                Cursor cursor =datos.actualizarFolio(grupotext);
                if(cursor!=null){
                    if (cursor.moveToFirst()) {
                        int columna = cursor.getColumnIndex("folio");
                        String estado = cursor.getString(columna);
                        Log.d("ViajesAsigandos", estado);
                    }
                }
                else{Log.d("ViajesAsigandos", "Error en query 1");}
                Cursor cursor2 =datos.actualizarStatus("Aceptado", grupotext);
                if(cursor2!=null){
                    if (cursor2.moveToFirst()) {
                        int columna = cursor2.getColumnIndex("folio");
                        String estado = cursor2.getString(columna);
                        Log.d("ViajesAsigandos", estado);
                    }
                }
                else{Log.d("ViajesAsigandos", "Error en query 2");}
                datos.getDb().setTransactionSuccessful();
            } finally {
                datos.getDb().endTransaction();
            }
            // [QUERIES]
            Log.d("USER","----------------Obtencion de base de datos de Viajes asignados "+ UserComanda);
            DatabaseUtils.dumpCursor(datos.obtenerApp());
            return null;
        }
    }

    private String obtenerEstado() {
        String resStatus = "";
        try {
            Log.e("ViajesAsigandos", "Actualizar");
            datos.getDb().beginTransaction();
            Cursor cursor =datos.obtenerApp();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("estatus");
                    vistaSave = cursor.getString(columna);
                }
                if(vistaSave!=null)
                    if(!vistaSave.isEmpty())
                        resStatus = vistaSave;
                    else
                        resStatus="Error";
                else
                    resStatus="Error";
                Log.e("ViajesAsigandos", "ESTATUS: "+vistaSave);
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

    private void getUser(){
        try {
            datos.getDb().beginTransaction();
            //int a = 1;
            Log.d("ViajesAsigandos","get user()");
            Cursor cursor =datos.obtenerUser();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("nombre");
                    UserComanda = cursor.getString(columna);
                    Log.d("ViajesAsigandos","user: "+UserComanda);
                }
            }
            else{
                Log.d("USER","Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        // [QUERIES]
        Log.d("USER","----------------Obtencion de base de datos de Viajes asignados "+ UserComanda);
    }

    public static boolean isOnline(Context context) {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

}
