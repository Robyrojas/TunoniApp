package com.synappsis.carlos.apptunoni.entidades;

/**
 * Created by armando on 17/12/17.
 */

public class Documentos {
    String iddocumentos = "iddocumentos";
    String foto1 = "foto1";
    String foto2 = "foto2";
    String foto3 = "foto3";
    String firma = "firma";
    String comentarios = "comentarios";
    String status = "status";
    String usuario_nombre = "usuario_nombre";
    String entrega_folio="entrega_folio";

    public Documentos(String iddocumentos, String foto1, String foto2, String foto3, String firma, String comentarios, String status, String usuario_nombre, String entrega_folio) {
        this.iddocumentos = iddocumentos;
        this.foto1 = foto1;
        this.foto2 = foto2;
        this.foto3 = foto3;
        this.firma = firma;
        this.comentarios = comentarios;
        this.status = status;
        this.usuario_nombre = usuario_nombre;
        this.entrega_folio = entrega_folio;
    }
}
