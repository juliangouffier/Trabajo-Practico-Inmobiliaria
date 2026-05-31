package com.example.trabajopracticoinmobiliaria.ui.contratos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.modelo.Contrato;
import com.example.trabajopracticoinmobiliaria.data.modelo.Inmueble;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleContratoViewModel extends AndroidViewModel {

    public static final String ARG_INMUEBLE = "inmueble";

    private final MutableLiveData<Inmueble> inmueble = new MutableLiveData<>();
    private final MutableLiveData<Contrato> contrato = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public DetalleContratoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Inmueble> getInmueble() {
        return inmueble;
    }

    public LiveData<Contrato> getContrato() {
        return contrato;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void setInmueble(@NonNull Inmueble inmuebleData) {
        inmueble.setValue(inmuebleData);
    }

    public void cargarContrato() {
        Inmueble actual = inmueble.getValue();
        if (actual == null) {
            mensajeError.setValue("Inmueble no disponible");
            return;
        }

        cargando.setValue(true);
        String token = ApiClient.bearerToken(getApplication());

        ApiClient.getServicio().getContratoPorInmueble(token, actual.getIdInmueble())
                .enqueue(new Callback<Contrato>() {
                    @Override
                    public void onResponse(@NonNull Call<Contrato> call, @NonNull Response<Contrato> response) {
                        cargando.postValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            contrato.postValue(response.body());
                        } else {
                            mensajeError.postValue("Error al cargar contrato: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Contrato> call, @NonNull Throwable t) {
                        cargando.postValue(false);
                        mensajeError.postValue("Error de conexión: " + t.getMessage());
                    }
                });
    }
}
