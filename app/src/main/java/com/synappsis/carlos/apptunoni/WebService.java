package com.synappsis.carlos.apptunoni;
import android.util.Log;

import com.synappsis.carlos.apptunoni.entidades.Entrega;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by CARLOS on 13/12/2017.
 */
public class WebService {
    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://WebServicesApp/";
    //Webservice URL - WSDL File location
    private static String URL = "http://192.241.195.227:8080/SeguimientoTunoni/ControlApp?wsdl";//Make sure you changed IP address
    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = "http://192.241.195.227:8080/SeguimientoTunoni/ControlApp";
    //
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public static boolean invokeLoginWS(String userName,String passWord, String webMethName) {
        boolean loginStatus = false;
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // Property which holds input parameters
        PropertyInfo unamePI = new PropertyInfo();
        PropertyInfo passPI = new PropertyInfo();
        // Set Username
        unamePI.setName("usuario");
        // Set Value
        unamePI.setValue(userName);
        // Set dataType
        unamePI.setType(String.class);
        // Add the property to request object
        request.addProperty(unamePI);
        //Set Password
        passPI.setName("password");
        //Set dataType
        passPI.setValue(passWord);
        //Set dataType
        passPI.setType(String.class);
        //Add the property to request object
        request.addProperty(passPI);
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            // Invoke web service
            Log.d("S0AP",SOAP_ACTION+webMethName+"");
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            // Assign it to  boolean variable variable
            loginStatus = Boolean.parseBoolean(response.toString());

        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            MainActivity.errored = true;
            e.printStackTrace();
        }
        //Return booleam to calling object
        Log.d("l0gin",loginStatus+"");
        return loginStatus;
    }

    public static List<String> invokeAsignadosWS(){
        List<String> list = new ArrayList<String>();

        list.add("one");
        list.add("two");
        return list;
    }

    public static Entrega invokeGetComanda(String UserComanda, String webMethName) {
        //Entrega res = "";
        //UserComanda = "LJ928J";
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // Property which holds input parameters
        PropertyInfo unamePI = new PropertyInfo();
        PropertyInfo passPI = new PropertyInfo();
        // Set licencia
        unamePI.setName("licencia");
        // Set Value
        unamePI.setValue(UserComanda);
        // Set dataType
        unamePI.setType(String.class);
        // Add the property to request object
        request.addProperty(unamePI);
        //Set fecha
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String fecha = dateFormat.format(date);
        passPI.setName("fecha");
        //Set dataType
        passPI.setValue(fecha);
        //Set dataType
        passPI.setType(String.class);
        //Add the property to request object
        request.addProperty(passPI);
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        Entrega entrega = new Entrega();
        try {
            // Invoke web service
            Log.d("S0AP",SOAP_ACTION+webMethName+"");
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);
            // Get the response
            //SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            SoapObject resSoap =(SoapObject)envelope.getResponse();
            entrega.folio = resSoap.getProperty(0).toString();
            entrega.estatus = resSoap.getProperty(1).toString();
            entrega.fechadestino = resSoap.getProperty(2).toString();
            entrega.fechaorigen = resSoap.getProperty(3).toString();
            entrega.nombre = resSoap.getProperty(4).toString();
            entrega.dirdestino = resSoap.getProperty(5).toString();
            entrega.nombrereceptor = resSoap.getProperty(6).toString();
            entrega.info = resSoap.getProperty(7).toString();
            entrega.usuario_nombre = UserComanda;

        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            MainActivity.errored = true;
            e.printStackTrace();
        }
        return entrega;
    }
}
