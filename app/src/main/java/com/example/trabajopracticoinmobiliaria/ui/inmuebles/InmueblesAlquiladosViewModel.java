package com.example.trabajopracticoinmobiliaria.ui.inmuebles;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InmueblesAlquiladosViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Inmueble>> inmueblesAlquilados = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public InmueblesAlquiladosViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Inmueble>> getInmueblesAlquilados() {
        return inmueblesAlquilados;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void cargarInmueblesAlquilados() {
        cargando.setValue(true);
        String token = ApiClient.bearerToken(getApplication());

        ApiClient.getServicio().getInmueblesAlquilados(token).enqueue(new Callback<List<Inmueble>>() {
            @Override
            public void onResponse(@NonNull Call<List<Inmueble>> call, @NonNull Response<List<Inmueble>> response) {
                cargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    inmueblesAlquilados.postValue(response.body());
                } else {
                    mensajeError.postValue("Error al cargar inmuebles alquilados: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Inmueble>> call, @NonNull Throwable t) {
                cargando.postValue(false);
                mensajeError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}
