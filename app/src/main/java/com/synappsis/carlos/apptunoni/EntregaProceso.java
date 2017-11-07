package com.synappsis.carlos.apptunoni;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    TextView text;
    private OnFragmentInteractionListener mListener;
    private GoogleMap mapa;
    private Marker mMarcadorActual;
    private SupportMapFragment mSupportMapFragment;
    Spinner spinnerOpc;
    String [] contenido;
    ArrayAdapter <String> adaptador;

    public EntregaProceso() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntregaProceso.
     */
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
        new CallWebService().execute("1");

    }

    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        //ask for permissions..
        //mapa.setMyLocationEnabled(true);
        LatLng sydney = new LatLng(-34, 151);
        mMarcadorActual = mapa.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    }*/

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
            Log.e(tag, "Inicio SOAP");
            //propertyInfo.setType(PropertyInfo.OBJECT_TYPE);
            //soapObject.addProperty(propertyInfo);
            SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
            //envelope.dotNet=true;
            envelope.setOutputSoapObject(soapObject);
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            try {
                httpTransportSE.call(SOAP_ACTION, envelope);
                Log.e(tag, "envolvio");
                SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                result = soapPrimitive.toString();
                Log.e(tag, "respuetaaaaa" + result.toString());
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
        String [] values =
                {"Seleccionar","En Camino","Entregado",};
        spinnerOpc = (Spinner) v.findViewById(R.id.spinnerList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerOpc.setAdapter(adapter);
        spinnerOpc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerOpc.getSelectedItemPosition() == 2)
                {
                    createAndShowAlertDialog();
                }
                else if(spinnerOpc.getSelectedItemPosition() == 1)
                {
                    Toast.makeText(getActivity(), "Estas en Camino", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Log.e(tag, "Se lleno list");
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mSupportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            Log.e(tag, "Es nulo y vacio");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mSupportMapFragment).commit();
            Log.e(tag, "se lleno");
        }

        if (mSupportMapFragment != null) {
            Log.e(tag, "fragment no nulo");
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (googleMap != null) {
                        Log.e(tag, "mapa lleno");
                        mapa = googleMap;
                        mapa.getUiSettings().setAllGesturesEnabled(true);
                        mapa.getUiSettings().setZoomControlsEnabled(true);
                        LatLng sydney = new LatLng(-33.87365, 151.20689);
                        mMarcadorActual = mapa.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                        Log.e(tag,mapa.toString());
                        //CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(15.0f).build();
                        //CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        //mapa.moveCamera(cameraUpdate);
                        mapa.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    }

                }
            });
        }

        return v;
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
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                Log.e(tag, "NO");
                dialog.dismiss();
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
