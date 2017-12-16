package com.synappsis.carlos.apptunoni.entidades;

/**
 * Created by CARLOS on 14/12/2017.
 */

public class Tunoni {
    private String usuario;
    private String pass;
    private String folio;
    private String estatus;
    private String origen;
    private String fechaOrigen;
    private String nombre;
    private String destino;
    private String fechaDestino;
    private String nombreReceptor;
    private String infoAdicional;
    private String foto;
    private String firma;

    public Tunoni(String usuario, String pass, String folio, String estatus, String origen, String fechaOrigen, String nombre, String destino, String fechaDestino, String nombreReceptor, String infoAdicional, String foto, String firma) {
        this.usuario = usuario;
        this.pass = pass;
        this.folio = folio;
        this.estatus = estatus;
        this.origen = origen;
        this.fechaOrigen = fechaOrigen;
        this.nombre = nombre;
        this.destino = destino;
        this.fechaDestino = fechaDestino;
        this.nombreReceptor = nombreReceptor;
        this.infoAdicional = infoAdicional;
        this.foto = foto;
        this.firma = firma;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return pass;
    }

    public void setContraseña(String pass) {
        this.pass = pass;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getFechaOrigen() {
        return fechaOrigen;
    }

    public void setFechaOrigen(String fechaOrigen) {
        this.fechaOrigen = fechaOrigen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFechaDestino() {
        return fechaDestino;
    }

    public void setFechaDestino(String fechaDestino) {
        this.fechaDestino = fechaDestino;
    }

    public String getNombreReceptor() {
        return nombreReceptor;
    }

    public void setNombreReceptor(String nombreReceptor) {
        this.nombreReceptor = nombreReceptor;
    }

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }
}

