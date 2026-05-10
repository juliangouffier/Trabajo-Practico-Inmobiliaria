package com.example.trabajopracticoinmobiliaria.ui.logout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.trabajopracticoinmobiliaria.data.AlmacenToken;

public final class LogoutViewModel extends ViewModel {

    private final AlmacenToken almacenToken;

    public LogoutViewModel(@NonNull AlmacenToken almacenToken) {
        this.almacenToken = almacenToken;
    }

    public void limpiarSesion() {
        almacenToken.limpiar();
    }

    public static final class Factory implements ViewModelProvider.Factory {

        private final AlmacenToken almacenToken;

        public Factory(@NonNull AlmacenToken almacenToken) {
            this.almacenToken = almacenToken;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LogoutViewModel.class)) {
                return (T) new LogoutViewModel(almacenToken);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass);
        }
    }
}
