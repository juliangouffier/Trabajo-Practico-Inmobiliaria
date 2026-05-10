package com.example.trabajopracticoinmobiliaria.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.trabajopracticoinmobiliaria.ContenedorAplicacion;

public final class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final Application aplicacion;

    public LoginViewModelFactory(@NonNull Application aplicacion) {
        this.aplicacion = aplicacion;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            ContenedorAplicacion contenedor = (ContenedorAplicacion) aplicacion;
            return (T) new LoginViewModel(
                    aplicacion,
                    contenedor.obtenerPropietariosApiService(),
                    contenedor.obtenerExecutorServicio(),
                    contenedor.obtenerAlmacenToken());
        }
        throw new IllegalArgumentException("ViewModel desconocido: " + modelClass);
    }
}
