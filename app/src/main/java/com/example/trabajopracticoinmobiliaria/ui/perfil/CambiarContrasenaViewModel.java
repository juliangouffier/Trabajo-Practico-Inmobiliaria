package com.example.trabajopracticoinmobiliaria.ui.perfil;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trabajopracticoinmobiliaria.data.remote.ApiClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarContrasenaViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> cambioExitoso = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);

    public CambiarContrasenaViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getCambioExitoso() { return cambioExitoso; }
    public LiveData<String> getMensajeError() { return mensajeError; }
    public LiveData<Boolean> getCargando() { return cargando; }

    public void cambiarContrasena(@NonNull String claveActual, @NonNull String claveNueva) {
        if (claveActual.isEmpty()) {
            mensajeError.setValue("Ingrese la contraseña actual");
            return;
        }
        if (claveNueva.isEmpty()) {
            mensajeError.setValue("Ingrese la nueva contraseña");
            return;
        }
        if (claveNueva.equals(claveActual)) {
            mensajeError.setValue("La nueva contraseña debe ser diferente a la actual");
            return;
        }

        cargando.setValue(true);
        String token = ApiClient.bearerToken(getApplication());

        ApiClient.getServicio().cambiarContrasena(token, claveActual, claveNueva)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        cargando.postValue(false);
                        if (response.isSuccessful()) {
                            cambioExitoso.postValue(true);
                        } else {
                            String msg = "Error al cambiar contraseña";
                            if (response.errorBody() != null) {
                                try {
                                    String body = response.errorBody().string();
                                    if (body != null && !body.isEmpty()) msg = body;
                                } catch (IOException ignored) {}
                            }
                            mensajeError.postValue(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        cargando.postValue(false);
                        mensajeError.postValue("Error de conexión: " + t.getMessage());
                    }
                });
    }
}
