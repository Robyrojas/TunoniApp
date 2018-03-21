package com.synappsis.carlos.apptunoni;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EntregaProceso.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EntregaProceso#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntregaProceso extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //Conexion
    private static String tag = "EntregaProceso";

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private OnFragmentInteractionListener mListener;
    private GoogleMap mapa;
    private Marker mMarcadorActual1;
    private Marker mMarcadorActual2;
    private SupportMapFragment mSupportMapFragment;
    Spinner spinnerOpc;
    int enCAMINO = 0;
    OperacionesBaseDatos datos = null;
    private String UserComanda;
    private String Foliomaps;
    String vistaSave = null;
    double longitudeGPS = 0, latitudeGPS = 0;
    /*mapas*/
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    public EntregaProceso() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EntregaProceso newInstance(String param1, String param2) {
        EntregaProceso fragment = new EntregaProceso();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //text = (TextView) getView().findViewById(R.id.prueba);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        datos = OperacionesBaseDatos
                .obtenerInstancia(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_entrega_proceso, container, false);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(getContext(), "Aún no terminas el proceso", Toast.LENGTH_SHORT).show();
                    //return true;
                }
                Log.d(tag, "back");
                return true;
            }
        });
        String[] values =
                {"Selecionar", "En Camino", "Entregar"};
        spinnerOpc = (Spinner) v.findViewById(R.id.spinnerList);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this.getActivity(), android.R.layout.simple_spinner_item, values) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Nullable
            @Override
            public String getItem(int position) {
                return super.getItem(position);
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerOpc.setAdapter(spinnerArrayAdapter);
        vistaSave = obtenerEstado();
        if (vistaSave.equals("En Camino")) {
            spinnerOpc.setSelection(1);
            Cursor folio = datos.obtenerApp();
            String folioString = "";
            if (folio != null) {
                if (folio.moveToFirst()) {
                    int columna = folio.getColumnIndex("folio");
                    folioString = folio.getString(columna);
                }
                Log.e("ESTAD0", "folio: " + folioString);
            }
            Toast.makeText(getContext(), "Se eligio el Folio: " + folioString, Toast.LENGTH_SHORT).show();
        }
        spinnerOpc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerOpc.getSelectedItem().toString().equals("Entregar")) {
                    if (enCAMINO == 1) {
                        createAndShowAlertDialog();
                    } else {
                        Toast.makeText(getActivity(), "Aún no estas en camino", Toast.LENGTH_SHORT).show();
                        spinnerOpc.setSelection(0);
                    }
                } else if (spinnerOpc.getSelectedItemPosition() == 1) {
                    //enCAMINO = getStatus();
                    if (enCAMINO == 0) {
                        actualizarStatus("En Camino");
                        new enviarStatus().execute();
                        Toast.makeText(getActivity(), "Estas en Camino", Toast.LENGTH_LONG).show();
                        enCAMINO = 1;
                    }
                    //else
                    // Toast.makeText(getActivity(), "Opción ya seleccionada", Toast.LENGTH_SHORT).show();
                } else {
                    if (spinnerOpc.getSelectedItem().toString().equals("Entregado")) {
                        spinnerOpc.setSelection(1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mSupportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            //Log.e(tag, "Es nulo y vacio");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mSupportMapFragment).commit();
            //Log.e(tag, "se lleno");
        }
        obtenerfolio();
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        if (mSupportMapFragment != null) {
            //Log.e(tag, "fragment no nulo");
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        Log.e(tag, "mapa lleno");
                        mapa = googleMap;
                        mapa.getUiSettings().setAllGesturesEnabled(true);
                        mapa.getUiSettings().setZoomControlsEnabled(true);
                        //LatLng sydney = new LatLng(-33.87365, 151.20689);
                        //MarkerOptions marker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
                        LatLng origen, destino;
                        if(Foliomaps!=null){
                            if(Foliomaps.isEmpty())
                                obtenerOrigen();
                        }
                        if (longitudeGPS != 0 && latitudeGPS != 0) {
                            origen = new LatLng(latitudeGPS, longitudeGPS);
                        } else {
                            origen = new LatLng(19.430464, -99.135046);//tunoni
                        }
                        destino = obtenerdestino();
                        mMarcadorActual1 = mapa.addMarker(new MarkerOptions().position(origen).title("Origen"));
                        mMarcadorActual2 = mapa.addMarker(new MarkerOptions().position(destino).title("Destino"));
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(mMarcadorActual1.getPosition());
                        builder.include(mMarcadorActual2.getPosition());
                        LatLngBounds bounds = builder.build();
                        imprimirdatos(origen,destino);
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = 200; // offset from edges of the map in pixels
                        try {
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                            googleMap.moveCamera(cu);
                            Log.e(tag, "mapa terminado");
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage().toString());
                            Toast.makeText(getContext(), "Error al cargar el Mapa sin acceso a internet", Toast.LENGTH_SHORT).show();
                        }
                        if(!obtenerOrigen()){
                            getLocation();
                        }
                    }

                }
            });
        }

        return v;
    }

    private String obtenerEstado() {
        String resStatus = "Error";
        try {
            Log.e("ESTAD0", "obtener estado");
            datos.getDb().beginTransaction();
            Cursor cursor = datos.obtenerApp();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("estatus");
                    vistaSave = cursor.getString(columna);
                }
                Log.e("ESTAD0", "ESTATUS: " + vistaSave);
                if(vistaSave!=null){
                    if (!vistaSave.isEmpty())
                        resStatus = vistaSave;
                }
            } else {
                Log.d("USER", "Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerApp());
        return resStatus;
    }

    private void actualizarStatus(String statusNew) {
        try {
            Log.e(tag, "Actualizar");
            datos.getDb().beginTransaction();
            //int a = 1;
            Cursor cursor = datos.obtenerEstatus();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("folio");
                    UserComanda = cursor.getString(columna);
                }
                Log.e(tag, "user: " + UserComanda);
                Cursor cursor2 = datos.actualizarStatus(statusNew, UserComanda);
                if (cursor2 != null) {
                    Log.e(tag, "Si hay actualizar estado");
                    if (cursor2.moveToFirst()) {
                        int columna = cursor2.getColumnIndex("folio");
                        String estado = cursor2.getString(columna);
                        Log.d("QUERY", estado);
                    }
                } else {
                    Log.d("QUERY", "Error en query 2");
                }
            } else {
                Log.d("USER", "Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerApp());
    }

    private void obtenerfolio() {
        try {
            datos.getDb().beginTransaction();
            Cursor cursor = datos.obtenerApp();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("folio");
                    Foliomaps = cursor.getString(columna);
                    Log.d("USER", "Foliomaps: "+Foliomaps);
                }
            } else {
                Log.d("USER", "Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
    }

    private boolean obtenerOrigen() {
        boolean result=false;
        try {
            datos.getDb().beginTransaction();
            //int a = 1;
            Cursor cursor = datos.obtenerEntregas(Foliomaps);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("dirorigen");
                    String geo = cursor.getString(columna);
                    if(geo!=null){
                        if(!geo.isEmpty()){
                            latitudeGPS = Double.parseDouble(geo.split(",")[0]);
                            longitudeGPS = Double.parseDouble(geo.split(",")[1]);
                            result= true;
                        }
                    }
                }
            } else {
                Log.d("USER", "Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        return result;
    }

    private LatLng obtenerdestino() {
        LatLng result=null;
        try {
            datos.getDb().beginTransaction();
            //int a = 1;
            Cursor cursor = datos.obtenerEntregas();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("dirDestino");
                    String geo = cursor.getString(columna);
                    if(geo!=null){
                        if(!geo.isEmpty()){
                            LatLng regresoDEstino = new LatLng(Double.parseDouble(geo.split(",")[0]), Double.parseDouble(geo.split(",")[1]));
                            result= regresoDEstino;
                        }
                    }
                }
            } else {
                Log.d("USER", "Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        return result;
    }

    private void guardarLatLong(String s) {
        try {
            datos.getDb().beginTransaction();
            //int a = 1;
            Cursor cursor = datos.actualizarOrigen(s,Foliomaps);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("dirorigen");
                    String ac = cursor.getString(columna);
                    Log.d("UPDATE ORI", ac);
                }
            } else {
                Log.d("USER", "Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
    }

    public void imprimirdatos(LatLng ori, LatLng des){
        ((TextView)getView().findViewById(R.id.dirSalida1)).setText("Lat: " + String.valueOf(ori.latitude));
        ((TextView)getView().findViewById(R.id.dirSalida2)).setText("Lng: " +String.valueOf(ori.longitude));
        ((TextView)getView().findViewById(R.id.dirEntrega1)).setText("Lat: " +String.valueOf(des.latitude));
        ((TextView)getView().findViewById(R.id.dirEntrega2)).setText("Lng: " +String.valueOf(des.longitude));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("¿Continuar con la entrega de mercancia?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                Log.e(tag, "SI");
                actualizarStatus("Entregando");
                new enviarProceso().execute();
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), productos.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                Log.e(tag, "NO");
                dialog.dismiss();
                spinnerOpc.setSelection(1);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                spinnerOpc.setSelection(1);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

     void getLocation() {
        if(checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null){
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                Log.e("latidude: ",latti+" ");
                Log.e("longitude: ",longi+" ");
                latitudeGPS = latti;
                longitudeGPS = longi;
                guardarLatLong(latti+","+longi);
                //obtenerMapa();
                LatLng origen = new LatLng(latitudeGPS, longitudeGPS);
                LatLng destino = obtenerdestino();
                mapa.clear();
                mMarcadorActual1 = mapa.addMarker(new MarkerOptions().position(origen).title("Origen"));
                mMarcadorActual2 = mapa.addMarker(new MarkerOptions().position(destino).title("Destino"));
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(mMarcadorActual1.getPosition());
                builder.include(mMarcadorActual2.getPosition());
                LatLngBounds bounds = builder.build();
                imprimirdatos(origen,destino);
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = 200; // offset from edges of the map in pixels
                try {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                    mapa.moveCamera(cu);
                    Log.e(tag, "mapa terminado");
                } catch (Exception e) {
                    Log.e("Error", e.getMessage().toString());
                    Toast.makeText(getContext(), "Error al cargar el Mapa sin acceso a internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Desabilitado para encontrar la correcta locación", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(REQUEST_LOCATION == requestCode) {
            Log.e("LOCATION: ",REQUEST_LOCATION+" ");
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(getContext(), "Permisos denegados", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public class enviarStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean status = WebService.invokeComanda(Foliomaps, "En Camino");
            return null;
        }
    }
    public class enviarProceso extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean status = WebService.invokeComanda(Foliomaps, "Por Entregar");
            return null;
        }
    }
}
