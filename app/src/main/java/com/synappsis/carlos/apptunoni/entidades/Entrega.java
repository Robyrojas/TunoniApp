package com.synappsis.carlos.apptunoni.entidades;

/**
 * Created by armando on 17/12/17.
 */

public class Entrega {
    public String folio;
    public String estatus;
    public String dirorigen;
    public String fechaorigen;
    public String nombre;
    public String dirdestino;
    public String fechadestino;
    public String nombrereceptor;
    public String info;
    public String usuario_nombre;

    public Entrega(String folio, String estatus, String dirorigen, String fechaorigen, String nombre, String dirdestino, String fechadestino, String nombrereceptor, String info, String usuario_nombre) {
        this.folio = folio;
        this.estatus = estatus;
        this.dirorigen = dirorigen;
        this.fechaorigen = fechaorigen;
        this.nombre = nombre;
        this.dirdestino = dirdestino;
        this.fechadestino = fechadestino;
        this.nombrereceptor = nombrereceptor;
        this.info = info;
        this.usuario_nombre = usuario_nombre;
    }
}
