package com.synappsis.carlos.apptunoni.entidades;

import java.util.UUID;

/**
 * Created by CARLOS on 15/12/2017.
 */

public class Comanda {
    interface ColumnasEntrega {
        String FOLIO = "folio";
        String ESTATUS = "estatus";
        String DIRORIGEN = "dirOrigen";
        String FECHAORIGEN = "fechaOrigen";
        String NOMBRE = "nombre";
        String DIRDESTINO = "dirDestino";
        String FECHADESTINO = "fechaDestino";
        String NOMBRERECEPTOR = "nombreReceptor";
        String INFO = "infoAdicional";
        String USUARIO_NOMBRE = "Usuario_nombre";
    }
    interface ColumnasUsuario {
        String NOMBRE = "nombre";
        String PASS = "pass";
    }
    interface ColumnasProducto{
        String IDPRODUCTO = "idProducto";
        String FALTANTE = "faltante";
        String CANTIDAD = "cantidad";
        String PRODUCTO = "producto";
        String ESTADO = "estado";
        String USUARIO_NOMBRE = "Usuario_nombre";
        String ENTREGA_FOLIO = "Entrega_folio";
    }
    interface ColumnasDocumentos {
        String IDDOCUMENTOS = "idDocumentos";
        String FOTO1 = "foto1";
        String FOTO2 = "foto2";
        String FOTO3 = "foto3";
        String FIRMA = "firma";
        String COMENTARIOS = "comentarios";
        String STATUS = "status";
        String USUARIO_NOMBRE = "Usuario_nombre";
        String ENTREGA_FOLIO = "Entrega_folio";
    }
    interface ColumnasApp {
        String FOLIO = "folio";
        String ESTATUS = "estatus";
        String ENVIO = "envio";
        String ACTUALIZAR = "actualizar";
    }

    public static class Entrega implements ColumnasEntrega {
        public static String generarIdEntrega() {
            return "E-" + UUID.randomUUID().toString();
        }
    }
    public static class Usuario implements ColumnasUsuario {
        public static String generarIdUsuario() {
            return "U-" + UUID.randomUUID().toString();
        }
    }
    public static class Producto implements ColumnasProducto {
        public static String generarIdProducto() {
            return "P-" + UUID.randomUUID().toString();
        }
    }
    public static class Documentos implements ColumnasDocumentos {
        public static String generarIdDocumentos() {
            return "D-" + UUID.randomUUID().toString();
        }
    }
    public static class App implements ColumnasApp {
        public static String generarIdApp() {
            return "A-" + UUID.randomUUID().toString();
        }
    }
    private Comanda() {
    }
}

