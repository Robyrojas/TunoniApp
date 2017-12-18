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

    public Producto(String idproducto, String faltante, int cantidad, String producto, String estado, String usuario_nombre) {
        this.idproducto = idproducto;
        this.faltante = faltante;
        this.cantidad = cantidad;
        this.producto = producto;
        this.estado = estado;
        this.usuario_nombre = usuario_nombre;
    }
}
