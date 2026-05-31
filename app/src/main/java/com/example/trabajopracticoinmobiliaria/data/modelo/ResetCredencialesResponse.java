package com.example.trabajopracticoinmobiliaria.data.modelo;

import java.io.Serializable;

public class ResetCredencialesResponse implements Serializable {

    private String message;
    private int idPropietario;
    private String email;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
