package com.example.trabajopracticoinmobiliaria.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.modelo.ResetCredencialesResponse;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarClaveViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensajeExito = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public RecuperarClaveViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<String> getMensajeExito() {
        return mensajeExito;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void resetearCredenciales() {
        cargando.setValue(true);
        ApiClient.getServicio().resetearCredenciales().enqueue(new Callback<ResetCredencialesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ResetCredencialesResponse> call,
                                   @NonNull Response<ResetCredencialesResponse> response) {
                cargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    String msg = response.body().getMessage();
                    if (msg == null || msg.isEmpty()) {
                        msg = "Contraseña reseteada. Use la clave inicial para ingresar.";
                    }
                    mensajeExito.postValue(msg + " Clave inicial: DEEKQW");
                } else {
                    mensajeError.postValue(leerError(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResetCredencialesResponse> call, @NonNull Throwable t) {
                cargando.postValue(false);
                mensajeError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    @NonNull
    private String leerError(@NonNull Response<?> response) {
        if (response.errorBody() != null) {
            try {
                String body = response.errorBody().string();
                if (body != null && !body.isEmpty()) {
                    return body;
                }
            } catch (IOException ignored) {
            }
        }
        return "No se pudo resetear la contraseña";
    }
}
