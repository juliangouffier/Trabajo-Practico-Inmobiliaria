package com.example.trabajopracticoinmobiliaria.data;

public interface AlmacenToken {

    void guardar(String tokenJwt);

    String obtener();

    void limpiar();

    boolean haySesion();
}
