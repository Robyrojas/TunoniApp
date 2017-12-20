package com.synappsis.carlos.apptunoni.entidades;

/**
 * Created by armando on 17/12/17.
 */

public class Producto {
    String idproducto;
    String faltante;
    int cantidad;
    String producto;
    String estado;
    String usuario_nombre;
    String entrega_folio;

    public Producto(String idproducto, String faltante, int cantidad, String producto, String estado, String usuario_nombre, String entrega_folio) {
        this.idproducto = idproducto;
        this.faltante = faltante;
        this.cantidad = cantidad;
        this.producto = producto;
        this.estado = estado;
        this.usuario_nombre = usuario_nombre;
        this.entrega_folio = entrega_folio;
    }
}
