package com.synappsis.carlos.apptunoni;
import android.util.Log;

import com.synappsis.carlos.apptunoni.entidades.Documentos;
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
import java.util.Date;
import java.util.List;

/**
 * Created by CARLOS on 13/12/2017.
 */
public class WebService {
    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://WebServicesApp/";
    //Webservice URL - WSDL File location // 192.241.195.227
    //http://148.204.4.150:8080/SeguimientoTunoni/
    private static String URL = "http://148.204.4.150:8080/SeguimientoTunoni/ControlApp?wsdl";//Make sure you changed IP address
    //SOAP Action URI again Namespace + Web method name


    private static String SOAP_ACTION = "http://148.204.4.150:8080/SeguimientoTunoni/ControlApp";
    private static int Timeout = 10000;
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
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,Timeout);
        //Log.d("Tiempo: ", "time: "+Timeout);
        try {
            // Invoke web service
            //Log.d("S0AP",SOAP_ACTION+webMethName+"");
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
        return loginStatus;
    }

    public static List<String> invokeAsignadosWS(){
        List<String> list = new ArrayList<String>();

        list.add("one");
        list.add("two");
        return list;
    }

    public static boolean invokeComanda(String folio, String estatus){
        boolean Status = false;
        String webMethName="UpdataComanda";
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // Property which holds input parameters
        PropertyInfo folioC = new PropertyInfo();
        PropertyInfo estadoC = new PropertyInfo();
        // Set folioPI
        folioC.setName("folio");
        // Set Value
        folioC.setValue(folio);
        // Set dataType
        folioC.setType(String.class);
        // Add the property to request object
        request.addProperty(folioC);
        // Set folioPI
        estadoC.setName("estado");
        // Set Value
        estadoC.setValue(estatus);
        // Set dataType
        estadoC.setType(String.class);
        // Add the property to request object
        request.addProperty(estadoC);
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,Timeout);
        //Log.d("Tiempo: ", "time: "+Timeout);
        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            // Assign it to  boolean variable variable
            Status = Boolean.parseBoolean(response.toString());

        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            MainActivity.errored = true;
            e.printStackTrace();
        }
        //Return booleam to calling object
        return Status;
    }

    public static Entrega[] invokeGetComanda(String UserComanda, String webMethName) {
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

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,Timeout);

        List<Entrega> entrega = new ArrayList<Entrega>();
        try {
            // Invoke web service
            //Log.d("S0AP",SOAP_ACTION+webMethName+"");
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);
            // Get the response
            //SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            SoapObject resSoap =(SoapObject)envelope.bodyIn;
            if(resSoap!=null) {
                for(int i = 0;i<resSoap.getPropertyCount();i++) {
                    SoapObject obj3 =(SoapObject) resSoap.getProperty(i);
                    Entrega item = new Entrega();
                    item.folio = obj3.getProperty(0).toString();
                    item.estatus = obj3.getProperty(1).toString();
                    item.fechadestino = obj3.getProperty(2).toString();
                    item.fechaorigen = obj3.getProperty(3).toString();
                    item.nombre = obj3.getProperty(4).toString();
                    item.dirdestino = obj3.getProperty(5).toString();
                    item.nombrereceptor = obj3.getProperty(6).toString();
                    item.info = obj3.getProperty(7).toString();
                    item.usuario_nombre = UserComanda; //Log.d("S0AP","Ya todo "+item.folio);
                    entrega.add(item);
                }
            }else{ Log.d("S0AP","Error vacío");}
        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            MainActivity.errored = true;
            e.printStackTrace();
        }
        Entrega[] array = entrega.toArray(new Entrega[entrega.size()]);
        return array;
    }

    public static boolean invokeImagenWS(String folio, String fotoC, String tipo) {
        boolean envioStatus = false;
        String webMethName="Updataimagen";
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // Property which holds input parameters
        PropertyInfo folioPI = new PropertyInfo();
        PropertyInfo fotoPI = new PropertyInfo();
        PropertyInfo tipoPI = new PropertyInfo();
        // Set folioPI
        folioPI.setName("folio");
        // Set Value
        folioPI.setValue(folio);
        // Set dataType
        folioPI.setType(String.class);
        // Add the property to request object
        request.addProperty(folioPI);
        //Set fotoPI
        fotoPI.setName("foto");
        //Set dataType
        fotoPI.setValue(fotoC);
        //Set dataType
        fotoPI.setType(String.class);
        //Add the property to request object
        request.addProperty(fotoPI);
        //Set tipoPI
        tipoPI.setName("tipo");
        //Set dataType
        tipoPI.setValue(tipo);
        //Set dataType
        tipoPI.setType(String.class);
        //Add the property to request object
        request.addProperty(tipoPI);
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,Timeout);
        //Log.d("Tiempo: ", "time: "+Timeout);
        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            // Assign it to  boolean variable variable
            envioStatus = Boolean.parseBoolean(response.toString());

        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            MainActivity.errored = true;
            e.printStackTrace();
        }
        //Return booleam to calling object
        return envioStatus;
    }

    public static boolean invokeUbicacion(String folio, String ubicacion, String tipo){
        boolean Status = false;
        String webMethName="UpdataUbicacion";
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // Property which holds input parameters
        PropertyInfo folioU = new PropertyInfo();
        PropertyInfo tipoU = new PropertyInfo();
        PropertyInfo ubicacionU = new PropertyInfo();
        // Set folioPI
        folioU.setName("folio");
        // Set Value
        folioU.setValue(folio);
        // Set dataType
        folioU.setType(String.class);
        // Add the property to request object
        request.addProperty(folioU);
        // Set folioPI
        tipoU.setName("tipo");
        // Set Value
        tipoU.setValue(tipo);
        // Set dataType
        tipoU.setType(String.class);
        // Add the property to request object
        request.addProperty(tipoU);
        // Set folioPI
        ubicacionU.setName("ubicacion");
        // Set Value
        ubicacionU.setValue(ubicacion);
        // Set dataType
        ubicacionU.setType(String.class);
        // Add the property to request objec
        request.addProperty(ubicacionU);
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,Timeout);
        //Log.d("Tiempo: ", "time: "+Timeout);
        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            // Assign it to  boolean variable variable
            Status = Boolean.parseBoolean(response.toString());

        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            MainActivity.errored = true;
            e.printStackTrace();
        }
        //Return booleam to calling object
        return Status;
    }

    public static boolean invokeProducto(String folio, String producto, String estado, String faltante, String comentario){
        boolean Status = false;
        String webMethName="UpdataProducto";
        // Create request
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        // Property which holds input parameters
        PropertyInfo folioP = new PropertyInfo();
        PropertyInfo productoP = new PropertyInfo();
        PropertyInfo estadoP = new PropertyInfo();
        PropertyInfo faltanteP = new PropertyInfo();
        PropertyInfo comentarioP = new PropertyInfo();
        // Set folioPI
        folioP.setName("folio");
        // Set Value
        folioP.setValue(folio);
        // Set dataType
        folioP.setType(String.class);
        // Add the property to request object
        request.addProperty(folioP);
        // Set folioPI
        productoP.setName("producto");
        // Set Value
        productoP.setValue(producto);
        // Set dataType
        productoP.setType(String.class);
        // Add the property to request object
        request.addProperty(productoP);
        // Set folioPI
        estadoP.setName("estado");
        // Set Value
        estadoP.setValue(estado);
        // Set dataType
        estadoP.setType(String.class);
        // Add the property to request objec
        request.addProperty(estadoP);
        // Set folioPI
        faltanteP.setName("faltante");
        // Set Value
        faltanteP.setValue(faltante);
        // Set dataType
        faltanteP.setType(String.class);
        // Add the property to request objec
        request.addProperty(faltanteP);
        // Set folioPI
        comentarioP.setName("comentario");
        // Set Value
        comentarioP.setValue(comentario);
        // Set dataType
        comentarioP.setType(String.class);
        // Add the property to request objec
        request.addProperty(comentarioP);
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,Timeout);
        //Log.d("Tiempo: ", "time: "+Timeout);
        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION+webMethName, envelope);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            // Assign it to  boolean variable variable
            Status = Boolean.parseBoolean(response.toString());

        } catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            MainActivity.errored = true;
            e.printStackTrace();
        }
        //Return booleam to calling object
        return Status;
    }
}
