package com.example.trabajopracticoinmobiliaria.ui.contratos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.modelo.Pago;
import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagosContratoViewModel extends AndroidViewModel {

    public static final String ARG_ID_CONTRATO = "idContrato";
    public static final String ARG_DIRECCION = "direccion";

    private final MutableLiveData<List<Pago>> pagos = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public PagosContratoViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Pago>> getPagos() {
        return pagos;
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void cargarPagos(int idContrato) {
        cargando.setValue(true);
        String token = ApiClient.bearerToken(getApplication());

        ApiClient.getServicio().getPagosPorContrato(token, idContrato).enqueue(new Callback<List<Pago>>() {
            @Override
            public void onResponse(@NonNull Call<List<Pago>> call, @NonNull Response<List<Pago>> response) {
                cargando.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    pagos.postValue(response.body());
                } else {
                    mensajeError.postValue("Error al cargar pagos: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Pago>> call, @NonNull Throwable t) {
                cargando.postValue(false);
                mensajeError.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}
