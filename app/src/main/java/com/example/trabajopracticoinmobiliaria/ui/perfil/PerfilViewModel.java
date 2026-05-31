package com.example.trabajopracticoinmobiliaria.ui.perfil;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.modelo.Propietario;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {

    private final MutableLiveData<Propietario> propietarioMutable = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actualizacionExitosa = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public PerfilViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Propietario> getPropietarioMutable() {
        return propietarioMutable;
    }

    public LiveData<Boolean> getActualizacionExitosa() {
        return actualizacionExitosa;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void cargarPerfil() {
        String token = ApiClient.bearerToken(getApplication());
        ApiClient.getServicio().getPropietario(token).enqueue(new Callback<Propietario>() {
            @Override
            public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                if (response.isSuccessful()) {
                    propietarioMutable.postValue(response.body());
                } else {
                    mensajeError.postValue("Error al cargar perfil: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Propietario> call, Throwable t) {
                mensajeError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void actualizarPerfil(@NonNull Propietario propietario) {
        String token = ApiClient.bearerToken(getApplication());
        ApiClient.getServicio().actualizarPropietario(token, propietario).enqueue(new Callback<Propietario>() {
            @Override
            public void onResponse(Call<Propietario> call, Response<Propietario> response) {
                if (response.isSuccessful()) {
                    propietarioMutable.postValue(response.body());
                    actualizacionExitosa.postValue(true);
                } else {
                    mensajeError.postValue("Error al guardar: " + response.message());
                    actualizacionExitosa.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Propietario> call, Throwable t) {
                mensajeError.postValue("Error de conexión: " + t.getMessage());
                actualizacionExitosa.postValue(false);
            }
        });
    }
}
