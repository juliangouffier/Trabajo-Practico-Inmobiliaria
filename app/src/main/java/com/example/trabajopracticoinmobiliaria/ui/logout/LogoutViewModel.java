package com.example.trabajopracticoinmobiliaria.ui.logout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

public final class LogoutViewModel extends AndroidViewModel {

    public LogoutViewModel(@NonNull Application application) {
        super(application);
    }

    public void limpiarSesion() {
        ApiClient.borrarToken(getApplication());
    }
}
