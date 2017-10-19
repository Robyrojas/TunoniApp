package com.synappsis.carlos.apptunoni;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
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
public class EntregaProceso extends Fragment {
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

    private static String NAMESPACE = "http://148.204.186.243:8080/";
    private static String METHOD_NAME = "BuscarProducto";

    private static String URL = "http://148.204.186.243:8080/WebservicesPrueba/WebservicesProducto?wsdl";

    private OnFragmentInteractionListener mListener;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        Log.e(tag, "LLego aqui");
        //Use this to add parameters
        //request.addProperty("Parameter","Value");

        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        Log.e(tag, "envelope init");
        //Needed to make the internet call
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {
            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);
            Log.e(tag, "envelope");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Get the SoapResult from the envelope body.
        SoapObject result = (SoapObject)envelope.bodyIn;
        //Log.d("myTag", "SOAP response:\n\n" + result.getProperty(0).toString());
        if(result != null){
            TextView testo =  (TextView) getView().findViewById(R.id.prueba);
            //Get the first property and change the label text
            String soap = "SOAP response:\n\n" + result.getProperty(0).toString();
            Log.e(tag,soap);
            testo.setText(soap);
        }
        else{
            Log.e(tag,"No hay result");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrega_proceso, container, false);
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
