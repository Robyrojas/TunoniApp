package com.synappsis.carlos.apptunoni;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EntregaProceso.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EntregaProceso#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntregaProceso extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //Conexion
    private static String tag="EntregaProceso";


    private static String SOAP_ACTION = "http://148.204.186.243:8080/WebservicesPrueba/WebservicesProducto";
    private static String NAMESPACE = "http://WebServices/";
    private static String METHOD_NAME = "BuscarProducto";
    private static String URL = "http://148.204.186.243:8080/WebservicesPrueba/WebservicesProducto?wsdl";

    private SoapObject request=null;
    private SoapSerializationEnvelope envelope=null;
    private SoapPrimitive resultsRequestSOAP=null;
    private OnFragmentInteractionListener mListener;
    private GoogleMap mapa;
    private Marker mMarcadorActual1;
    private Marker mMarcadorActual2;
    private SupportMapFragment mSupportMapFragment;
    Spinner spinnerOpc;
    int enCAMINO=0;
    OperacionesBaseDatos datos = null;
    private String UserComanda;
    String vistaSave=null;

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
        new CallWebService().execute("1");

    }

    class CallWebService extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            //text.setText("Result = " + s);
            Log.e(tag, "Result = " + s);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapObject.addProperty("id", "1");
            //Log.e(tag, "Inicio SOAP");
            //propertyInfo.setType(PropertyInfo.OBJECT_TYPE);
            //soapObject.addProperty(propertyInfo);
            SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
            //envelope.dotNet=true;
            envelope.setOutputSoapObject(soapObject);
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            try {
                httpTransportSE.call(SOAP_ACTION, envelope);
                //Log.e(tag, "envolvio");
                SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                result = soapPrimitive.toString();
                //Log.e(tag, "respuetaaaaa" + result.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(tag, String.valueOf(e));
            }
            return result;
        }
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
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Toast.makeText(getContext(), "Aún no terminas el proceso", Toast.LENGTH_SHORT).show();
                    //return true;
                }
                Log.d(tag,"back");
                return true;
            }
        });
        String [] values =
                {"Selecionar","En Camino","Entregar"};
        spinnerOpc = (Spinner) v.findViewById(R.id.spinnerList);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this.getActivity(),android.R.layout.simple_spinner_item, values){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
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
                if(position==0) {
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerOpc.setAdapter(spinnerArrayAdapter);
        vistaSave = obtenerEstado();
        if(vistaSave.equals("En Camino")) {
            spinnerOpc.setSelection(1);
            Cursor folio = datos.obtenerApp();
            String folioString = "";
            if(folio!=null){
                if (folio.moveToFirst()) {
                    int columna = folio.getColumnIndex("folio");
                    folioString = folio.getString(columna);
                }
                Log.e("ESTAD0", "folio: "+folioString);
            }
            Toast.makeText(getContext(), "Se eligio el Folio: "+folioString, Toast.LENGTH_SHORT).show();
        }
        spinnerOpc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerOpc.getSelectedItem().toString().equals("Entregar"))
                {
                    if(enCAMINO==1) {
                        createAndShowAlertDialog();
                    }
                    else{
                        Toast.makeText(getActivity(), "Aún no estas en camino", Toast.LENGTH_SHORT).show();
                        spinnerOpc.setSelection(0);
                    }
                }
                else if(spinnerOpc.getSelectedItemPosition() == 1)
                {
                    //enCAMINO = getStatus();
                    if(enCAMINO==0)
                    {
                        actualizarStatus("En Camino");
                        Toast.makeText(getActivity(), "Estas en Camino", Toast.LENGTH_LONG).show();
                        enCAMINO = 1;
                    }
                    //else
                       // Toast.makeText(getActivity(), "Opción ya seleccionada", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(spinnerOpc.getSelectedItem().toString().equals("Entregado")) {
                        spinnerOpc.setSelection(1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        //Log.e(tag, "Se lleno list");
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
                        mapa.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
                        {
                            @Override
                            public void onMyLocationChange(Location arg0) {
                                // TODO Auto-generated method stub

                                mapa.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                            }
                        });
                        LatLng origen = new LatLng(19.430464, -99.135046);
                        LatLng destino = new LatLng(19.026809, -98.178635);
                        mMarcadorActual1 = mapa.addMarker(new MarkerOptions().position(origen).title("Origen"));
                        mMarcadorActual2 = mapa.addMarker(new MarkerOptions().position(destino).title("Destino"));
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(mMarcadorActual1.getPosition());
                        builder.include(mMarcadorActual2.getPosition());
                        LatLngBounds bounds = builder.build();

                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = 100; // offset from edges of the map in pixels
                        try {
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,width,height, padding);
                            googleMap.moveCamera(cu);
                        }catch (Exception e) {
                            Log.e("Error",e.getMessage().toString());
                            Toast.makeText(getContext(), "Error al cargar el Mapa sin acceso a internet", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }

        return v;
    }



    private String obtenerEstado() {
        String resStatus = "";
        try {
            Log.e("ESTAD0", "obtener estado");
            datos.getDb().beginTransaction();
            Cursor cursor =datos.obtenerApp();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("estatus");
                    vistaSave = cursor.getString(columna);
                }
                Log.e("ESTAD0", "ESTATUS: "+vistaSave);
                resStatus = vistaSave;
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

    private void actualizarStatus(String statusNew) {
        try {
            Log.e(tag, "Actualizar");
            datos.getDb().beginTransaction();
            //int a = 1;
            Cursor cursor =datos.obtenerEstatus();
            if(cursor!=null){
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("folio");
                    UserComanda = cursor.getString(columna);
                }
                Log.e(tag, "user: "+UserComanda);
                Cursor cursor2 =datos.actualizarStatus(statusNew, UserComanda);
                if(cursor2!=null){Log.e(tag, "Si hay actualizar estado");
                    if (cursor2.moveToFirst()) {
                        int columna = cursor2.getColumnIndex("folio");
                        String estado = cursor2.getString(columna);
                        Log.d("QUERY", estado);
                    }
                }
                else{Log.d("QUERY", "Error en query 2");}
            }
            else{
                Log.d("USER","Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerApp());
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
                dialog.dismiss();
                Intent intent =  new Intent(getActivity(), productos.class);
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
}
