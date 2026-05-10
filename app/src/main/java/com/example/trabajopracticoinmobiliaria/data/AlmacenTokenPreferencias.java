package com.example.trabajopracticoinmobiliaria.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public final class AlmacenTokenPreferencias implements AlmacenToken {

    private static final String ARCHIVO = "sesion_propietario";
    private static final String CLAVE_JWT = "jwt";

    private final SharedPreferences preferencias;

    public AlmacenTokenPreferencias(@NonNull Context contexto) {
        preferencias = contexto.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE);
    }

    @Override
    public void guardar(String tokenJwt) {
        preferencias.edit().putString(CLAVE_JWT, tokenJwt).apply();
    }

    @Override
    public String obtener() {
        return preferencias.getString(CLAVE_JWT, null);
    }

    @Override
    public void limpiar() {
        preferencias.edit().remove(CLAVE_JWT).apply();
    }

    @Override
    public boolean haySesion() {
        String token = obtener();
        return token != null && !token.isEmpty();
    }
}
