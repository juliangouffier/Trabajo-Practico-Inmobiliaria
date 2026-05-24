package com.example.trabajopracticoinmobiliaria.ui.inmuebles;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleInmuebleViewModel extends AndroidViewModel {

    public static final String ARG_INMUEBLE = "inmueble";

    private final MutableLiveData<Inmueble> inmueble = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public DetalleInmuebleViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Inmueble> getInmueble() {
        return inmueble;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void setInmueble(@NonNull Inmueble inmuebleData) {
        inmueble.setValue(inmuebleData);
    }

    public void cambiarDisponibilidad() {
        Inmueble actual = inmueble.getValue();
        if (actual == null) {
            return;
        }
        if (actual.isTieneContratoVigente()) {
            mensajeError.setValue("No se puede cambiar la disponibilidad de un inmueble con contrato vigente");
            return;
        }

        boolean nuevaDisponibilidad = !actual.isDisponible();
        Inmueble payload = Inmueble.crearPayloadActualizacion(actual, nuevaDisponibilidad);
        String token = ApiClient.bearerToken(getApplication());

        ApiClient.getServicio().actualizarInmueble(token, payload).enqueue(new Callback<Inmueble>() {
            @Override
            public void onResponse(@NonNull Call<Inmueble> call, @NonNull Response<Inmueble> response) {
                if (response.isSuccessful()) {
                    actual.setDisponible(nuevaDisponibilidad);
                    inmueble.postValue(actual);
                } else {
                    String msg = "Error al actualizar disponibilidad";
                    if (response.errorBody() != null) {
                        try {
                            String body = response.errorBody().string();
                            if (body != null && !body.isEmpty()) {
                                msg = body;
                            }
                        } catch (IOException ignored) {
                        }
                    }
                    mensajeError.postValue(msg);
                    inmueble.postValue(actual);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Inmueble> call, @NonNull Throwable t) {
                mensajeError.postValue("Error de conexión: " + t.getMessage());
                inmueble.postValue(actual);
            }
        });
    }
}
