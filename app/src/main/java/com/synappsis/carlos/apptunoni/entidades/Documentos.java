package com.synappsis.carlos.apptunoni.entidades;

/**
 * Created by armando on 17/12/17.
 */

public class Documentos {
    public String iddocumentos = "iddocumentos";
    public String foto1 = "foto1";
    public String foto2 = "foto2";
    public String foto3 = "foto3";
    public String firma = "firma";
    public String comentarios = "comentarios";
    public String status = "status";
    public String usuario_nombre = "usuario_nombre";
    public String entrega_folio="entrega_folio";

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
    public Documentos()
    {

    }
}
