package com.synappsis.carlos.apptunoni;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private static String NAMESPACE = "http://WebServices/";
    private static String METHOD_NAME = "BuscarProducto";
    private static String URL = "http://148.204.186.243:8080/WebservicesPrueba/WebservicesProducto?wsdl";

    private SoapObject request=null;
    private SoapSerializationEnvelope envelope=null;
    private SoapPrimitive resultsRequestSOAP=null;
    TextView text;
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
        //text = (TextView) getView().findViewById(R.id.prueba);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
            //propertyInfo.setType(PropertyInfo.OBJECT_TYPE);
            Log.e(tag, "adentr del parametros");
            //soapObject.addProperty(propertyInfo);
            SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
            //envelope.dotNet=true;
            envelope.setOutputSoapObject(soapObject);
            Log.e(tag, "objeto envuelto");
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            try {
                Log.e(tag, "adentr del try");
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
