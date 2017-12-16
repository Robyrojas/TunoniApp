package com.synappsis.carlos.apptunoni.entidades;

/**
 * Created by CARLOS on 14/12/2017.
 */

public class Utilidades {
    public static final String TABLA_TUNONI = "tunoni";
    public static final String campo_usuario = "usuario";
    public static final String campo_pass = "pass";
    public static final String campo_folio = "folio";
    public static final String campo_estatus = "estatus";
    public static final String campo_origen = "origen";
    public static final String campo_fechaOrigen = "fechaOrigen";
    public static final String campo_nombre = "nombre";
    public static final String campo_destino = "destino";
    public static final String campo_fechaDestino = "fechaDestino";
    public static final String campo_nombreReceptor = "nombreReceptor";
    public static final String campo_infoAdicional = "infoAdicional";
    public static final String campo_foto = "foto";
    public static final String campo_firma = "firma";
    //public final String campo_ = "tunoni";

    public static final String CREAR_TABLA_TUNONI = "CREATE TABLE "+TABLA_TUNONI + "(usuario TEXT,\n" +
            "contraseña TEXT,\n" +
            "folio TEXT,\n" +
            "estatus TEXT,\n" +
            "origen TEXT,\n" +
            "fechaOrigen TEXT,\n" +
            "nombre TEXT,\n" +
            "destino TEXT,\n" +
            "fechaDestino TEXT,\n" +
            "nombreReceptor TEXT,\n" +
            "infoAdicional TEXT,\n" +
            "foto TEXT,\n" +
            "firma TEXT)";
}
