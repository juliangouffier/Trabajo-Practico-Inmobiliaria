package com.example.trabajopracticoinmobiliaria.data.modelo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class Inmueble implements Serializable {

    private int idInmueble;
    private String direccion;
    private String uso;
    private String tipo;
    private int ambientes;
    private int superficie;
    private double latitud;
    private double longitud;
    private double valor;
    private String imagen;
    private boolean disponible;
    private int idPropietario;
    private Propietario duenio;
    private boolean tieneContratoVigente;

    public Inmueble() {
    }

    @NonNull
    public static Inmueble crearParaCarga(@NonNull String direccion, @NonNull String uso, @NonNull String tipo,
                                          int ambientes, int superficie, double latitud, double longitud,
                                          double valor, boolean disponible) {
        Inmueble inmueble = new Inmueble();
        inmueble.setDireccion(direccion);
        inmueble.setUso(uso);
        inmueble.setTipo(tipo);
        inmueble.setAmbientes(ambientes);
        inmueble.setSuperficie(superficie);
        inmueble.setLatitud(latitud);
        inmueble.setLongitud(longitud);
        inmueble.setValor(valor);
        inmueble.setDisponible(disponible);
        return inmueble;
    }

    @NonNull
    public JsonObject toJsonCarga() {
        JsonObject json = new JsonObject();
        json.addProperty("direccion", direccion);
        json.addProperty("uso", uso);
        json.addProperty("tipo", tipo);
        json.addProperty("ambientes", ambientes);
        json.addProperty("superficie", superficie);
        json.addProperty("latitud", latitud);
        json.addProperty("longitud", longitud);
        json.addProperty("valor", valor);
        json.addProperty("disponible", disponible);
        return json;
    }

    @NonNull
    public static Inmueble crearPayloadActualizacion(@NonNull Inmueble origen, boolean disponible) {
        Inmueble payload = new Inmueble();
        payload.idInmueble = origen.getIdInmueble();
        payload.direccion = origen.getDireccion();
        payload.uso = origen.getUso();
        payload.tipo = origen.getTipo();
        payload.ambientes = origen.getAmbientes();
        payload.superficie = origen.getSuperficie();
        payload.latitud = origen.getLatitud();
        payload.longitud = origen.getLongitud();
        payload.valor = origen.getValor();
        payload.imagen = origen.getImagen();
        payload.disponible = disponible;
        payload.tieneContratoVigente = origen.isTieneContratoVigente();
        return payload;
    }

    public int getIdInmueble() {
        return idInmueble;
    }

    public void setIdInmueble(int idInmueble) {
        this.idInmueble = idInmueble;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUso() {
        return uso;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getAmbientes() {
        return ambientes;
    }

    public void setAmbientes(int ambientes) {
        this.ambientes = ambientes;
    }

    public int getSuperficie() {
        return superficie;
    }

    public void setSuperficie(int superficie) {
        this.superficie = superficie;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    public Propietario getDuenio() {
        return duenio;
    }

    public void setDuenio(Propietario duenio) {
        this.duenio = duenio;
    }

    public boolean isTieneContratoVigente() {
        return tieneContratoVigente;
    }

    public void setTieneContratoVigente(boolean tieneContratoVigente) {
        this.tieneContratoVigente = tieneContratoVigente;
    }
}
